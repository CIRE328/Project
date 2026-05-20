package dao;

import pojo.Outward;

public class OutwardDao extends BaseAbstractDao<Outward> {
    public OutwardDao() {
        super("outwards.json", Outward.class);
    }
}