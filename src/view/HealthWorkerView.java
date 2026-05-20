package view;

import pojo.*;
import service.*;
import view.utils.ConsoleUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HealthWorkerView {

    private static final CustomerService customerService = new CustomerService();
    private static final NurseService nurseService = new NurseService();
    private static final HousekeeperService housekeeperService = new HousekeeperService();

    private static User currentWorker;

    public static void show(User worker) {
        currentWorker = worker;
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printTitle("健康管家菜单");
            System.out.println("当前登录：" + worker.getNickname() + "（健康管家）");
            ConsoleUtils.printSeparator();
            System.out.println("1. 我的服务客户");
            System.out.println("2. 日常护理");
            System.out.println("3. 护理记录查询");
            System.out.println("4. 外出申请");
            System.out.println("5. 退住申请");
            System.out.println("0. 退出登录");
            ConsoleUtils.printSeparator();
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: listMyCustomers(); break;
                case 2: dailyNursing(); break;
                case 3: queryMyNurseRecords(); break;
                case 4: applyOutward(); break;
                case 5: applyBackdown(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
            ConsoleUtils.pressEnterToContinue();
        }
    }

    private static void listMyCustomers() {
        ConsoleUtils.printSubTitle("我的服务客户");
        List<Customer> customers = housekeeperService.findCustomersByHousekeeper(currentWorker.getId());
        if (customers.isEmpty()) {
            System.out.println("暂无服务客户");
        } else {
            customers.forEach(System.out::println);
        }
    }

    private static void dailyNursing() {
        ConsoleUtils.printSubTitle("日常护理");
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Optional<Customer> opt = customerService.findCustomerById(customerId);
        if (opt.isEmpty()) { System.out.println("客户不存在"); return; }
        // 检查该客户是否属于当前管家
        if (!opt.get().getUserId().equals(currentWorker.getId())) {
            System.out.println("该客户不是您服务的客户，无法护理");
            return;
        }
        // 显示客户已有的护理项目
        List<CustomerNurseItem> items = nurseService.getCustomerNurseItems(customerId);
        if (items.isEmpty()) {
            System.out.println("该客户没有购买任何护理项目");
            return;
        }
        System.out.println("可护理项目:");
        for (CustomerNurseItem cni : items) {
            Optional<NurseContent> content = nurseService.findAllNurseContents().stream()
                    .filter(c -> c.getId().equals(cni.getItemId())).findFirst();
            String name = content.map(NurseContent::getNursingName).orElse("未知");
            System.out.println("项目ID:" + cni.getItemId() + " " + name + " 剩余次数:" + cni.getNurseNumber() +
                    " 到期:" + cni.getMaturityTime());
        }
        Integer itemId = ConsoleUtils.readInt("项目ID: ");
        int count = ConsoleUtils.readInt("护理次数: ");
        boolean success = nurseService.performNursing(customerId, itemId, count, currentWorker.getId());
        System.out.println(success ? "护理记录已生成" : "护理失败，请检查剩余次数或有效期");
    }

    private static void queryMyNurseRecords() {
        ConsoleUtils.printSubTitle("护理记录查询");
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        List<NurseRecord> records = nurseService.getNurseRecordsByCustomer(customerId);
        if (records.isEmpty()) {
            System.out.println("无护理记录");
        } else {
            records.forEach(System.out::println);
        }
    }

    private static void applyOutward() {
        ConsoleUtils.printSubTitle("外出申请");
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Optional<Customer> opt = customerService.findCustomerById(customerId);
        if (opt.isEmpty()) { System.out.println("客户不存在"); return; }
        if (!opt.get().getUserId().equals(currentWorker.getId())) {
            System.out.println("该客户不是您服务的客户");
            return;
        }
        Outward outward = new Outward();
        outward.setCustomerId(customerId);
        outward.setOutgoingReason(ConsoleUtils.readString("外出事由: "));
        outward.setOutgoingTime(ConsoleUtils.readDate("外出日期"));
        outward.setExpectedReturnTime(ConsoleUtils.readDate("预计返回日期"));
        outward.setEscorted(ConsoleUtils.readString("陪同人: "));
        outward.setRelation(ConsoleUtils.readString("关系: "));
        outward.setEscortedTel(ConsoleUtils.readString("陪同人电话: "));
        boolean success = customerService.submitOutward(outward);
        System.out.println(success ? "申请已提交" : "提交失败");
    }

    private static void applyBackdown() {
        ConsoleUtils.printSubTitle("退住申请");
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Optional<Customer> opt = customerService.findCustomerById(customerId);
        if (opt.isEmpty()) { System.out.println("客户不存在"); return; }
        if (!opt.get().getUserId().equals(currentWorker.getId())) {
            System.out.println("该客户不是您服务的客户");
            return;
        }
        Backdown backdown = new Backdown();
        backdown.setCustomerId(customerId);
        backdown.setRetreatTime(ConsoleUtils.readDate("退住日期"));
        backdown.setRetreatType(ConsoleUtils.readInt("退住类型 (0正常/1死亡/2保留床位): "));
        backdown.setRetreatReason(ConsoleUtils.readString("退住原因: "));
        boolean success = customerService.submitBackdown(backdown);
        System.out.println(success ? "申请已提交" : "提交失败");
    }
}