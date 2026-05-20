package dao;

import pojo.CustomerNurseItem;

public class CustomerNurseItemDao extends BaseAbstractDao<CustomerNurseItem> {
    public CustomerNurseItemDao() {
        super("customernurseitems.json", CustomerNurseItem.class);
    }
}