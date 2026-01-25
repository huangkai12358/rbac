package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
import com.ymjrhk.rbac.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        permissionHistoryService.record(permission.getPermissionId(), OperateTypeConstant.CREATE);

        return permission.getPermissionId();
    }

    /**
     * 权限分页查询
     *
     * @param permissionPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(PermissionPageQueryDTO permissionPageQueryDTO) {
        normalizePage(permissionPageQueryDTO);

        PageHelper.startPage(permissionPageQueryDTO.getPageNum(), permissionPageQueryDTO.getPageSize());

        Page<Permission> page = permissionMapper.pageQuery(permissionPageQueryDTO);

        long total = page.getTotal();

        List<PermissionVO> records = page.getResult().stream()
                                         .map(permission -> BeanUtil.copyProperties(permission, PermissionVO.class))
                                         .toList();

        return new PageResult(total, records);
    }

    /**
     * 根据 permissionId 查询权限
     *
     * @param permissionId
     * @return
     */
    @Override
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
        permissionHistoryService.record(permission.getPermissionId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 启用或禁用权限
     *
     * @param permissionId
     * @param status
     */
    @Override
    @Transactional
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
        permissionHistoryService.record(permission.getPermissionId(), OperateTypeConstant.UPDATE);
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
