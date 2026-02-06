package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.PermissionCreateDTO;
import com.ymjrhk.rbac.dto.PermissionDTO;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.entity.Permission;
import com.ymjrhk.rbac.exception.PermissionCreateFailedException;
import com.ymjrhk.rbac.exception.PermissionForbiddenException;
import com.ymjrhk.rbac.exception.PermissionNotExistException;
import com.ymjrhk.rbac.exception.UpdateFailedException;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.PermissionHistoryService;
import com.ymjrhk.rbac.service.PermissionService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.PermissionExcelVO;
import com.ymjrhk.rbac.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ymjrhk.rbac.constant.CacheConstant.*;
import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl extends BaseService implements PermissionService {
    private final PermissionMapper permissionMapper;

    private final PermissionHistoryService permissionHistoryService;

    /**
     * 创建权限
     *
     * @param permissionCreateDTO
     */
    @Override
    @Transactional
    @CacheEvict(
            // 只担心超级管理员，因为会自动获取全部权限
            cacheNames = {
                    USER_PERMISSIONS,
                    USER_ME,
                    ROLE_PERMISSIONS
            },
            allEntries = true
    )
    public Long create(PermissionCreateDTO permissionCreateDTO) {
        Permission permission = BeanUtil.copyProperties(permissionCreateDTO, Permission.class);

        String secretToken = UUID.randomUUID().toString();

        // 查询当前用户id
        Long userId = UserContext.getCurrentUserId();

        permission.setSecretToken(secretToken);
        permission.setCreateUserId(userId);
        permission.setUpdateUserId(userId);

        int result = permissionMapper.insert(permission);
        if (result != 1) {
            throw new PermissionCreateFailedException(PERMISSION_CREATE_FAILED); // 创建权限失败
        }

        // 写到历史表
        permissionHistoryService.recordHistory(permission.getPermissionId(), OperateTypeConstant.CREATE);

        return permission.getPermissionId();
    }

    /**
     * 权限分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(PermissionPageQueryDTO dto) {

        // 1. 分页兜底
        normalizePage(dto);

        // 2. 查总数
        long total = permissionMapper.count(dto);
        if (total == 0) {
            return new PageResult<>(0, List.of());
        }

        // 3. 构造安全 orderBy（排序字段白名单）
        String orderBy = buildOrderBy(dto);
        dto.setOrderBy(orderBy);

        // 4. 第一段：只查 permission_id（有序）
        List<Long> ids = permissionMapper.pageQueryIds(dto);
        if (ids.isEmpty()) {
            return new PageResult<>(total, List.of());
        }

        // 5. 第二段：按 ID 查完整数据（顺序保证）
        List<Permission> permissions = permissionMapper.selectByIds(ids);

        // 6. 转 VO
        List<PermissionVO> records = permissions.stream()
                                                .map(p -> BeanUtil.copyProperties(p, PermissionVO.class))
                                                .toList();

        return new PageResult(total, records);
    }

/*    @Override
    public PageResult pageQuery(PermissionPageQueryDTO permissionPageQueryDTO) {
        normalizePage(permissionPageQueryDTO); // pageNum 和 pageSize 设置默认值兜底

        // 加 limit
        PageMethod.startPage(permissionPageQueryDTO.getPageNum(), permissionPageQueryDTO.getPageSize());

        // 加 order by
        String orderBy = buildOrderBy(permissionPageQueryDTO); // 排序字段白名单
        PageMethod.orderBy(orderBy);

        // 正式分页（会被 PageHelper 拦截器拦截并加参数）
        Page<Permission> page = permissionMapper.pageQuery(permissionPageQueryDTO);

        long total = page.getTotal();

        List<PermissionVO> records = page.getResult().stream()
                                         .map(permission -> BeanUtil.copyProperties(permission, PermissionVO.class))
                                         .toList();

        return new PageResult(total, records);
    }*/

    /**
     * 排序构建方法（排序字段白名单）
     *
     * @param dto
     * @return
     */
    private String buildOrderBy(PermissionPageQueryDTO dto) {

        // 默认排序（当用户没点排序时）
        String defaultOrder = "create_time desc";

        if (CharSequenceUtil.isBlank(dto.getSortField()) || CharSequenceUtil.isBlank(dto.getSortOrder())) {
            return defaultOrder;
        }

        // 允许排序的字段（数据库字段）
        Set<String> allowedFields = Set.of(
                "permission_id",
                "permission_name",
                "type",
                "path",
                "method",
                "create_time",
                "status"
        );

        // 前端字段 → 数据库字段映射
        Map<String, String> fieldMap = Map.of(
                "permissionId", "permission_id",
                "permissionName", "permission_name",
                "type", "type",
                "path", "path",
                "method", "method",
                "createTime", "create_time",
                "status", "status"
        );

        String column = fieldMap.get(dto.getSortField());
        if (column == null || !allowedFields.contains(column)) {
            return defaultOrder;
        }

        String order = dto.getSortOrder().equalsIgnoreCase("asc") ? "asc" : "desc";

        return column + " " + order;
    }

    /**
     * 根据 permissionId 查询权限
     *
     * @param permissionId
     * @return
     */
    @Override
    @Cacheable(
            cacheNames = PERMISSION_BASIC,
            key = "#permissionId"
    )
    public PermissionVO getByPermissionId(Long permissionId) {
        Permission permission = permissionMapper.getByPermissionId(permissionId);

        // 权限不存在
        if (permission == null) {
            throw new PermissionNotExistException(PERMISSION_NOT_EXIST);
        }

        return BeanUtil.copyProperties(permission, PermissionVO.class);
    }

    /**
     * 修改权限
     *
     * @param permissionId
     * @param permissionDTO
     */
    @Override
    @Transactional
    @Caching(evict = { // 可以修改 permissionName
            @CacheEvict(
                    cacheNames = PERMISSION_BASIC,
                    key = "#permissionId"
            ),
            @CacheEvict(
                    cacheNames = {
                            USER_PERMISSIONS,
                            ROLE_PERMISSIONS,
                            USER_ME
                    },
                    allEntries = true
            )
    }
    )
    public void update(Long permissionId, PermissionDTO permissionDTO) {
        log.debug("获取更新前必要字段（包括乐观锁字段）：");
        Permission dbPermission = permissionMapper.getByPermissionId(permissionId);

        // 权限不存在
        if (dbPermission == null) {
            throw new PermissionNotExistException(PERMISSION_NOT_EXIST);
        }

        // 权限被禁用，不能修改
        if (Objects.equals(dbPermission.getStatus(), DISABLED)) {
            throw new PermissionForbiddenException(PERMISSION_FORBIDDEN);
        }

        Permission permission = getPermission(permissionDTO); // 从 PermissionDTO 拷贝属性到 Permission
        permission.setPermissionId(permissionId);

        Integer version = permissionDTO.getVersion(); // 获取前端保存的版本号
        String secretToken = permissionDTO.getSecretToken(); // 获取前端保存的旧 secretToken
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = UserContext.getCurrentUserId();

        fillOptimisticLockFields(permission, version, secretToken, newSecretToken, updateUserId);

        doUpdate(permission);

        // 写到历史表
        permissionHistoryService.recordHistory(permission.getPermissionId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 启用或禁用权限
     *
     * @param permissionId
     * @param status
     */
    @Override
    @Transactional
    @Caching(evict = { // 可以修改 status
            @CacheEvict(
                    cacheNames = PERMISSION_BASIC,
                    key = "#permissionId"
            ),
            @CacheEvict(
                    cacheNames = {
                            USER_PERMISSIONS,
                            ROLE_PERMISSIONS,
                            USER_ME
                    },
                    allEntries = true
            )
    }
    )
    public void changeStatus(Long permissionId, Integer status) {
        // 1. 查数据库
        log.debug("获取更新前必要字段（包括乐观锁字段）：");
        Permission dbPermission = permissionMapper.getByPermissionId(permissionId);
        if (dbPermission == null) { // 权限不存在
            throw new PermissionNotExistException(PERMISSION_NOT_EXIST);
        }
        // 2. 构造“更新用实体”（只放必要字段）
        Permission permission = new Permission();
        permission.setPermissionId(permissionId);

        // 3. 调 BaseService 的模板方法
        changeStatus(dbPermission, permission, status);

        // 4. 执行 update
        doUpdate(permission);

        // 写到历史表
        permissionHistoryService.recordHistory(permission.getPermissionId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 导出权限数据
     *
     * @param dto
     * @return
     */
    @Override
    public List<PermissionExcelVO> listForExport(PermissionPageQueryDTO dto) {
        // 动态排序：跟随页面排序
        dto.setOrderBy(buildOrderBy(dto));

        List<PermissionVO> list = permissionMapper.listForExport(dto);

        return list.stream().map(permissionVO -> {
            PermissionExcelVO vo = new PermissionExcelVO();
            vo.setPermissionId(permissionVO.getPermissionId());
            vo.setPermissionName(permissionVO.getPermissionName());
            vo.setPermissionDisplayName(permissionVO.getPermissionDisplayName());
            vo.setType(permissionVO.getType());
            vo.setPath(permissionVO.getPath());
            vo.setMethod(permissionVO.getMethod());
            vo.setStatus(permissionVO.getStatus() == 1 ? "启用" : "禁用");
            vo.setCreateTime(permissionVO.getCreateTime());
            return vo;
        }).toList();
    }

    /**
     * 公共的调用 mapper 的 update() 方法
     *
     * @param permission
     */
    private void doUpdate(Permission permission) {
        int result = permissionMapper.update(permission);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }
    }

    /**
     * 从 PermissionDTO 拷贝属性到 Permission
     *
     * @param permissionDTO
     * @return
     */
    public static Permission getPermission(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionDisplayName(permissionDTO.getPermissionDisplayName());
        permission.setDescription(permissionDTO.getDescription());

        permission.setType(permissionDTO.getType());
        permission.setParentId(permissionDTO.getParentId());
        permission.setPath(permissionDTO.getPath());
        permission.setMethod(permissionDTO.getMethod());
        permission.setSort(permissionDTO.getSort());
        return permission;
    }
}
