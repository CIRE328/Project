package dao;

import pojo.NurseRecord;

public class NurseRecordDao extends BaseAbstractDao<NurseRecord> {
    public NurseRecordDao() {
        super("nurserecords.json", NurseRecord.class);
    }
}