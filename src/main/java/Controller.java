import java.util.HashMap;

public class Controller {
    static HashMap<String, VersionControl> vscs = new HashMap<>();

    public static String init(String name) {
        if (vscs.containsKey(name)){
            String message = "qeError: vsc " + name + " already created";
            return message;
        }
        else {
            VersionControl vsc = VersionControl.init(name);
            vscs.put(name, vsc);
            return name + " initialized";
        }
    }

    public static String commit(String vscName, String commit){
        return vscs.get(vscName).commit(commit);
    }

    public static String status(String vscName){
        return vscs.get(vscName).status();
    }

    public static String log(String vscName){
        return vscs.get(vscName).log();
    }

    public static String diff(String vscName, int first, int second){
        return vscs.get(vscName).diff(first, second);
    }

    public static String checkout(String vscName, int number){
        return vscs.get(vscName).checkout(number);
    }
}
