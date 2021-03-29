import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Task {
    //TODO убрать сущность имени из vcs
    public static void main(String[] args) throws IOException {
        StatusExec out = executeCommand(args);
        switch (out) {
            case PARAMERROR:
                System.out.println("Invalid count of params");
                break;
            case UNKNOWN:
                System.out.println("Unknown command.");
                break;
        }
    }

    static StatusExec executeCommand(String[] command) {
        String name = command[0] + ".vcsdata";
        boolean loaded = Controller.tryLoad(name);
        StatusExec st = StatusExec.OK;
        String message = null;

        if (command.length < 2)
            return StatusExec.UNKNOWN;

        switch (command[1]) {
            case "init":
                try {
                    if (loaded)
                        message = "Vcs already created in this folder";
                    else
                        message = Controller.init(name);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "commit":
                if (command.length != 3)
                    return StatusExec.PARAMERROR;
                message = Controller.commit(name, command[2]);
                break;
            case "status":
                message = Controller.status(name);
                break;
            case "log":
                message = Controller.log(name);
                break;
            case "diff":
                if (command.length != 4)
                    return StatusExec.PARAMERROR;
                message = Controller.diff(name, Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                break;
            case "checkout":
                if (command.length != 3)
                    return StatusExec.PARAMERROR;
                message = Controller.checkout(name, Integer.parseInt(command[2]));
                break;
            default:
                st = StatusExec.UNKNOWN;
                break;
        }
        if (message != null)
            System.out.println(message);
        if (Controller.vcs != null)
            Controller.save(name);
        return st;
    }

    enum StatusExec {
        UNKNOWN,
        PARAMERROR,
        OK
    }
}
