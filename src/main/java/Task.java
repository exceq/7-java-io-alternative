import java.util.Optional;

public class Task {
    public static void main(String[] args) {
        StatusExec out = executeCommand(args);
        switch (out) {
            case PARAMERROR:
                System.out.println("Error: Invalid count of params");
                break;
            case PARSEINTERROR:
                System.out.println("Error: Arguments must be numbers");
                break;
            case UNKNOWN:
                System.out.println("Error: Unknown command.");
                break;
        }
    }

    static StatusExec executeCommand(String[] command) {
        boolean loaded = Controller.tryLoad();
        StatusExec st = StatusExec.OK;
        String message = null;
        int len = command.length;

        if (len < 1)
            return StatusExec.UNKNOWN;

        switch (command[0]) {
            case "init":
                message = loaded ? "Vcs already created in this folder" : Controller.init();
                break;
            case "commit":
                if (len != 2)
                    return StatusExec.PARAMERROR;
                message = Controller.commit(command[1]);
                break;
            case "status":
                if (len != 1)
                    return StatusExec.PARAMERROR;
                message = Controller.status();
                break;
            case "log":
                if (len != 1)
                    return StatusExec.PARAMERROR;
                message = Controller.log();
                break;
            case "diff":
                if (command.length != 3)
                    return StatusExec.PARAMERROR;
                Optional<Integer> first = tryParse(command[1]);
                Optional<Integer> second = tryParse(command[2]);
                if (first.isPresent() && second.isPresent())
                    message = Controller.diff(first.get(), second.get());
                else
                    return StatusExec.PARSEINTERROR;
                break;
            case "checkout":
                if (command.length != 2)
                    return StatusExec.PARAMERROR;
                first = tryParse(command[1]);
                if (first.isPresent())
                    message = Controller.checkout(first.get());
                else
                    return StatusExec.PARSEINTERROR;
                break;
            default:
                st = StatusExec.UNKNOWN;
                break;
        }
        if (message != null)
            System.out.println(message);
        if (Controller.vcs != null)
            Controller.save();
        return st;
    }

    private static Optional<Integer> tryParse(String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    enum StatusExec {
        UNKNOWN,
        PARSEINTERROR,
        PARAMERROR,
        OK
    }
}
