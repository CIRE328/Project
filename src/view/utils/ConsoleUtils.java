package view.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void printSeparator() {
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    public static void printTitle(String title) {
        printSeparator();
        System.out.println("          " + title);
        printSeparator();
    }

    public static void printSubTitle(String subtitle) {
        System.out.println("\n--- " + subtitle + " ---");
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readStringAllowEmpty(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入一个整数！");
            }
        }
    }

    public static Integer readIntOrCancel(String prompt) {
        System.out.print(prompt + " (输入0取消): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("输入无效，请重新输入。");
            return readIntOrCancel(prompt);
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (格式 yyyy-MM-dd): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("日期格式错误，请重新输入。");
            }
        }
    }

    public static void pressEnterToContinue() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }

    public static void clearScreen() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n");
    }
}
