package cs4432.project1.utils;

import java.util.Scanner;

public class InputManager {
    public static String scanCommand() {
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }
}
