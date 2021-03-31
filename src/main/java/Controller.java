import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Controller {
    static VersionControl vcs;
    private static String vcsDataName = "vcsData.data";
    private static String notInit = "Vcs is not initialized";

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(vcsDataName);
        try {
            mapper.writeValue(f, vcs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean tryLoad() {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(vcsDataName);
        if (f.isFile()) {
            try {
                vcs = mapper.readValue(f, VersionControl.class);
                return true;
            } catch (IOException e) {
                //e.printStackTrace();
                return false;
            }
        }
        return false;

    }

    public static String init() {
        if (vcs != null) {
            return "Error: vsc already created";
        } else {
            vcs = VersionControl.init(vcsDataName);
            vcs.commit("Start!");
            return "vcs initialized";
        }
    }

    public static String commit(String commit) {
        return (vcs != null) ? vcs.commit(commit) : notInit;
    }

    public static String status() {
        return (vcs != null) ? vcs.status() : notInit;
    }

    public static String log() {
        return (vcs != null) ? vcs.log() : notInit;
    }

    public static String diff(int first, int second) {
        return (vcs != null) ? vcs.diff(first, second) : notInit;
    }

    public static String checkout(int number) {
        return (vcs != null) ? vcs.checkout(number) : notInit;
    }
}
