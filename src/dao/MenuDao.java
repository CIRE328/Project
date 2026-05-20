package dao;

import pojo.Menu;

public class MenuDao extends BaseAbstractDao<Menu> {
    public MenuDao() {
        super("menus.json", Menu.class);
    }
}