import java.util.Scanner;

/**
 * Created by Dmitriy on 08.02.2018.
 */
public class Main {
    public static void main(String[] args) {
        boolean checker = false;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Would you like to start server or client?");
        while (!checker) {
            String choice = scanner.nextLine();
            switch (choice) {
                case "server":
                    new ChatAppServer();
                    checker = true;
                    break;
                case "client":
                    new ChatAppClient();
                    checker = true;
                    break;
                default:
                    System.out.println("Unknown word, please make a choice: server or client");
                    break;
            }
        }
    }
}