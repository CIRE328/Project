package view;

import pojo.User;
import service.AuthService;
import service.UserService;
import view.utils.ConsoleUtils;

public class LoginView {

    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printTitle("东软颐养中心管理系统");
            System.out.println("         请登录");
            ConsoleUtils.printSeparator();

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
        }
    }
}