package dao;

import pojo.RoleMenu;

public class RoleMenuDao extends BaseAbstractDao<RoleMenu> {
    public RoleMenuDao() {
        super("rolemenus.json", RoleMenu.class);
    }
}