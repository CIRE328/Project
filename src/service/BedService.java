package service;

import dao.*;
import pojo.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 床位管理业务逻辑
 * 包含：床位示意图、床位调换、床位使用详情查询
 */
public class BedService {

    private final BedDao bedDao = new BedDao();
    private final RoomDao roomDao = new RoomDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 获取床位统计数据
     * @return map: total, free, occupied, outward
     */
    public Map<String, Integer> getBedStatistics() {
        List<Bed> allBeds = bedDao.findAll();
        int total = allBeds.size();
        int free = (int) allBeds.stream().filter(b -> b.getBedStatus() == 1).count();
        int occupied = (int) allBeds.stream().filter(b -> b.getBedStatus() == 2).count();
        int outward = (int) allBeds.stream().filter(b -> b.getBedStatus() == 3).count();
        return Map.of("total", total, "free", free, "occupied", occupied, "outward", outward);
    }

    /**
     * 按楼层获取房间及床位列表（用于床位示意图）
     * @param floor 楼层，如 "1F"
     * @return 房间列表，每个房间包含房间号和该房间下的床位列表
     */
    public List<Map<String, Object>> getRoomsWithBedsByFloor(String floor) {
        // 获取该楼层所有房间（room_no 为整数）
        List<Room> rooms = roomDao.findAll().stream()
                .filter(r -> r.getRoomFloor().equals(floor))
                .collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomNo", room.getRoomNo());
            List<Bed> beds = bedDao.findAll().stream()
                    .filter(b -> b.getRoomNo().equals(room.getRoomNo()))
                    .collect(Collectors.toList());
            roomInfo.put("beds", beds);
            result.add(roomInfo);
        }
        return result;
    }

    /**
     * 查询客户的床位使用详情
     * @param customerId 客户ID
     * @param usageStatus "当前使用" 或 "历史使用"
     * @return 床位详情列表
     */
    public List<BedDetails> getBedUsageDetails(Integer customerId, String usageStatus) {
        List<BedDetails> all = bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        if ("当前使用".equals(usageStatus)) {
            return all.stream().filter(d -> d.getEndDate() == null).collect(Collectors.toList());
        } else if ("历史使用".equals(usageStatus)) {
            return all.stream().filter(d -> d.getEndDate() != null).collect(Collectors.toList());
        }
        return all;
    }

    /**
     * 床位调换（事务性操作，多个步骤）
     * @param customerId 客户ID
     * @param newBedId 新床位ID
     * @return 是否成功
     */
    public boolean changeBed(Integer customerId, Integer newBedId) {
        // 1. 检查客户存在且未删除
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) {
            System.err.println("客户不存在");
            return false;
        }
        Customer customer = optCustomer.get();
        Integer oldBedId = customer.getBedId();

        // 2. 检查新床位存在且空闲
        Optional<Bed> optNewBed = bedDao.findById(newBedId);
        if (optNewBed.isEmpty() || optNewBed.get().getBedStatus() != 1) {
            System.err.println("新床位不存在或不是空闲状态");
            return false;
        }
        Bed newBed = optNewBed.get();

        // 3. 结束旧床位使用记录（设置结束时间为今天）
        List<BedDetails> oldDetailsList = bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId().equals(customerId) && d.getBedId().equals(oldBedId) && d.getEndDate() == null)
                .collect(Collectors.toList());
        for (BedDetails oldDetails : oldDetailsList) {
            oldDetails.setEndDate(LocalDate.now());
            bedDetailsDao.update(oldDetails);
        }

        // 4. 释放旧床位（状态设为空闲）
        Optional<Bed> optOldBed = bedDao.findById(oldBedId);
        if (optOldBed.isPresent()) {
            Bed oldBed = optOldBed.get();
            oldBed.setBedStatus(1);
            bedDao.update(oldBed);
        }

        // 5. 占用新床位（状态设为有人）
        newBed.setBedStatus(2);
        bedDao.update(newBed);

        // 6. 添加新床位使用记录（开始时间为今天，结束时间为null）
        BedDetails newDetails = new BedDetails();
        newDetails.setStartDate(LocalDate.now());
        newDetails.setEndDate(null);
        newDetails.setCustomerId(customerId);
        newDetails.setBedId(newBedId);
        newDetails.setIsDeleted(false);
        bedDetailsDao.insert(newDetails);

        // 7. 更新客户信息中的床位ID和房间号
        customer.setBedId(newBedId);
        // 根据床位获取房间号
        Integer roomNo = newBed.getRoomNo();
        customer.setRoomNo(String.valueOf(roomNo));
        customerDao.update(customer);

        return true;
    }

    /**
     * 根据客户姓名、入住日期、使用状态组合查询床位使用详情
     * (需求中的复合查询)
     */
    public List<BedDetails> queryBedDetails(String customerName, LocalDate checkinDate, String usageStatus) {
        // 先根据姓名模糊查询客户ID
        List<Customer> customers = customerDao.findAll().stream()
                .filter(c -> customerName == null || c.getCustomerName().contains(customerName))
                .collect(Collectors.toList());
        if (customers.isEmpty()) return List.of();
        Set<Integer> customerIds = customers.stream().map(Customer::getId).collect(Collectors.toSet());

        // 所有相关详情
        List<BedDetails> allDetails = bedDetailsDao.findAll().stream()
                .filter(d -> customerIds.contains(d.getCustomerId()))
                .collect(Collectors.toList());

        // 按入住日期筛选（入住日期即 startDate）
        if (checkinDate != null) {
            allDetails = allDetails.stream()
                    .filter(d -> d.getStartDate() != null && d.getStartDate().equals(checkinDate))
                    .collect(Collectors.toList());
        }

        // 按使用状态筛选
        if ("当前使用".equals(usageStatus)) {
            allDetails = allDetails.stream().filter(d -> d.getEndDate() == null).collect(Collectors.toList());
        } else if ("历史使用".equals(usageStatus)) {
            allDetails = allDetails.stream().filter(d -> d.getEndDate() != null).collect(Collectors.toList());
        }
        return allDetails;
    }
}