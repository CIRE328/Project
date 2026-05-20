package dao;

import pojo.CustomerPreference;

public class CustomerPreferenceDao extends BaseAbstractDao<CustomerPreference> {
    public CustomerPreferenceDao() {
        super("customerpreferences.json", CustomerPreference.class);
    }
}