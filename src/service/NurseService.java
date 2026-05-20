package service;

import dao.*;
import pojo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 护理管理业务逻辑
 * 包含：护理项目、护理级别、级别-项目配置、客户护理设置、护理记录
 */
public class NurseService {

    private final NurseContentDao contentDao = new NurseContentDao();
    private final NurseLevelDao levelDao = new NurseLevelDao();
    private final NurseLevelItemDao levelItemDao = new NurseLevelItemDao();
    private final CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private final CustomerDao customerDao = new CustomerDao();

    // ==================== 护理项目管理 ====================
    public List<NurseContent> findAllNurseContents() {
        return contentDao.findAll();
    }

    public List<NurseContent> findNurseContentsByStatus(Integer status) {
        return contentDao.findAll().stream()
                .filter(c -> c.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<NurseContent> findNurseContentsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllNurseContents();
        return contentDao.findAll().stream()
                .filter(c -> c.getNursingName().contains(keyword))
                .collect(Collectors.toList());
    }

    public NurseContent addNurseContent(NurseContent content) {
        return contentDao.insert(content);
    }

    public boolean updateNurseContent(NurseContent content) {
        if (contentDao.findById(content.getId()).isEmpty()) return false;
        // 如果状态改为停用(2)，需要从所有护理级别项目中移除该护理项目
        NurseContent old = contentDao.findById(content.getId()).get();
        if (old.getStatus() == 1 && content.getStatus() == 2) {
            // 移除所有级别项目关联表中包含此itemId的记录
            List<NurseLevelItem> items = levelItemDao.findAll().stream()
                    .filter(item -> item.getItemId().equals(content.getId()))
                    .collect(Collectors.toList());
            for (NurseLevelItem item : items) {
                levelItemDao.deleteById(item.getId());
            }
        }
        contentDao.update(content);
        return true;
    }

    public boolean deleteNurseContent(Integer id) {
        return contentDao.deleteById(id);
    }

    // ==================== 护理级别管理 ====================
    public List<NurseLevel> findAllNurseLevels() {
        return levelDao.findAll();
    }

    public List<NurseLevel> findNurseLevelsByStatus(Integer status) {
        return levelDao.findAll().stream()
                .filter(l -> l.getLevelStatus().equals(status))
                .collect(Collectors.toList());
    }

    public NurseLevel addNurseLevel(NurseLevel level) {
        return levelDao.insert(level);
    }

    public boolean updateNurseLevel(NurseLevel level) {
        if (levelDao.findById(level.getId()).isEmpty()) return false;
        levelDao.update(level);
        return true;
    }

    public boolean deleteNurseLevel(Integer id) {
        // 逻辑删除前，需要先清除该级别下的所有项目关联
        List<NurseLevelItem> items = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(id))
                .collect(Collectors.toList());
        for (NurseLevelItem item : items) {
            levelItemDao.deleteById(item.getId());
        }
        return levelDao.deleteById(id);
    }

    // ==================== 级别-项目配置 ====================
    public List<NurseContent> getItemsByLevelId(Integer levelId) {
        List<Integer> itemIds = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId))
                .map(NurseLevelItem::getItemId)
                .collect(Collectors.toList());
        return contentDao.findAll().stream()
                .filter(c -> itemIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    public boolean addItemToLevel(Integer levelId, Integer itemId) {
        // 检查是否已存在
        boolean exists = levelItemDao.findAll().stream()
                .anyMatch(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId));
        if (exists) return false;
        NurseLevelItem item = new NurseLevelItem();
        item.setLevelId(levelId);
        item.setItemId(itemId);
        item.setIsDeleted(false);
        levelItemDao.insert(item);
        return true;
    }

    public boolean removeItemFromLevel(Integer levelId, Integer itemId) {
        Optional<NurseLevelItem> opt = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId))
                .findFirst();
        return opt.map(nurseLevelItem -> levelItemDao.deleteById(nurseLevelItem.getId())).orElse(false);
    }

    // ==================== 客户护理设置 ====================
    public boolean setCustomerLevel(Integer customerId, Integer levelId) {
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) return false;
        Customer customer = optCustomer.get();
        // 如果已有级别，必须先移除（业务规则：需要先移除再设置）
        if (customer.getLevelId() != null) {
            System.err.println("该客户已有护理级别，请先移除");
            return false;
        }
        // 获取该级别下的所有护理项目
        List<NurseContent> items = getItemsByLevelId(levelId);
        // 批量添加到 customernurseitem，购买数量=1，服务到期日期=当前日期+3个月
        LocalDate buyTime = LocalDate.now();
        LocalDate maturityTime = buyTime.plusMonths(3);
        for (NurseContent item : items) {
            CustomerNurseItem cni = new CustomerNurseItem();
            cni.setCustomerId(customerId);
            cni.setItemId(item.getId());
            cni.setLevelId(levelId);
            cni.setNurseNumber(1);
            cni.setBuyTime(buyTime);
            cni.setMaturityTime(maturityTime);
            cni.setIsDeleted(false);
            customerNurseItemDao.insert(cni);
        }
        // 更新客户的 levelId
        customer.setLevelId(levelId);
        customerDao.update(customer);
        return true;
    }

    public boolean removeCustomerLevel(Integer customerId) {
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) return false;
        Customer customer = optCustomer.get();
        if (customer.getLevelId() == null) {
            System.err.println("该客户没有护理级别");
            return false;
        }
        // 删除该客户在当前级别下的所有护理项目详情
        List<CustomerNurseItem> items = customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId) && cni.getLevelId().equals(customer.getLevelId()))
                .collect(Collectors.toList());
        for (CustomerNurseItem item : items) {
            customerNurseItemDao.deleteById(item.getId());
        }
        // 清空客户级别
        customer.setLevelId(null);
        customerDao.update(customer);
        return true;
    }

    // ==================== 客户购买/续费/移除护理项目（服务关注） ====================
    public List<CustomerNurseItem> getCustomerNurseItems(Integer customerId) {
        return customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public boolean purchaseNurseItem(Integer customerId, Integer itemId, Integer quantity, LocalDate maturityTime) {
        // 检查是否已有该护理项目（且未删除）
        boolean exists = customerNurseItemDao.findAll().stream()
                .anyMatch(cni -> cni.getCustomerId().equals(customerId) && cni.getItemId().equals(itemId));
        if (exists) {
            System.err.println("客户已拥有该护理项目，请使用续费功能");
            return false;
        }
        CustomerNurseItem cni = new CustomerNurseItem();
        cni.setCustomerId(customerId);
        cni.setItemId(itemId);
        cni.setLevelId(null); // 单独购买，不属于级别套餐
        cni.setNurseNumber(quantity);
        cni.setBuyTime(LocalDate.now());
        cni.setMaturityTime(maturityTime);
        cni.setIsDeleted(false);
        customerNurseItemDao.insert(cni);
        return true;
    }

    public boolean renewNurseItem(Integer customerNurseItemId, Integer additionalQuantity, LocalDate newMaturityTime) {
        Optional<CustomerNurseItem> opt = customerNurseItemDao.findById(customerNurseItemId);
        if (opt.isEmpty()) return false;
        CustomerNurseItem cni = opt.get();
        cni.setNurseNumber(cni.getNurseNumber() + additionalQuantity);
        if (newMaturityTime != null) {
            cni.setMaturityTime(newMaturityTime);
        }
        customerNurseItemDao.update(cni);
        return true;
    }

    public boolean removeCustomerNurseItem(Integer customerNurseItemId) {
        return customerNurseItemDao.deleteById(customerNurseItemId);
    }

    // ==================== 日常护理（生成护理记录，减少次数） ====================
    public boolean performNursing(Integer customerId, Integer itemId, Integer nursingCount, Integer userId) {
        // 查找客户对应的护理项目条目
        Optional<CustomerNurseItem> optCni = customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId) && cni.getItemId().equals(itemId))
                .findFirst();
        if (optCni.isEmpty()) {
            System.err.println("客户未购买该护理项目");
            return false;
        }
        CustomerNurseItem cni = optCni.get();
        // 检查剩余次数和有效期
        if (cni.getNurseNumber() < nursingCount) {
            System.err.println("剩余次数不足");
            return false;
        }
        if (cni.getMaturityTime().isBefore(LocalDate.now())) {
            System.err.println("服务已过期");
            return false;
        }
        // 减少次数
        cni.setNurseNumber(cni.getNurseNumber() - nursingCount);
        customerNurseItemDao.update(cni);
        // 生成护理记录
        NurseRecord record = new NurseRecord();
        record.setCustomerId(customerId);
        record.setItemId(itemId);
        record.setNursingTime(LocalDateTime.now());
        record.setNursingCount(nursingCount);
        record.setUserId(userId);
        record.setIsDeleted(false);
        // 可选：自动填充护理内容描述
        Optional<NurseContent> contentOpt = contentDao.findById(itemId);
        record.setNursingContent(contentOpt.map(NurseContent::getNursingName).orElse("护理服务"));
        nurseRecordDao.insert(record);
        return true;
    }

    public List<NurseRecord> getNurseRecordsByCustomer(Integer customerId) {
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public boolean deleteNurseRecord(Integer recordId) {
        return nurseRecordDao.deleteById(recordId);
    }
}