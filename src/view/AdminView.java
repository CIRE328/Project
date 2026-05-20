package view;

import pojo.*;
import service.*;
import view.utils.ConsoleUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminView {

    private static final CustomerService customerService = new CustomerService();
    private static final BedService bedService = new BedService();
    private static final NurseService nurseService = new NurseService();
    private static final HousekeeperService housekeeperService = new HousekeeperService();
    private static final UserService userService = new UserService();
    private static final MealService mealService = new MealService();
    private static final StatisticsService statisticsService = new StatisticsService();

    private static User currentAdmin;

    public static void show(User admin) {
        currentAdmin = admin;
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printTitle("系统管理员菜单");
            System.out.println("当前登录：" + admin.getNickname() + "（管理员）");
            ConsoleUtils.printSeparator();
            System.out.println("1. 客户管理");
            System.out.println("2. 床位管理");
            System.out.println("3. 护理管理");
            System.out.println("4. 健康管家管理");
            System.out.println("5. 用户管理");
            System.out.println("6. 膳食管理");
            System.out.println("7. 统计信息");
            System.out.println("0. 退出登录");
            ConsoleUtils.printSeparator();
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: customerManagement(); break;
                case 2: bedManagement(); break;
                case 3: nurseManagement(); break;
                case 4: housekeeperManagement(); break;
                case 5: userManagement(); break;
                case 6: mealManagement(); break;
                case 7: showStatistics(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
            ConsoleUtils.pressEnterToContinue();
        }
    }

    // ==================== 客户管理 ====================
    private static void customerManagement() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printSubTitle("客户管理");
            System.out.println("1. 入住登记");
            System.out.println("2. 修改客户信息");
            System.out.println("3. 删除客户（逻辑删除）");
            System.out.println("4. 查询客户列表");
            System.out.println("5. 退住申请审核");
            System.out.println("6. 外出申请审核");
            System.out.println("0. 返回上级");
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: checkin(); break;
                case 2: updateCustomer(); break;
                case 3: deleteCustomer(); break;
                case 4: searchCustomers(); break;
                case 5: auditBackdown(); break;
                case 6: auditOutward(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void checkin() {
        ConsoleUtils.printSubTitle("入住登记");
        Customer customer = new Customer();
        customer.setCustomerName(ConsoleUtils.readString("客户姓名: "));
        customer.setCustomerSex(ConsoleUtils.readInt("性别 (0-男 1-女): "));
        customer.setIdcard(ConsoleUtils.readString("身份证号: "));
        customer.setBirthday(ConsoleUtils.readDate("出生日期"));
        customer.setBloodType(ConsoleUtils.readString("血型: "));
        customer.setContactTel(ConsoleUtils.readString("联系电话: "));
        customer.setFamilyMember(ConsoleUtils.readString("家属姓名: "));
        customer.setCheckinDate(ConsoleUtils.readDate("入住时间"));
        customer.setExpirationDate(ConsoleUtils.readDate("合同到期时间"));
        // 显示所有空闲床位
        List<Bed> freeBeds = bedService.getBedStatistics().entrySet().stream().noneMatch(e -> e.getKey().equals("free")) ?
                List.of() : bedService.getBedStatistics().entrySet().stream().filter(e -> e.getKey().equals("free")).findFirst().map(e -> {
            // 简化：直接调用BedService获取空闲床位列表需要新增方法，这里先手动取
            return List.<Bed>of();
        }).orElse(List.of());
        // 实际上需要从BedDao获取空闲床位，为了简洁，我们在BedService中增加一个方法getFreeBeds()
        // 但暂时先提示用户输入床位ID
        System.out.println("请先确保床位空闲，床位ID列表可查看床位管理->床位示意图");
        Integer bedId = ConsoleUtils.readInt("床位ID: ");
        boolean success = customerService.checkin(customer, bedId);
        System.out.println(success ? "入住登记成功！" : "入住登记失败，请检查床位状态或输入信息。");
    }

    private static void updateCustomer() {
        ConsoleUtils.printSubTitle("修改客户信息");
        Integer id = ConsoleUtils.readInt("客户ID: ");
        Optional<Customer> opt = customerService.findCustomerById(id);
        if (opt.isEmpty()) {
            System.out.println("客户不存在");
            return;
        }
        Customer c = opt.get();
        System.out.println("原信息: " + c);
        String name = ConsoleUtils.readStringAllowEmpty("新姓名 (直接回车不变): ");
        if (!name.isEmpty()) c.setCustomerName(name);
        String tel = ConsoleUtils.readStringAllowEmpty("新联系电话: ");
        if (!tel.isEmpty()) c.setContactTel(tel);
        LocalDate newExp = ConsoleUtils.readDate("新合同到期时间 (直接回车不变): ");
        if (newExp != null) c.setExpirationDate(newExp);
        boolean success = customerService.updateCustomer(c);
        System.out.println(success ? "修改成功" : "修改失败");
    }

    private static void deleteCustomer() {
        Integer id = ConsoleUtils.readInt("客户ID: ");
        boolean success = customerService.deleteCustomer(id);
        System.out.println(success ? "已逻辑删除客户" : "删除失败");
    }

    private static void searchCustomers() {
        ConsoleUtils.printSubTitle("查询客户");
        String type = ConsoleUtils.readString("老人类型 (自理老人/护理老人/全部): ");
        String name = ConsoleUtils.readString("客户姓名 (模糊, 直接回车忽略): ");
        List<Customer> list = customerService.findAllCustomers();
        if (!"全部".equals(type) && !type.isEmpty()) {
            list = customerService.findCustomersByType(type);
        }
        if (!name.isEmpty()) {
            list = list.stream().filter(c -> c.getCustomerName().contains(name)).collect(Collectors.toList());
        }
        if (list.isEmpty()) {
            System.out.println("无符合条件的客户");
        } else {
            list.forEach(System.out::println);
        }
    }

    private static void auditBackdown() {
        ConsoleUtils.printSubTitle("退住申请审核");
        List<Backdown> backdowns = customerService.findAllBackdowns().stream()
                .filter(b -> b.getAuditStatus() == 0).collect(Collectors.toList());
        if (backdowns.isEmpty()) {
            System.out.println("没有待审核的退住申请");
            return;
        }
        for (Backdown b : backdowns) {
            System.out.println("申请ID:" + b.getId() + " 客户ID:" + b.getCustomerId() +
                    " 退住类型:" + b.getRetreatType() + " 原因:" + b.getRetreatReason());
        }
        Integer id = ConsoleUtils.readInt("请输入申请ID: ");
        boolean approved = ConsoleUtils.readInt("审核 (1-通过, 0-不通过): ") == 1;
        boolean success = customerService.auditBackdown(id, approved, currentAdmin.getNickname());
        System.out.println(success ? "审核完成" : "审核失败");
    }

    private static void auditOutward() {
        ConsoleUtils.printSubTitle("外出申请审核");
        List<Outward> outwards = customerService.findAllOutwards().stream()
                .filter(o -> o.getAuditStatus() == 0).collect(Collectors.toList());
        if (outwards.isEmpty()) {
            System.out.println("没有待审核的外出申请");
            return;
        }
        for (Outward o : outwards) {
            System.out.println("申请ID:" + o.getId() + " 客户ID:" + o.getCustomerId() +
                    " 外出事由:" + o.getOutgoingReason());
        }
        Integer id = ConsoleUtils.readInt("请输入申请ID: ");
        boolean approved = ConsoleUtils.readInt("审核 (1-通过, 0-不通过): ") == 1;
        boolean success = customerService.auditOutward(id, approved, currentAdmin.getNickname());
        System.out.println(success ? "审核完成" : "审核失败");
    }

    // ==================== 床位管理 ====================
    private static void bedManagement() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printSubTitle("床位管理");
            System.out.println("1. 床位示意图");
            System.out.println("2. 床位调换");
            System.out.println("3. 床位使用详情查询");
            System.out.println("0. 返回上级");
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: showBedMap(); break;
                case 2: changeBed(); break;
                case 3: queryBedDetails(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void showBedMap() {
        ConsoleUtils.printSubTitle("床位示意图");
        Map<String, Integer> stats = bedService.getBedStatistics();
        System.out.println("总床位数:" + stats.get("total") + " 空闲:" + stats.get("free") +
                " 有人:" + stats.get("occupied") + " 外出:" + stats.get("outward"));
        // 按楼层显示
        List<Room> rooms = new dao.RoomDao().findAll();
        Map<String, List<Room>> byFloor = rooms.stream().collect(Collectors.groupingBy(Room::getRoomFloor));
        for (Map.Entry<String, List<Room>> entry : byFloor.entrySet()) {
            System.out.println("\n楼层：" + entry.getKey());
            for (Room r : entry.getValue()) {
                List<Bed> beds = bedService.getRoomsWithBedsByFloor(entry.getKey()).stream()
                        .filter(map -> map.get("roomNo").equals(r.getRoomNo()))
                        .flatMap(map -> ((List<Bed>) map.get("beds")).stream())
                        .collect(Collectors.toList());
                System.out.print("房间 " + r.getRoomNo() + " : ");
                for (Bed b : beds) {
                    String status = b.getBedStatus() == 1 ? "空闲" : (b.getBedStatus() == 2 ? "有人" : "外出");
                    System.out.print(b.getBedNo() + "(" + status + ") ");
                }
                System.out.println();
            }
        }
    }

    private static void changeBed() {
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Integer newBedId = ConsoleUtils.readInt("新床位ID: ");
        boolean success = bedService.changeBed(customerId, newBedId);
        System.out.println(success ? "调换成功" : "调换失败");
    }

    private static void queryBedDetails() {
        String name = ConsoleUtils.readString("客户姓名 (模糊): ");
        LocalDate date = ConsoleUtils.readDate("入住日期 (直接回车忽略): ");
        String status = ConsoleUtils.readString("使用状态 (当前使用/历史使用/全部): ");
        List<BedDetails> details = bedService.queryBedDetails(name, date, status);
        if (details.isEmpty()) {
            System.out.println("无记录");
        } else {
            details.forEach(System.out::println);
        }
    }

    // ==================== 护理管理 ====================
    private static void nurseManagement() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printSubTitle("护理管理");
            System.out.println("1. 护理项目管理");
            System.out.println("2. 护理级别管理");
            System.out.println("3. 客户护理设置");
            System.out.println("4. 护理记录查询");
            System.out.println("0. 返回上级");
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: manageNurseItems(); break;
                case 2: manageNurseLevels(); break;
                case 3: setCustomerNurse(); break;
                case 4: queryNurseRecords(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void manageNurseItems() {
        ConsoleUtils.printSubTitle("护理项目管理");
        System.out.println("1. 查看所有项目");
        System.out.println("2. 添加项目");
        System.out.println("3. 修改项目");
        System.out.println("4. 删除项目");
        int choice = ConsoleUtils.readInt("请选择: ");
        if (choice == 1) {
            nurseService.findAllNurseContents().forEach(System.out::println);
        } else if (choice == 2) {
            NurseContent nc = new NurseContent();
            nc.setSerialNumber(ConsoleUtils.readString("编号: "));
            nc.setNursingName(ConsoleUtils.readString("名称: "));
            nc.setServicePrice(ConsoleUtils.readString("价格: "));
            nc.setMessage(ConsoleUtils.readString("描述: "));
            nc.setStatus(1);
            nc.setExecutionCycle(ConsoleUtils.readString("执行周期: "));
            nc.setExecutionTimes(ConsoleUtils.readString("执行次数: "));
            nurseService.addNurseContent(nc);
            System.out.println("添加成功");
        } else if (choice == 3) {
            Integer id = ConsoleUtils.readInt("项目ID: ");
            Optional<NurseContent> opt = nurseService.findAllNurseContents().stream().filter(c -> c.getId().equals(id)).findFirst();
            if (opt.isEmpty()) { System.out.println("不存在"); return; }
            NurseContent nc = opt.get();
            nc.setNursingName(ConsoleUtils.readStringAllowEmpty("新名称: "));
            nc.setStatus(ConsoleUtils.readInt("新状态 (1启用/2停用): "));
            nurseService.updateNurseContent(nc);
            System.out.println("修改成功");
        } else if (choice == 4) {
            Integer id = ConsoleUtils.readInt("项目ID: ");
            nurseService.deleteNurseContent(id);
            System.out.println("已删除");
        }
    }

    private static void manageNurseLevels() {
        ConsoleUtils.printSubTitle("护理级别管理");
        System.out.println("1. 查看所有级别");
        System.out.println("2. 添加级别");
        System.out.println("3. 修改级别状态");
        System.out.println("4. 配置级别项目");
        int choice = ConsoleUtils.readInt("请选择: ");
        if (choice == 1) {
            nurseService.findAllNurseLevels().forEach(System.out::println);
        } else if (choice == 2) {
            NurseLevel nl = new NurseLevel();
            nl.setLevelName(ConsoleUtils.readString("级别名称: "));
            nl.setLevelStatus(1);
            nurseService.addNurseLevel(nl);
            System.out.println("添加成功");
        } else if (choice == 3) {
            Integer id = ConsoleUtils.readInt("级别ID: ");
            int status = ConsoleUtils.readInt("新状态 (1启用/2停用): ");
            NurseLevel nl = nurseService.findAllNurseLevels().stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
            if (nl == null) { System.out.println("不存在"); return; }
            nl.setLevelStatus(status);
            nurseService.updateNurseLevel(nl);
            System.out.println("修改成功");
        } else if (choice == 4) {
            Integer levelId = ConsoleUtils.readInt("级别ID: ");
            System.out.println("当前已配置项目:");
            nurseService.getItemsByLevelId(levelId).forEach(System.out::println);
            System.out.println("所有可用护理项目:");
            nurseService.findAllNurseContents().forEach(c -> System.out.println(c.getId() + " " + c.getNursingName()));
            Integer itemId = ConsoleUtils.readInt("添加项目ID (0取消): ");
            if (itemId != 0) {
                nurseService.addItemToLevel(levelId, itemId);
                System.out.println("添加成功");
            }
        }
    }

    private static void setCustomerNurse() {
        ConsoleUtils.printSubTitle("客户护理设置");
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Optional<Customer> opt = customerService.findCustomerById(customerId);
        if (opt.isEmpty()) { System.out.println("客户不存在"); return; }
        System.out.println("1. 设置护理级别");
        System.out.println("2. 移除护理级别");
        System.out.println("3. 购买护理项目");
        int choice = ConsoleUtils.readInt("请选择: ");
        if (choice == 1) {
            System.out.println("可用护理级别:");
            nurseService.findAllNurseLevels().forEach(l -> System.out.println(l.getId() + " " + l.getLevelName()));
            Integer levelId = ConsoleUtils.readInt("级别ID: ");
            boolean success = nurseService.setCustomerLevel(customerId, levelId);
            System.out.println(success ? "设置成功" : "设置失败，可能已有级别");
        } else if (choice == 2) {
            boolean success = nurseService.removeCustomerLevel(customerId);
            System.out.println(success ? "移除成功" : "移除失败");
        } else if (choice == 3) {
            System.out.println("可用护理项目:");
            nurseService.findAllNurseContents().forEach(c -> System.out.println(c.getId() + " " + c.getNursingName() + " 价格:" + c.getServicePrice()));
            Integer itemId = ConsoleUtils.readInt("项目ID: ");
            int quantity = ConsoleUtils.readInt("购买数量: ");
            LocalDate maturity = ConsoleUtils.readDate("到期日期");
            boolean success = nurseService.purchaseNurseItem(customerId, itemId, quantity, maturity);
            System.out.println(success ? "购买成功" : "购买失败");
        }
    }

    private static void queryNurseRecords() {
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        List<NurseRecord> records = nurseService.getNurseRecordsByCustomer(customerId);
        if (records.isEmpty()) System.out.println("无护理记录");
        else records.forEach(System.out::println);
    }

    // ==================== 健康管家管理 ====================
    private static void housekeeperManagement() {
        ConsoleUtils.printSubTitle("健康管家管理");
        System.out.println("1. 查看所有健康管家");
        System.out.println("2. 为客户分配管家");
        System.out.println("3. 移除客户管家");
        System.out.println("4. 查看管家服务客户");
        int choice = ConsoleUtils.readInt("请选择: ");
        if (choice == 1) {
            housekeeperService.findAllHousekeepers().forEach(System.out::println);
        } else if (choice == 2) {
            System.out.println("无管家客户:");
            housekeeperService.findCustomersWithoutHousekeeper().forEach(c -> System.out.println(c.getId() + " " + c.getCustomerName()));
            Integer customerId = ConsoleUtils.readInt("客户ID: ");
            System.out.println("健康管家列表:");
            housekeeperService.findAllHousekeepers().forEach(u -> System.out.println(u.getId() + " " + u.getNickname()));
            Integer hid = ConsoleUtils.readInt("管家ID: ");
            boolean success = housekeeperService.assignHousekeeper(customerId, hid);
            System.out.println(success ? "分配成功" : "分配失败");
        } else if (choice == 3) {
            Integer customerId = ConsoleUtils.readInt("客户ID: ");
            boolean success = housekeeperService.removeHousekeeper(customerId);
            System.out.println(success ? "移除成功" : "移除失败");
        } else if (choice == 4) {
            Integer hid = ConsoleUtils.readInt("管家ID: ");
            List<Customer> list = housekeeperService.findCustomersByHousekeeper(hid);
            list.forEach(System.out::println);
        }
    }

    // ==================== 用户管理 ====================
    private static void userManagement() {
        ConsoleUtils.printSubTitle("用户管理");
        System.out.println("1. 查看所有用户");
        System.out.println("2. 添加用户");
        System.out.println("3. 修改用户");
        System.out.println("4. 重置密码");
        System.out.println("5. 删除用户");
        int choice = ConsoleUtils.readInt("请选择: ");
        if (choice == 1) {
            userService.findAllUsers().forEach(System.out::println);
        } else if (choice == 2) {
            User u = new User();
            u.setNickname(ConsoleUtils.readString("姓名: "));
            u.setUsername(ConsoleUtils.readString("账号: "));
            u.setSex(ConsoleUtils.readInt("性别 (0女/1男): "));
            u.setPhoneNumber(ConsoleUtils.readString("手机号: "));
            u.setEmail(ConsoleUtils.readString("邮箱: "));
            u.setRoleId(ConsoleUtils.readInt("角色 (1管理员/2健康管家): "));
            User added = userService.addUser(u);
            System.out.println(added != null ? "添加成功，默认密码为手机号后6位" : "添加失败");
        } else if (choice == 3) {
            Integer id = ConsoleUtils.readInt("用户ID: ");
            Optional<User> opt = userService.findUserById(id);
            if (opt.isEmpty()) { System.out.println("不存在"); return; }
            User u = opt.get();
            u.setNickname(ConsoleUtils.readStringAllowEmpty("新姓名: "));
            u.setPhoneNumber(ConsoleUtils.readStringAllowEmpty("新手机号: "));
            userService.updateUser(u);
            System.out.println("修改成功");
        } else if (choice == 4) {
            Integer id = ConsoleUtils.readInt("用户ID: ");
            boolean success = userService.resetPassword(id);
            System.out.println(success ? "密码已重置为手机号后6位" : "失败");
        } else if (choice == 5) {
            Integer id = ConsoleUtils.readInt("用户ID: ");
            userService.deleteUser(id);
            System.out.println("已删除");
        }
    }

    // ==================== 膳食管理 ====================
    private static void mealManagement() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printSubTitle("膳食管理");
            System.out.println("1. 食品管理");
            System.out.println("2. 客户饮食喜好");
            System.out.println("3. 膳食日历");
            System.out.println("0. 返回上级");
            int choice = ConsoleUtils.readInt("请选择: ");
            switch (choice) {
                case 1: foodManagement(); break;
                case 2: preferenceManagement(); break;
                case 3: mealCalendarManagement(); break;
                case 0: return;
            }
        }
    }

    private static void foodManagement() {
        ConsoleUtils.printSubTitle("食品管理");
        System.out.println("1. 查看所有食品");
        System.out.println("2. 添加食品");
        System.out.println("3. 修改食品");
        System.out.println("4. 删除食品");
        int ch = ConsoleUtils.readInt("请选择: ");
        if (ch == 1) {
            mealService.findAllFoods().forEach(System.out::println);
        } else if (ch == 2) {
            Food f = new Food();
            f.setFoodName(ConsoleUtils.readString("食品名称: "));
            f.setFoodType(ConsoleUtils.readString("类型: "));
            f.setPrice(java.math.BigDecimal.valueOf(ConsoleUtils.readInt("价格: ")));
            f.setIsHalal(ConsoleUtils.readInt("是否清真 (1是/0否): "));
            mealService.addFood(f);
            System.out.println("添加成功");
        } else if (ch == 3) {
            Integer id = ConsoleUtils.readInt("食品ID: ");
            Optional<Food> opt = mealService.findAllFoods().stream().filter(f -> f.getId().equals(id)).findFirst();
            if (opt.isEmpty()) { System.out.println("不存在"); return; }
            Food f = opt.get();
            f.setFoodName(ConsoleUtils.readStringAllowEmpty("新名称: "));
            mealService.updateFood(f);
            System.out.println("修改成功");
        } else if (ch == 4) {
            Integer id = ConsoleUtils.readInt("食品ID: ");
            mealService.deleteFood(id);
            System.out.println("已删除");
        }
    }

    private static void preferenceManagement() {
        Integer customerId = ConsoleUtils.readInt("客户ID: ");
        Optional<CustomerPreference> opt = mealService.findPreferenceByCustomerId(customerId);
        if (opt.isPresent()) {
            System.out.println("现有喜好: " + opt.get());
        }
        String pref = ConsoleUtils.readStringAllowEmpty("饮食喜好 (如:少糖): ");
        String att = ConsoleUtils.readStringAllowEmpty("注意事项: ");
        CustomerPreference cp = new CustomerPreference();
        cp.setCustomerId(customerId);
        cp.setPreferences(pref);
        cp.setAttention(att);
        mealService.savePreference(cp);
        System.out.println("保存成功");
    }

    private static void mealCalendarManagement() {
        ConsoleUtils.printSubTitle("膳食日历");
        String weekDay = ConsoleUtils.readString("星期几 (周一/周二...): ");
        List<Meal> meals = mealService.findMealsByWeekDay(weekDay);
        meals.forEach(m -> System.out.println("餐次:" + m.getMealType() + " 食品ID:" + m.getFoodId()));
        System.out.println("1. 安排餐次");
        System.out.println("2. 移除餐次");
        int ch = ConsoleUtils.readInt("请选择: ");
        if (ch == 1) {
            int mealType = ConsoleUtils.readInt("餐次 (1早餐/2午餐/3晚餐): ");
            int foodId = ConsoleUtils.readInt("食品ID: ");
            Meal m = new Meal();
            m.setWeekDay(weekDay);
            m.setMealType(mealType);
            m.setFoodId(foodId);
            mealService.scheduleMeal(m);
            System.out.println("安排成功");
        } else if (ch == 2) {
            int mealType = ConsoleUtils.readInt("餐次: ");
            mealService.removeMealSchedule(weekDay, mealType);
            System.out.println("移除成功");
        }
    }

    // ==================== 统计信息 ====================
    private static void showStatistics() {
        ConsoleUtils.printSubTitle("统计信息");
        Map<String, Integer> bedStats = statisticsService.getBedStatistics();
        System.out.println("床位统计：总 " + bedStats.get("total") + "，空闲 " + bedStats.get("free") +
                "，有人 " + bedStats.get("occupied") + "，外出 " + bedStats.get("outward"));
        Map<String, Integer> custStats = statisticsService.getCustomerStatistics();
        System.out.println("客户统计：总数 " + custStats.get("total") + "，自理老人 " + custStats.get("selfCare") +
                "，护理老人 " + custStats.get("nursingCare"));
        long recordCount = statisticsService.getNurseRecordCount(null);
        System.out.println("护理记录总数：" + recordCount);
    }
}