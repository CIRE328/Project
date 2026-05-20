package dao;

import pojo.NurseLevel;

public class NurseLevelDao extends BaseAbstractDao<NurseLevel> {
    public NurseLevelDao() {
        super("nurselevels.json", NurseLevel.class);
    }
}