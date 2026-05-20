package dao;

import pojo.Customer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerDao extends BaseAbstractDao<Customer> {
    public CustomerDao() {
        super("customers.json", Customer.class);
    }

    // 常用自定义方法示例（可根据需要保留或删除）

    // 根据客户姓名模糊查询
    public List<Customer> findByNameLike(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(c -> c.getCustomerName() != null &&
                        c.getCustomerName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    // 根据护理级别ID查询客户
    public List<Customer> findByLevelId(Integer levelId) {
        if (levelId == null) {
            return findAll();
        }
        return findAll().stream()
                .filter(c -> c.getLevelId() != null && c.getLevelId().equals(levelId))
                .collect(Collectors.toList());
    }

    // 根据健康管家ID查询服务客户
    public List<Customer> findByUserId(Integer userId) {
        if (userId == null) {
            return findAll();
        }
        return findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // 查询无管家的客户（userId 为 null 或 -1）
    public List<Customer> findWithoutHousekeeper() {
        return findAll().stream()
                .filter(c -> c.getUserId() == null || c.getUserId() == -1)
                .collect(Collectors.toList());
    }

    // 查询有护理级别的客户
    public List<Customer> findWithLevel() {
        return findAll().stream()
                .filter(c -> c.getLevelId() != null && c.getLevelId() > 0)
                .collect(Collectors.toList());
    }
}