package dao;

import pojo.BedDetails;

public class BedDetailsDao extends BaseAbstractDao<BedDetails> {
    public BedDetailsDao() {
        super("beddetails.json", BedDetails.class);
    }
}