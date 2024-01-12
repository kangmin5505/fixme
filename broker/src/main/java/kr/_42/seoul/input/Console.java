package kr._42.seoul.input;

import java.util.Scanner;

public class Console {
    private static final Scanner scanner = new Scanner(System.in);

    private Console() {}

    public static String readLine() {
        System.out.print("> ");

        return scanner.nextLine();
    }

    public static void usage() {
        System.out.println("Usage");
        System.out.println("\t[sell|buy] [instrument] [quantity] [market] [price]");
        System.out.println("\texit");
    }

    public static void invalidInput() {
        System.out.println("Invalid Input");
    }

    public static void exit() {
        System.out.println("Bye");
    }
}
