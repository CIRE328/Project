package dao;

import pojo.Bed;

public class BedDao extends BaseAbstractDao<Bed> {
    public BedDao() {
        super("beds.json", Bed.class);
    }
}