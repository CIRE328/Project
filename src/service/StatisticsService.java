package service;

import dao.*;
import pojo.Bed;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务：提供各种统计数据
 * （可合并到其他Service，这里单独列出便于调用）
 */
public class StatisticsService {

    private final BedDao bedDao = new BedDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();

    /**
     * 获取床位统计（复用 BedService 的方法，避免重复）
     * 实际可以直接使用 BedService.getBedStatistics()
     * 此处为了独立，重新实现
     */
    public Map<String, Integer> getBedStatistics() {
        List<Bed> beds = bedDao.findAll();
        int total = beds.size();
        int free = (int) beds.stream().filter(b -> b.getBedStatus() == 1).count();
        int occupied = (int) beds.stream().filter(b -> b.getBedStatus() == 2).count();
        int outward = (int) beds.stream().filter(b -> b.getBedStatus() == 3).count();
        return Map.of("total", total, "free", free, "occupied", occupied, "outward", outward);
    }

    /**
     * 获取客户统计：总数、自理老人数、护理老人数
     */
    public Map<String, Integer> getCustomerStatistics() {
        List<pojo.Customer> customers = customerDao.findAll();
        int total = customers.size();
        int selfCare = (int) customers.stream().filter(c -> c.getLevelId() == null).count();
        int nursingCare = total - selfCare;
        return Map.of("total", total, "selfCare", selfCare, "nursingCare", nursingCare);
    }

    /**
     * 获取护理记录总数（可按客户筛选）
     */
    public long getNurseRecordCount(Integer customerId) {
        if (customerId == null) {
            return nurseRecordDao.findAll().size();
        } else {
            return nurseRecordDao.findAll().stream()
                    .filter(r -> r.getCustomerId().equals(customerId))
                    .count();
        }
    }
}