package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.MePasswordUpdateDTO;
import com.ymjrhk.rbac.dto.MeUpdateDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.PasswordErrorException;
import com.ymjrhk.rbac.exception.UpdateFailedException;
import com.ymjrhk.rbac.exception.UserForbiddenException;
import com.ymjrhk.rbac.exception.UserNotExistException;
import com.ymjrhk.rbac.mapper.MeMapper;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.MeService;
import com.ymjrhk.rbac.service.UserHistoryService;
import com.ymjrhk.rbac.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.ymjrhk.rbac.constant.CacheConstant.*;
import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.RoleNameConstant.SUPER_ADMIN;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeServiceImpl implements MeService {
    private final MeMapper meMapper;

    private final UserMapper userMapper;

    private final UserHistoryService userHistoryService;

    private final PasswordEncoder passwordEncoder;

    private final PermissionMapper permissionMapper;

    /**
     * 查询个人信息
     *
     * @return
     */
    @Override
    @Cacheable(
            cacheNames = USER_ME,   // 新 cache
            key = "T(com.ymjrhk.rbac.context.UserContext).getCurrentUserId()"
    )
    public MeViewVO query() {
        Long userId = UserContext.getCurrentUserId();

        // 1. 基本信息
        MeViewVO meViewVO = meMapper.getByUserId(userId);
        // 如果 userId 不存在（冗余设计，/me 是认证后接口，AuthInterceptor 已经校验，可删）
        if (meViewVO == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 角色
        List<MeRoleVO> roles = meMapper.selectRolesByUserId(userId);
        meViewVO.setRoles(roles);

        // 3. 权限
        // 看拥有角色有没有超级管理员
        for (MeRoleVO role : roles) {
            if (Objects.equals(role.getRoleName(), SUPER_ADMIN)) {
                List<PermissionVO> list = permissionMapper.listAllActivePermissions();

                // List 转类型
                List<MePermissionVO> permissions = list
                        .stream()
                        .map(permissionVO -> BeanUtil.copyProperties(permissionVO, MePermissionVO.class))
                        .toList();

                meViewVO.setPermissions(permissions);

                return meViewVO;
            }
        }

        // 拥有角色没有超级管理员
        List<MePermissionVO> permissions = meMapper.selectPermissionsByUserId(userId);
        meViewVO.setPermissions(permissions);

        return meViewVO;
    }

    /**
     * 修改个人信息
     *
     * @param meUpdateDTO
     */
    @Override
    @Transactional
    @CacheEvict( // 暂时不能修改 username，所以不用加 USER_AUTH
            cacheNames = {
                    USER_ME,
                    USER_BASIC
            },
            key = "T(com.ymjrhk.rbac.context.UserContext).getCurrentUserId()"
    )
    public void update(MeUpdateDTO meUpdateDTO) {
        Long userId = UserContext.getCurrentUserId();

        User dbUser = userMapper.getByUserId(userId);

        // 用户不存在
        if (dbUser == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 用户被禁用，不能修改
        if (Objects.equals(dbUser.getStatus(), DISABLED)) {
            throw new UserForbiddenException(USER_FORBIDDEN);
        }

        User user = new User();
        user.setUserId(userId);
        user.setNickname(meUpdateDTO.getNickname());
        user.setEmail(meUpdateDTO.getEmail());

        user.setVersion(dbUser.getVersion());
        user.setSecretToken(dbUser.getSecretToken());
        user.setNewSecretToken(UUID.randomUUID().toString());
        user.setUpdateUserId(userId); // 当前用户在更新自己

        int result = userMapper.update(user);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }

        // 写到历史表
        userHistoryService.recordHistory(user.getUserId(), OperateTypeConstant.UPDATE);
    }

    /**
     * 修改个人密码
     *
     * @param mePasswordUpdateDTO
     */
    @Override
    @Transactional
    @CacheEvict(
            cacheNames = {
                    USER_ME,
                    USER_BASIC,
                    USER_AUTH    // 强制 JWT 失效
            },
            key = "T(com.ymjrhk.rbac.context.UserContext).getCurrentUserId()"
    )
    public void changePassword(MePasswordUpdateDTO mePasswordUpdateDTO) {
        Long userId = UserContext.getCurrentUserId();

        String oldPassword = mePasswordUpdateDTO.getOldPassword();
        String newPassword = mePasswordUpdateDTO.getNewPassword();

        // 1. 根据 userId 查数据库中数据
        User dbUser = userMapper.getByUserId(userId);

        // 2. 处理各种异常情况（用户不存在、密码错误、账号被禁用）
        // 2.1 如果用户不存在
        if (dbUser == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2.2 如果密码错误
        String peppered = oldPassword + "#" + userId;
        if (!passwordEncoder.matches(peppered, dbUser.getPassword())) {
            throw new PasswordErrorException(PASSWORD_ERROR);
        }

        // 2.3 如果账号被禁用
        if (Objects.equals(dbUser.getStatus(), StatusConstant.DISABLED)) {
            throw new UserForbiddenException(MessageConstant.USER_FORBIDDEN);
        }

        // 3. 修改密码
        User user = new User();
        user.setUserId(userId);

        // 设置新密码
        peppered = newPassword + "#" + userId;
        String encodedPassword = passwordEncoder.encode(peppered);
        user.setPassword(encodedPassword);

        user.setVersion(dbUser.getVersion());
        user.setSecretToken(dbUser.getSecretToken());
        user.setNewSecretToken(UUID.randomUUID().toString());
        user.setUpdateUserId(userId); // 当前用户在更新自己

        // 获取登录版本号（其实这里随便填什么，只要不为 null 就可以触发 xml 中 auth_version + 1）
        // 用于触发 auth_version + 1（强制下线），通过 authVersion + 1 使已有 jwt 失效
        Integer authVersion = dbUser.getAuthVersion();
        user.setAuthVersion(authVersion);

        int result = userMapper.update(user);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }

        // 4. 写到历史表
        userHistoryService.recordHistory(user.getUserId(), OperateTypeConstant.UPDATE);
    }
}
