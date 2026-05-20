package dao;

import pojo.NurseLevelItem;

public class NurseLevelItemDao extends BaseAbstractDao<NurseLevelItem> {
    public NurseLevelItemDao() {
        super("nurselevelitems.json", NurseLevelItem.class);
    }
}