package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.PermissionTypeConstant;
import com.ymjrhk.rbac.constant.RoleNameConstant;
import com.ymjrhk.rbac.dto.UserDTO;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.UserHistoryService;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.PermissionVO;
import com.ymjrhk.rbac.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.PasswordConstant.rawPassword;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends BaseService implements UserService {
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserHistoryService userHistoryService;

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final UserRoleService userRoleService;

    private final PermissionMapper permissionMapper;

    /**
     * 根据用户名和密码创建用户
     *
     * @param userLoginDTO
     */
    @Override
    @Transactional // create 是一个复合写操作（insert + update），必须在同一事务内
    public void create(UserLoginDTO userLoginDTO) {
        // 1. 构造用户（先不设 password）
        User insertUser = new User();
        insertUser.setUsername(userLoginDTO.getUsername());
        insertUser.setPassword("!INIT!"); // // 先插入一条“不可登录”的占位密码（因为 password 不能为空），之后要修改
        insertUser.setNickname(userLoginDTO.getUsername()); // 默认昵称与用户名相同
        insertUser.setSecretToken(UUID.randomUUID().toString());

        Long operatorId = UserContext.getCurrentUserId(); // 拿到当前操作人
        insertUser.setCreateUserId(operatorId);
        insertUser.setUpdateUserId(operatorId); // 初始更新人与创建人相同

        // 2. 先插入，拿到 userId
        int row = userMapper.insert(insertUser);   // insert 后 insertUser.getUserId() 才有值
        if (row != 1) {
            throw new UserCreateFailedException(USER_CREATE_FAILED); // 创建用户失败（如果是用户名重复会提示“用户名已存在”）
        }
        Long newUserId = insertUser.getUserId();

        // 3. 使用 userId 作为 pepper
        String rawPassword = userLoginDTO.getPassword();
        String peppered = rawPassword + "#" + newUserId;
        String password = passwordEncoder.encode(peppered);

        // 4. 回写 password（// TODO：此处 auth_version 暂时不需要加一。未来应分为管理员创建用户版和用户自己注册版）
        User updateUser = new User();
        updateUser.setUserId(newUserId);
        updateUser.setPassword(password);

        int result = userMapper.updateForCreateUser(updateUser);
        if (result != 1) {
            throw new UserCreateFailedException(USER_CREATE_FAILED); // 创建用户失败
        }

        // 5. 写到历史表
        userHistoryService.record(newUserId, OperateTypeConstant.CREATE);
    }

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {
        normalizePage(userPageQueryDTO); // pageNum 和 pageSize 设置默认值兜底

        PageHelper.startPage(userPageQueryDTO.getPageNum(), userPageQueryDTO.getPageSize());

        Page<User> page = userMapper.pageQuery(userPageQueryDTO);

        long total = page.getTotal();

        List<UserVO> records = page.getResult()
                                   .stream()
                                   .map(user -> BeanUtil.copyProperties(user, UserVO.class))
                                   .toList();

        return new PageResult(total, records);
    }
    // TODO:为什么外卖里不填page,pageSize就查不到结果
    // TODO: 将来直接返回需要的字段，免得转换

    /**
     * 根据 userId 查询用户
     *
     * @param userId
     * @return
     */
    @Override
    public UserVO getByUserId(Long userId) {
        User user = userMapper.getByUserId(userId);

        // 用户不存在
        if (user == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 修改用户
     *
     * @param userId
     * @param userDTO
     */
    @Override
    @Transactional
    // 暂不更新 status
    public void update(Long userId, UserDTO userDTO) {
        User dbUser = userMapper.getByUserId(userId);

        // 用户不存在
        if (dbUser == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 用户被禁用，不能修改
        if (Objects.equals(dbUser.getStatus(), DISABLE)) {
            throw new UserForbiddenException(USER_FORBIDDEN);
        }

        User user = new User();
        user.setUserId(userId);
        user.setUsername(userDTO.getUsername());
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());

        Integer version = dbUser.getVersion(); // 获取版本号
        String secretToken = dbUser.getSecretToken(); // 获取旧 secretToken
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = UserContext.getCurrentUserId();

        fillOptimisticLockFields(user, version, secretToken, newSecretToken, updateUserId);

        doUpdate(user);

        // 写到历史表
        userHistoryService.record(user.getUserId(), OperateTypeConstant.UPDATE);

        // TODO:写审计表
    }

    /**
     * 启用或禁用用户
     *
     * @param userId
     * @param status
     */
    @Override
    @Transactional
    public void changeStatus(Long userId, Integer status) {
        // 1. 查数据库
        User dbUser = userMapper.getByUserId(userId);
        if (dbUser == null) { // 用户不存在
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 构造“更新用实体”（只放必要字段）
        User user = new User();
        user.setUserId(userId);

        // 3. 调 BaseService 的模板方法
        changeStatus(dbUser, user, status);

        // 4. 执行 update
        doUpdate(user);

        // 写到历史表
        userHistoryService.record(userId, OperateTypeConstant.UPDATE);

        // TODO:写审计表
    }

    /**
     * 重置用户密码
     *
     * @param userId
     */
    @Override
    @Transactional
    public void resetPassword(Long userId) {
        User dbUser = userMapper.getByUserId(userId);

        // 用户不存在
        if (dbUser == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 用户被禁用，不能修改
        if (Objects.equals(dbUser.getStatus(), DISABLE)) {
            throw new UserForbiddenException(USER_FORBIDDEN);
        }

        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(rawPassword + "#" + userId));

        Integer version = dbUser.getVersion();
        String secretToken = dbUser.getSecretToken();
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = UserContext.getCurrentUserId();

        fillOptimisticLockFields(user, version, secretToken, newSecretToken, updateUserId);

        // 获取登录版本号
        // 用于触发 auth_version + 1（强制下线），通过 authVersion + 1 使已有 jwt 失效
        Integer authVersion = dbUser.getAuthVersion();
        user.setAuthVersion(authVersion);

        doUpdate(user);

        // 写到历史表
        userHistoryService.record(userId, OperateTypeConstant.UPDATE);

        // TODO:写审计表
    }

    /**
     * 用户查询权限（可以查被禁用的用户的权限，但是不能查出用户禁用的角色和权限）
     *
     * @param userId
     * @return
     */
    @Override
    public List<PermissionVO> getUserPermissions(Long userId) {
        // 1. 查 userId 是否存在
        User user = userMapper.getByUserId(userId);
        if (user == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 是否是超级管理员
        if (isSuperAdmin(userId)) {
            return permissionMapper.listAllActivePermissions();
        }

        // 3. 查 userId 对应的权限
        return userMapper.selectPermissionsByUserId(userId);
    }

    /**
     * 判断用户拥有的权限是否匹配当前请求路径+方法
     *
     * @param userId
     * @param requestPath
     * @param requestMethod
     * @return
     */
    public boolean hasPermission(Long userId, String requestPath, String requestMethod) {
        // 0. 超级管理员直接放行（最重要）
        if (isSuperAdmin(userId)) {
            log.info("超级管理员直接放行，userId={}, path={}, method={}",
                    userId, requestPath, requestMethod);
            return true;
        }

        // 1. 查询该用户拥有的所有接口权限（已确定不是超级管理员）
        List<PermissionVO> permissions = getOrdinaryUserPermissions(userId);

        if (CollectionUtils.isEmpty(permissions)) {
            log.warn("无任何权限，userId={}", userId);
            return false;
        }

        log.debug("开始权限匹配，userId={}, path={}, method={}", userId, requestPath, requestMethod);

        // 2. 路径 + 方法匹配
        for (PermissionVO permission : permissions) {
            String permissionName = permission.getPermissionName();
            String path = permission.getPath();
            String method = permission.getMethod();

            // 2.1 只处理接口权限
            if (!Objects.equals(permission.getType(), PermissionTypeConstant.ACTION)) {
                continue;
            }

            // 2.2 兜底防御
            if (path == null || method == null) {
                log.warn("权限配置不完整，permissionName={}", permissionName);
                continue;
            }

            // 2.3 开始匹配
            if (matcher.match(path, requestPath) && method.equalsIgnoreCase(requestMethod)) {
                log.info("权限匹配成功，userId={}, permissionName={}, path={}, method={}", userId, permissionName, path, method);
                return true;
            }

            log.debug("权限未匹配，permissionName={}, path={}, method={}", permissionName, path, method);
        }

        // 3. 最终未匹配
        log.warn("权限校验失败，userId={}, path={}, method={}",
                userId, requestPath, requestMethod);
        return false;
    }

    /**
     * 判断是否是超级管理员，为 hasPermission() 服务
     * @param userId
     * @return
     */
    private boolean isSuperAdmin(Long userId) {
        log.debug("查询用户是否是超级管理员...");
        return userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN);
    }

    /**
     * 查非超级管理员用户权限，只为 hasPermission() 方法服务
     *
     * @param userId
     * @return
     */
    private List<PermissionVO> getOrdinaryUserPermissions(Long userId) {
        // 1. 查 userId 是否存在
        log.debug("查 userId 是否存在...");
        User user = userMapper.getByUserId(userId);
        if (user == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 查 userId 对应的权限
        log.debug("查 userId 对应的权限...");
        return userMapper.selectPermissionsByUserId(userId);
    }

    /**
     * 调用 mapper 的更新方法，同时进行乐观锁判断
     * @param user
     */
    public void doUpdate(User user) {
        int result = userMapper.update(user);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }
    }
}
