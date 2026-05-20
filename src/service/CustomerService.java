package service;

import dao.*;
import pojo.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户管理业务逻辑
 * 包含：入住登记、退住申请/审核、外出申请/审核、修改客户信息、删除客户（逻辑删除）
 */
public class CustomerService {

    private final CustomerDao customerDao = new CustomerDao();
    private final BedDao bedDao = new BedDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final OutwardDao outwardDao = new OutwardDao();
    private final BackdownDao backdownDao = new BackdownDao();

    // ==================== 客户基本操作 ====================

    /**
     * 查询所有未删除的客户
     */
    public List<Customer> findAllCustomers() {
        return customerDao.findAll();
    }

    /**
     * 根据客户姓名模糊查询（不分页）
     */
    public List<Customer> findCustomersByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllCustomers();
        }
        String lowerKw = keyword.toLowerCase();
        return customerDao.findAll().stream()
                .filter(c -> c.getCustomerName() != null && c.getCustomerName().toLowerCase().contains(lowerKw))
                .collect(Collectors.toList());
    }

    /**
     * 根据老人类型查询：自理老人（levelId == null）和护理老人（levelId != null）
     */
    public List<Customer> findCustomersByType(String type) {
        if ("自理老人".equals(type)) {
            return customerDao.findAll().stream()
                    .filter(c -> c.getLevelId() == null)
                    .collect(Collectors.toList());
        } else if ("护理老人".equals(type)) {
            return customerDao.findAll().stream()
                    .filter(c -> c.getLevelId() != null)
                    .collect(Collectors.toList());
        } else {
            return findAllCustomers();
        }
    }

    /**
     * 根据ID查询客户
     */
    public Optional<Customer> findCustomerById(Integer id) {
        return customerDao.findById(id);
    }

    /**
     * 入住登记
     * @param customer 客户信息（不含id，checkinDate, expirationDate等已填入）
     * @param bedId 选中的床位ID
     * @return 是否成功
     */
    public boolean checkin(Customer customer, Integer bedId) {
        // 1. 校验合同到期时间 >= 入住时间
        if (customer.getExpirationDate().isBefore(customer.getCheckinDate())) {
            System.err.println("合同到期时间不能小于入住时间");
            return false;
        }

        // 2. 检查床位是否存在且状态为空闲
        Optional<Bed> optBed = bedDao.findById(bedId);
        if (optBed.isEmpty() || optBed.get().getBedStatus() != 1) {
            System.err.println("床位不存在或不是空闲状态");
            return false;
        }
        Bed bed = optBed.get();

        // 3. 自动计算年龄（根据出生日期）
        if (customer.getBirthday() != null) {
            int age = LocalDate.now().getYear() - customer.getBirthday().getYear();
            customer.setCustomerAge(age);
        }

        // 4. 设置客户的其他默认值（楼栋固定001，床位ID，无管家等）
        customer.setBuildingNo("001");
        customer.setBedId(bedId);
        if (customer.getUserId() == null) customer.setUserId(-1); // 无管家
        customer.setIsDeleted(false);

        // 5. 保存客户信息（自动生成id）
        Customer saved = customerDao.insert(customer);

        // 6. 更新床位状态为有人（2）
        bed.setBedStatus(2);
        bedDao.update(bed);

        // 7. 添加床位使用详情记录（startDate = 入住时间，endDate = null）
        BedDetails details = new BedDetails();
        details.setStartDate(customer.getCheckinDate());
        details.setEndDate(null);
        details.setCustomerId(saved.getId());
        details.setBedId(bedId);
        details.setIsDeleted(false);
        bedDetailsDao.insert(details);

        return true;
    }

    /**
     * 修改客户信息
     * @param customer 已修改的客户对象（需包含id）
     * @return 是否成功
     */
    public boolean updateCustomer(Customer customer) {
        // 检查是否存在
        if (customerDao.findById(customer.getId()).isEmpty()) {
            System.err.println("客户不存在");
            return false;
        }
        // 如果合同到期时间发生变化，需要更新当前生效的床位详情的结束时间
        Customer old = customerDao.findById(customer.getId()).get();
        if (!old.getExpirationDate().equals(customer.getExpirationDate())) {
            // 找到该客户正在使用的床位详情（endDate为null且未删除）
            List<BedDetails> detailsList = bedDetailsDao.findAll().stream()
                    .filter(d -> d.getCustomerId().equals(customer.getId()) && d.getEndDate() == null && !d.getIsDeleted())
                    .collect(Collectors.toList());
            for (BedDetails d : detailsList) {
                d.setEndDate(customer.getExpirationDate());
                bedDetailsDao.update(d);
            }
        }
        // 更新客户
        customerDao.update(customer);
        return true;
    }

    /**
     * 逻辑删除客户（退住清理）
     * @param customerId
     * @return
     */
    public boolean deleteCustomer(Integer customerId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer customer = opt.get();

        // 1. 释放当前床位
        Integer bedId = customer.getBedId();
        if (bedId != null) {
            Optional<Bed> bedOpt = bedDao.findById(bedId);
            if (bedOpt.isPresent()) {
                Bed bed = bedOpt.get();
                bed.setBedStatus(1); // 空闲
                bedDao.update(bed);
            }
        }

        // 2. 将当前正在使用的床位详情标记为结束（如果未结束）
        List<BedDetails> detailsList = bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId().equals(customerId) && d.getEndDate() == null && !d.getIsDeleted())
                .collect(Collectors.toList());
        for (BedDetails d : detailsList) {
            d.setEndDate(LocalDate.now());
            bedDetailsDao.update(d);
        }

        // 3. 逻辑删除客户
        return customerDao.deleteById(customerId);
    }

    // ==================== 外出申请与审核 ====================

    /**
     * 健康管家为客户提交外出申请
     * @param outward 外出申请对象（不含id，审批状态默认0）
     * @return 是否成功
     */
    public boolean submitOutward(Outward outward) {
        // 校验客户存在且未删除
        if (customerDao.findById(outward.getCustomerId()).isEmpty()) {
            System.err.println("客户不存在");
            return false;
        }
        outward.setAuditStatus(0); // 已提交
        outward.setIsDeleted(false);
        outwardDao.insert(outward);
        return true;
    }

    /**
     * 管理员查询所有外出申请（可含条件）
     */
    public List<Outward> findAllOutwards() {
        return outwardDao.findAll();
    }

    /**
     * 根据客户姓名模糊查询外出申请
     */
    public List<Outward> findOutwardsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        List<Integer> customerIds = customers.stream().map(Customer::getId).collect(Collectors.toList());
        return outwardDao.findAll().stream()
                .filter(o -> customerIds.contains(o.getCustomerId()))
                .collect(Collectors.toList());
    }

    /**
     * 管理员审核外出申请
     * @param outwardId 申请ID
     * @param approved true-通过 false-不通过
     * @param auditorName 审批人姓名
     */
    public boolean auditOutward(Integer outwardId, boolean approved, String auditorName) {
        Optional<Outward> opt = outwardDao.findById(outwardId);
        if (opt.isEmpty()) return false;
        Outward outward = opt.get();
        if (outward.getAuditStatus() != 0) {
            System.err.println("该申请已审核过");
            return false;
        }
        if (approved) {
            outward.setAuditStatus(1);
            // 审核通过：修改客户当前床位状态为“外出”（3）
            Optional<Customer> customerOpt = customerDao.findById(outward.getCustomerId());
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                Optional<Bed> bedOpt = bedDao.findById(customer.getBedId());
                if (bedOpt.isPresent()) {
                    Bed bed = bedOpt.get();
                    bed.setBedStatus(3);
                    bedDao.update(bed);
                }
            }
        } else {
            outward.setAuditStatus(2);
        }
        outward.setAuditPerson(auditorName);
        outward.setAuditTime(LocalDate.now());
        outwardDao.update(outward);
        return true;
    }

    /**
     * 老人回院登记（更新实际回院时间，并恢复床位状态为有人）
     * @param outwardId
     * @param actualReturnTime
     */
    public boolean returnFromOutward(Integer outwardId, LocalDate actualReturnTime) {
        Optional<Outward> opt = outwardDao.findById(outwardId);
        if (opt.isEmpty()) return false;
        Outward outward = opt.get();
        if (outward.getAuditStatus() != 1) {
            System.err.println("只有审核通过的外出才能登记回院");
            return false;
        }
        outward.setActualReturnTime(actualReturnTime);
        outwardDao.update(outward);
        // 恢复床位状态为有人
        Optional<Customer> customerOpt = customerDao.findById(outward.getCustomerId());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            Optional<Bed> bedOpt = bedDao.findById(customer.getBedId());
            if (bedOpt.isPresent()) {
                Bed bed = bedOpt.get();
                bed.setBedStatus(2);
                bedDao.update(bed);
            }
        }
        return true;
    }

    // ==================== 退住申请与审核 ====================

    /**
     * 健康管家为客户提交退住申请
     * @param backdown 退住申请对象
     * @return
     */
    public boolean submitBackdown(Backdown backdown) {
        if (customerDao.findById(backdown.getCustomerId()).isEmpty()) {
            System.err.println("客户不存在");
            return false;
        }
        backdown.setAuditStatus(0);
        backdown.setIsDeleted(false);
        backdownDao.insert(backdown);
        return true;
    }

    /**
     * 管理员查询所有退住申请
     */
    public List<Backdown> findAllBackdowns() {
        return backdownDao.findAll();
    }

    /**
     * 按客户姓名模糊查询退住申请
     */
    public List<Backdown> findBackdownsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        List<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        return backdownDao.findAll().stream()
                .filter(b -> ids.contains(b.getCustomerId()))
                .collect(Collectors.toList());
    }

    /**
     * 管理员审核退住申请
     * @param backdownId
     * @param approved true-通过 false-不通过
     * @param auditorName
     * @return
     */
    public boolean auditBackdown(Integer backdownId, boolean approved, String auditorName) {
        Optional<Backdown> opt = backdownDao.findById(backdownId);
        if (opt.isEmpty()) return false;
        Backdown backdown = opt.get();
        if (backdown.getAuditStatus() != 0) {
            System.err.println("已审核过了");
            return false;
        }
        if (approved) {
            backdown.setAuditStatus(1);
            // 审核通过，处理床位释放和客户逻辑删除
            Integer customerId = backdown.getCustomerId();
            Optional<Customer> customerOpt = customerDao.findById(customerId);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                Integer bedId = customer.getBedId();
                if (bedId != null) {
                    Optional<Bed> bedOpt = bedDao.findById(bedId);
                    if (bedOpt.isPresent()) {
                        Bed bed = bedOpt.get();
                        bed.setBedStatus(1); // 空闲
                        bedDao.update(bed);
                    }
                }
                // 逻辑删除客户
                customerDao.deleteById(customerId);
                // 结束床位使用记录
                List<BedDetails> details = bedDetailsDao.findAll().stream()
                        .filter(d -> d.getCustomerId().equals(customerId) && d.getEndDate() == null && !d.getIsDeleted())
                        .collect(Collectors.toList());
                for (BedDetails d : details) {
                    d.setEndDate(LocalDate.now());
                    bedDetailsDao.update(d);
                }
            }
        } else {
            backdown.setAuditStatus(2);
        }
        backdown.setAuditPerson(auditorName);
        backdown.setAuditTime(LocalDate.now());
        backdownDao.update(backdown);
        return true;
    }
}