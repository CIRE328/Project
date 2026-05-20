package dao;

import pojo.Backdown;

public class BackdownDao extends BaseAbstractDao<Backdown> {
    public BackdownDao() {
        super("backdowns.json", Backdown.class);
    }
}