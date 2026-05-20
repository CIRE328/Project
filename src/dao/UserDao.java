package dao;

import pojo.User;

public class UserDao extends BaseAbstractDao<User> {
    public UserDao() {
        super("users.json", User.class);
    }

}