package service;

import dao.UserDao;
import pojo.User;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户管理：管理管理员和健康管家
 * 包含：增删改查，默认密码为手机号后6位
 */
public class UserService {

    private final UserDao userDao = new UserDao();

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> findUserById(Integer id) {
        return userDao.findById(id);
    }

    /**
     * 根据角色查询用户
     * @param roleId 1-管理员 2-健康管家
     */
    public List<User> findUsersByRole(Integer roleId) {
        return userDao.findAll().stream()
                .filter(u -> u.getRoleId().equals(roleId))
                .toList();
    }

    /**
     * 根据姓名模糊查询
     */
    public List<User> findUsersByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllUsers();
        return userDao.findAll().stream()
                .filter(u -> u.getNickname() != null && u.getNickname().contains(keyword))
                .toList();
    }

    /**
     * 添加用户（员工）
     * 密码默认设置为手机号后6位
     */
    public User addUser(User user) {
        // 校验账号唯一性
        if (userDao.findAll().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            System.err.println("用户名已存在");
            return null;
        }
        // 设置默认密码为手机号后6位
        if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
            String defaultPwd = user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6);
            user.setPassword(defaultPwd);
        } else {
            user.setPassword("123456"); // 后备默认密码
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsDeleted(false);
        return userDao.insert(user);
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        if (userDao.findById(user.getId()).isEmpty()) return false;
        user.setUpdateTime(LocalDateTime.now());
        userDao.update(user);
        return true;
    }

    /**
     * 重置用户密码为手机号后6位
     */
    public boolean resetPassword(Integer userId) {
        Optional<User> opt = userDao.findById(userId);
        if (opt.isEmpty()) return false;
        User user = opt.get();
        if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
            user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
        } else {
            user.setPassword("123456");
        }
        userDao.update(user);
        return true;
    }

    /**
     * 逻辑删除用户
     */
    public boolean deleteUser(Integer userId) {
        return userDao.deleteById(userId);
    }
}