package view;

import pojo.User;
import service.AuthService;
import service.UserService;
import view.utils.ConsoleUtils;
import tools.DataInitializer;

public class LoginView {

    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();

    public static void main(String[] args) {
        DataInitializer.init();
        start();
    }

    public static void start() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printTitle("东软颐养中心管理系统");
            System.out.println("         请登录");
            ConsoleUtils.printSeparator();
            System.out.println("0. 退出程序");
            System.out.println("1. 登录");
            System.out.print("请选择: ");
            String choice = ConsoleUtils.readStringAllowEmpty("");
            if ("0".equals(choice)) {
                System.out.println("感谢使用，再见！");
                System.exit(0);
            } else if ("1".equals(choice)) {
                // 继续登录流程
                String username = ConsoleUtils.readString("用户名: ");
                String password = ConsoleUtils.readString("密码: ");
                User user = authService.login(username, password);
                if (user == null) {
                    System.out.println("\n用户名或密码错误，请重新登录！");
                    ConsoleUtils.pressEnterToContinue();
                    continue;
                }
                if (user.getRoleId() == 1) {
                    AdminView.show(user);
                } else if (user.getRoleId() == 2) {
                    HealthWorkerView.show(user);
                } else {
                    System.out.println("未知角色，请联系管理员。");
                    ConsoleUtils.pressEnterToContinue();
                }
            } else {
                System.out.println("无效输入，请重新选择。");
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }
}