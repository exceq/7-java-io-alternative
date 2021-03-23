import java.util.Arrays;
import java.util.Scanner;

public class Task {
    public static void main(String[] args) {


        Scanner in = new Scanner(System.in);

        printMenu();
        bp:
        while (true) {
            String command = in.nextLine();
            StatusExec out = executeCommand(command);

            switch (out){
                case PARAMERROR:
                    System.out.println("Invalid count of params");
                    break;
                case CONTINUE:
                    continue bp;
                case UNKNOWN:
                    System.out.println("Unknown command.");
                    break;
                case BREAK:
                    System.out.println("Saving...\nShutting down.");
                    break bp;
            }
        }
    }

    static void printMenu() {
        System.out.println("Available commands:\n" +
                "1. <vsc name> init\n" +
                "2. <vsc name> commit\n" +
                "3. <vsc name> status\n" +
                "4. <vsc name> log\n" +
                "5. <vsc name> diff <revision1 revision2>\n" +
                "6. <vsc name> checkout <revision>\n");
    }

    static StatusExec executeCommand(String command) {
        String[] a = command.split(" ");
        StatusExec st = StatusExec.CONTINUE;
        String message = "";
        switch (a[1]){
            case "init":
                try {
                    message = Controller.init(a[0]);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case "commit":
                message = Controller.commit(a[0], a[2]);
                break;
            case "status":
                message = Controller.status(a[0]);
                break;
            case "log":
                message = Controller.log(a[0]);
                break;
            case "diff":
                message = Controller.diff(a[0], Integer.parseInt(a[2]), Integer.parseInt(a[3]));
                break;
            case "checkout":
                message = Controller.checkout(a[0], Integer.parseInt(a[2]));
                break;
            default:
                message = "Unknown command";

        }
        System.out.println(message);
        return st;
    }

    enum StatusExec {
        UNKNOWN,
        BREAK,
        CONTINUE,
        PARAMERROR
    }
}
