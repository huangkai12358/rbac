package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.RoleCreateDTO;
import com.ymjrhk.rbac.dto.RoleDTO;
import com.ymjrhk.rbac.dto.RolePageQueryDTO;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.exception.RoleCreateFailedException;
import com.ymjrhk.rbac.exception.RoleForbiddenException;
import com.ymjrhk.rbac.exception.RoleNotExistException;
import com.ymjrhk.rbac.exception.UpdateFailedException;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.RoleHistoryService;
import com.ymjrhk.rbac.service.RoleService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.RoleExcelVO;
import com.ymjrhk.rbac.vo.RoleVO;
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
public class RoleServiceImpl extends BaseService implements RoleService {
    private final RoleMapper roleMapper;

    private final RoleHistoryService roleHistoryService;

    /**
     * 创建角色
     *
     * @param roleCreateDTO
     */
    @Override
    @Transactional
    public Long create(RoleCreateDTO roleCreateDTO) {
        Role role = BeanUtil.copyProperties(roleCreateDTO, Role.class);

        String secretToken = UUID.randomUUID().toString();

        // 查询当前用户id
        Long userId = UserContext.getCurrentUserId();

        role.setSecretToken(secretToken);
        role.setCreateUserId(userId);
        role.setUpdateUserId(userId);

        int result = roleMapper.insert(role);
        if (result != 1) {
            throw new RoleCreateFailedException(ROLE_CREATE_FAILED); // 创建角色失败
        }

        // 写到历史表
        roleHistoryService.recordHistory(role.getRoleId(), OperateTypeConstant.CREATE);

        return role.getRoleId();
    }

    /**
     * 角色分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(RolePageQueryDTO dto) {

        // 1. 分页兜底
        normalizePage(dto);

        // 2. 查总数
        long total = roleMapper.count(dto);
        if (total == 0) {
            return new PageResult<>(0, List.of());
        }

        // 3. 构造安全 orderBy
        String orderBy = buildOrderBy(dto);
        dto.setOrderBy(orderBy);

        // 4. 第一段：只查 role_id（有序）
        List<Long> ids = roleMapper.pageQueryIds(dto);
        if (ids.isEmpty()) {
            return new PageResult<>(total, List.of());
        }

        // 5. 第二段：按 ID 查完整数据（顺序保证）
        List<Role> roles = roleMapper.selectByIds(ids);

        // 6. 转 VO
        List<RoleVO> records = roles.stream()
                                    .map(r -> BeanUtil.copyProperties(r, RoleVO.class))
                                    .toList();

        return new PageResult(total, records);
    }


    /**
     * 排序构建方法（排序字段白名单）
     *
     * @param dto
     * @return
     */
    private String buildOrderBy(RolePageQueryDTO dto) {

        // 默认排序（当用户没点排序时）
        String defaultOrder = "create_time desc";

        if (CharSequenceUtil.isBlank(dto.getSortField()) || CharSequenceUtil.isBlank(dto.getSortOrder())) {
            return defaultOrder;
        }

        // 允许排序的字段（数据库字段）
        Set<String> allowedFields = Set.of(
                "role_id",
                "role_name",
                "create_time",
                "status"
        );

        // 前端字段 → 数据库字段映射
        Map<String, String> fieldMap = Map.of(
                "roleId", "role_id",
                "roleName", "role_name",
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
     * 根据 roleId 查询角色
     *
     * @param roleId
     * @return
     */
    @Override
    @Cacheable(
            cacheNames = ROLE_BASIC,
            key = "#roleId"
    )
    public RoleVO getByRoleId(Long roleId) {
        Role role = roleMapper.getByRoleId(roleId);

        // 角色不存在
        if (role == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        return BeanUtil.copyProperties(role, RoleVO.class);
    }

    /**
     * 修改角色
     *
     * @param roleId
     * @param roleDTO
     */
    @Override
    @Transactional
    @Caching(evict = { // 可以修改 roleName
            @CacheEvict(
                    cacheNames = ROLE_BASIC,
                    key = "#roleId"
            ),
            @CacheEvict(
                    cacheNames = {
                            USER_ROLES,
                            USER_ME
                    },
                    allEntries = true
            )
    }
    )
    public void update(Long roleId, RoleDTO roleDTO) {
        log.debug("获取更新前必要字段（包括乐观锁字段）：");
        Role dbRole = roleMapper.getByRoleId(roleId);

        // 角色不存在
        if (dbRole == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        // 角色被禁用，不能修改
        if (Objects.equals(dbRole.getStatus(), DISABLED)) {
            throw new RoleForbiddenException(ROLE_FORBIDDEN);
        }

        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleDisplayName(roleDTO.getRoleDisplayName());
        role.setDescription(roleDTO.getDescription());

        Integer version = roleDTO.getVersion(); // 获取前端保存的版本号
        String secretToken = roleDTO.getSecretToken(); // 获取前端保存的旧 secretToken
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = UserContext.getCurrentUserId();

        fillOptimisticLockFields(role, version, secretToken, newSecretToken, updateUserId);

        doUpdate(role);

        // 写到历史表
        roleHistoryService.recordHistory(role.getRoleId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 启用或禁用角色
     *
     * @param roleId
     * @param status
     */
    @Override
    @Transactional
    @Caching(evict = { // 可以修改 status
            @CacheEvict(
                    cacheNames = ROLE_BASIC,
                    key = "#roleId"
            ),
            @CacheEvict(
                    cacheNames = {
                            USER_ROLES,
                            USER_ME
                    },
                    allEntries = true
            )
    }
    )
    public void changeStatus(Long roleId, Integer status) {
        // 1. 查数据库
        log.debug("获取更新前必要字段（包括乐观锁字段）：");
        Role dbRole = roleMapper.getByRoleId(roleId);
        if (dbRole == null) { // 角色不存在
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }
        // 2. 构造“更新用实体”（只放必要字段）
        Role role = new Role();
        role.setRoleId(roleId);

        // 3. 调 BaseService 的模板方法
        changeStatus(dbRole, role, status);

        // 4. 执行 update
        doUpdate(role);

        // 5. 写到历史表
        roleHistoryService.recordHistory(role.getRoleId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 导出角色数据
     *
     * @param dto
     * @return
     */
    @Override
    public List<RoleExcelVO> listForExport(RolePageQueryDTO dto) {
        // 动态排序：跟随页面排序
        dto.setOrderBy(buildOrderBy(dto));

        List<RoleVO> list = roleMapper.listForExport(dto);

        return list.stream().map(roleVO -> {
            RoleExcelVO vo = new RoleExcelVO();
            vo.setRoleId(roleVO.getRoleId());
            vo.setRoleName(roleVO.getRoleName());
            vo.setRoleDisplayName(roleVO.getRoleDisplayName());
            vo.setDescription(roleVO.getDescription());
            vo.setStatus(roleVO.getStatus() == 1 ? "启用" : "禁用");
            vo.setCreateTime(roleVO.getCreateTime());
            return vo;
        }).toList();
    }

    /**
     * 公共的调用 mapper 的 update() 方法
     *
     * @param role
     */
    private void doUpdate(Role role) {
        int result = roleMapper.update(role);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }
    }
}
