package service;

import dao.*;
import pojo.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康管家服务：为客户分配管家、移除管家、查询管家服务客户
 */
public class HousekeeperService {

    private final CustomerDao customerDao = new CustomerDao();
    private final UserDao userDao = new UserDao();

    /**
     * 获取所有健康管家（roleId=2）
     */
    public List<User> findAllHousekeepers() {
        return userDao.findAll().stream()
                .filter(u -> u.getRoleId() == 2)
                .collect(Collectors.toList());
    }

    /**
     * 获取无管家的客户（userId == null 或 userId == -1）
     */
    public List<Customer> findCustomersWithoutHousekeeper() {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() == null || c.getUserId() == -1)
                .collect(Collectors.toList());
    }

    /**
     * 查询某个管家服务的所有客户
     */
    public List<Customer> findCustomersByHousekeeper(Integer housekeeperId) {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(housekeeperId))
                .collect(Collectors.toList());
    }

    /**
     * 为客户分配管家
     * @param customerId 客户ID
     * @param housekeeperId 管家ID（必须存在且角色为健康管家）
     * @return 是否成功
     */
    public boolean assignHousekeeper(Integer customerId, Integer housekeeperId) {
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) return false;
        Optional<User> optHousekeeper = userDao.findById(housekeeperId);
        if (optHousekeeper.isEmpty() || optHousekeeper.get().getRoleId() != 2) {
            System.err.println("指定的管家不存在或不是健康管家");
            return false;
        }
        Customer customer = optCustomer.get();
        customer.setUserId(housekeeperId);
        customerDao.update(customer);
        return true;
    }

    /**
     * 移除客户的管家（设置为无管家）
     */
    public boolean removeHousekeeper(Integer customerId) {
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) return false;
        Customer customer = optCustomer.get();
        customer.setUserId(-1);
        customerDao.update(customer);
        return true;
    }
}