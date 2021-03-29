import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

public class Controller {
    static VersionControl vcs;

    public static void save(String vcsName) {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(vcsName);
        try {
            mapper.writeValue(f, vcs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean tryLoad(String name) {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(name);
        if (f.isFile()) {
            try {
                vcs = mapper.readValue(f, VersionControl.class);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }

    public static String init(String name) {
        if (vcs != null) {
            return "qeError: vsc " + name + " already created";
        } else {
            vcs = VersionControl.init(name);
            vcs.commit("Start!");
            return name + " initialized";
        }
    }

    public static String commit(String vcsName, String commit) {
        return (vcs != null) ? vcs.commit(commit) : vcsName + " isn't existing";
    }

    public static String status(String vcsName) {
        return (vcs != null) ? vcs.status() : vcsName + " isn't existing";
    }

    public static String log(String vcsName) {
        return (vcs != null) ? vcs.log() : vcsName + " isn't existing";
    }

    public static String diff(String vcsName, int first, int second) {
        return (vcs != null) ? vcs.diff(first, second) : vcsName + " isn't existing";
    }

    public static String checkout(String vcsName, int number) {
        return (vcs != null) ? vcs.checkout(number) : vcsName + " isn't existing";
    }
}
