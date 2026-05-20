package dao;

import pojo.NurseContent;

public class NurseContentDao extends BaseAbstractDao<NurseContent> {
    public NurseContentDao() {
        super("nursecontents.json", NurseContent.class);
    }
}