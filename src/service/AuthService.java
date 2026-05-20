package service;

import dao.UserDao;
import pojo.User;

import java.util.Optional;

/**
 * 认证服务：登录验证、角色判断
 */
public class AuthService {

    private final UserDao userDao = new UserDao();

    /**
     * 登录验证
     * @param username 账号
     * @param password 密码
     * @return 登录成功返回 User 对象，失败返回 null
     */
    public User login(String username, String password) {
        if (username == null || password == null) return null;
        // 遍历所有未删除用户
        for (User user : userDao.findAll()) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * 判断用户是否为管理员
     */
    public boolean isAdmin(User user) {
        return user != null && user.getRoleId() != null && user.getRoleId() == 1;
    }

    /**
     * 判断用户是否为健康管家
     */
    public boolean isHousekeeper(User user) {
        return user != null && user.getRoleId() != null && user.getRoleId() == 2;
    }
}