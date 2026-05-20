package dao;

import pojo.Role;

public class RoleDao extends BaseAbstractDao<Role> {
    public RoleDao() {
        super("roles.json", Role.class);
    }
}