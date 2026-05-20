package tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import pojo.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化工具：检查 data/ 目录和所有必需的 JSON 文件，
 * 如果不存在或为空，则创建并写入默认数据。
 */
public class DataInitializer {

    private static final String DATA_DIR = "data/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 所有需要初始化的文件列表（文件名 -> 对应的实体类）
    private static final String[][] FILES = {
            {"users.json", "pojo.User"},
            {"rooms.json", "pojo.Room"},
            {"beds.json", "pojo.Bed"},
            {"beddetails.json", "pojo.BedDetails"},
            {"nursecontents.json", "pojo.NurseContent"},
            {"nurselevels.json", "pojo.NurseLevel"},
            {"nurselevelitems.json", "pojo.NurseLevelItem"},
            {"customers.json", "pojo.Customer"},
            {"outwards.json", "pojo.Outward"},
            {"backdowns.json", "pojo.Backdown"},
            {"customernurseitems.json", "pojo.CustomerNurseItem"},
            {"nurserecords.json", "pojo.NurseRecord"},
            {"foods.json", "pojo.Food"},
            {"customerpreferences.json", "pojo.CustomerPreference"},
            {"meals.json", "pojo.Meal"},
            {"roles.json", "pojo.Role"},
            {"menus.json", "pojo.Menu"},
            {"rolemenus.json", "pojo.RoleMenu"}
    };

    public static void init() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        for (String[] fileInfo : FILES) {
            String fileName = fileInfo[0];
            String className = fileInfo[1];
            File file = new File(DATA_DIR + fileName);
            boolean needCreate = !file.exists() || file.length() == 0;
            if (!needCreate) {
                // 尝试读取，如果读取失败也重新创建
                try {
                    CollectionType listType = MAPPER.getTypeFactory()
                            .constructCollectionType(ArrayList.class, Class.forName(className));
                    MAPPER.readValue(file, listType);
                } catch (Exception e) {
                    System.out.println("文件 " + fileName + " 损坏，将重新生成");
                    needCreate = true;
                }
            }
            if (needCreate) {
                createDefaultFile(fileName, className);
            }
        }
    }

    private static String getDefaultJsonContent(String fileName) {
        switch (fileName) {
            case "users.json":
                return """
[
  {
    "id": 1,
    "nickname": "系统管理员",
    "username": "admin",
    "password": "admin",
    "sex": 1,
    "email": "admin@example.com",
    "phoneNumber": "13800000000",
    "roleId": 1,
    "createTime": "2025-01-01T00:00:00",
    "createBy": 1,
    "updateTime": "2025-01-01T00:00:00",
    "updateBy": 1,
    "isDeleted": false
  }
]
""";
            case "rooms.json":
                return """
[
  {"id": 1, "roomFloor": "1F", "roomNo": 101, "isDeleted": false},
  {"id": 2, "roomFloor": "1F", "roomNo": 102, "isDeleted": false},
  {"id": 3, "roomFloor": "2F", "roomNo": 201, "isDeleted": false}
]
""";
            case "beds.json":
                return """
[
  {"id": 1, "roomNo": 101, "bedNo": "A床", "bedStatus": 1, "remarks": "", "isDeleted": false},
  {"id": 2, "roomNo": 101, "bedNo": "B床", "bedStatus": 1, "remarks": "", "isDeleted": false},
  {"id": 3, "roomNo": 102, "bedNo": "A床", "bedStatus": 1, "remarks": "", "isDeleted": false},
  {"id": 4, "roomNo": 201, "bedNo": "A床", "bedStatus": 1, "remarks": "", "isDeleted": false}
]
""";
            case "nursecontents.json":
                return """
[
  {"id": 1, "serialNumber": "HL001", "nursingName": "翻身拍背", "servicePrice": "20", "message": "", "status": 1, "executionCycle": "每天", "executionTimes": "2次", "isDeleted": false},
  {"id": 2, "serialNumber": "HL002", "nursingName": "协助洗浴", "servicePrice": "50", "message": "", "status": 1, "executionCycle": "每周", "executionTimes": "2次", "isDeleted": false}
]
""";
            case "nurselevels.json":
                return """
[
  {"id": 1, "levelName": "一级护理", "levelStatus": 1, "isDeleted": false},
  {"id": 2, "levelName": "二级护理", "levelStatus": 1, "isDeleted": false}
]
""";
            case "nurselevelitems.json":
                return """
[
  {"id": 1, "levelId": 1, "itemId": 1, "isDeleted": false},
  {"id": 2, "levelId": 1, "itemId": 2, "isDeleted": false},
  {"id": 3, "levelId": 2, "itemId": 1, "isDeleted": false}
]
""";
            // 其他所有文件默认为空数组
            default:
                return "[]";
        }
    }

    @SuppressWarnings("unchecked")
    private static void createDefaultFile(String fileName, String className) {
        File file = new File(DATA_DIR + fileName);
        String jsonContent = getDefaultJsonContent(fileName);
        if (jsonContent == null) {
            jsonContent = "[]"; // 默认空数组
        }
        try {
            java.nio.file.Files.write(file.toPath(), jsonContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            System.out.println("已创建默认文件: " + fileName);
        } catch (IOException e) {
            System.err.println("创建文件失败: " + fileName);
            e.printStackTrace();
        }
    }

    private static List<?> getDefaultData(String fileName, String className) {
        switch (fileName) {
            case "users.json":
                return defaultUsers();
            case "rooms.json":
                return defaultRooms();
            case "beds.json":
                return defaultBeds();
            case "nursecontents.json":
                return defaultNurseContents();
            case "nurselevels.json":
                return defaultNurseLevels();
            case "nurselevelitems.json":
                return defaultNurseLevelItems();
            // 其他文件返回空列表
            default:
                return new ArrayList<>();
        }
    }

    // ========== 默认数据生成 ==========
    private static List<User> defaultUsers() {
        List<User> list = new ArrayList<>();
        User admin = new User();
        admin.setId(1);
        admin.setNickname("系统管理员");
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setSex(1);
        admin.setEmail("admin@example.com");
        admin.setPhoneNumber("13800000000");
        admin.setRoleId(1);
        admin.setCreateTime(LocalDateTime.now());
        admin.setCreateBy(1);
        admin.setUpdateTime(LocalDateTime.now());
        admin.setUpdateBy(1);
        admin.setIsDeleted(false);
        list.add(admin);
        return list;
    }

    private static List<Room> defaultRooms() {
        List<Room> list = new ArrayList<>();
        list.add(new Room(1, "1F", 101, false));
        list.add(new Room(2, "1F", 102, false));
        list.add(new Room(3, "2F", 201, false));
        list.add(new Room(4, "2F", 202, false));
        return list;
    }

    private static List<Bed> defaultBeds() {
        List<Bed> list = new ArrayList<>();
        list.add(new Bed(1, 101, 1, "", "A床", false));
        list.add(new Bed(2, 101, 1, "", "B床", false));
        list.add(new Bed(3, 102, 1, "", "A床", false));
        list.add(new Bed(4, 201, 1, "", "A床", false));
        list.add(new Bed(5, 202, 1, "", "A床", false));
        list.add(new Bed(6, 202, 1, "", "B床", false));
        return list;
    }

    private static List<NurseContent> defaultNurseContents() {
        List<NurseContent> list = new ArrayList<>();
        list.add(new NurseContent(1, "HL001", "翻身拍背", "20", "预防压疮", 1, "每天", "2次", false));
        list.add(new NurseContent(2, "HL002", "协助洗浴", "50", "", 1, "每周", "2次", false));
        list.add(new NurseContent(3, "HL003", "喂饭", "30", "", 1, "每天", "3次", false));
        return list;
    }

    private static List<NurseLevel> defaultNurseLevels() {
        List<NurseLevel> list = new ArrayList<>();
        list.add(new NurseLevel(1, "一级护理", 1, false));
        list.add(new NurseLevel(2, "二级护理", 1, false));
        return list;
    }

    private static List<NurseLevelItem> defaultNurseLevelItems() {
        List<NurseLevelItem> list = new ArrayList<>();
        // 一级护理包含翻身拍背和协助洗浴
        list.add(new NurseLevelItem(1, 1, 1, false));
        list.add(new NurseLevelItem(2, 1, 2, false));
        // 二级护理只包含翻身拍背
        list.add(new NurseLevelItem(3, 2, 1, false));
        return list;
    }
}