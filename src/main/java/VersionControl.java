import lombok.Data;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@Data
public class VersionControl {
    private File fileVsc;
    private String vscName;
    private ArrayList<Revision> revisions = new ArrayList<>();
    private ArrayList<MyFile> files = new ArrayList<>();

    public VersionControl() {
    }

    private VersionControl(String vscName) {
        try {
            File file = new File(vscName);
            file.createNewFile();
            this.fileVsc = file;
            this.vscName = file.getName();
        } catch (IOException e) {
            System.out.println("Error: failed to create file");
        }
    }

    public static VersionControl init(String vscName) {
        return new VersionControl(vscName);
    }

    public String commit(String commit) {
        File[] files = getCurrentFiles();
        Revision rev = createRevision(files, revisions.size() + 1, commit);
        revisions.add(rev);
        this.files.addAll(rev.created);
        if (rev.deleted != null) {
            for (MyFile file : rev.deleted) {
                this.files.removeIf(x -> x.name.equals(file.name));
            }
        }
        return "Created revision " + (revisions.size());
    }

    private Revision createRevision(File[] files, int revisionId, String commit) {
        ArrayList<MyFile> currentFiles = MyFile.getMyFileList(new ArrayList<>(Arrays.asList(files)));
        if (revisions.size() == 0)
            return new Revision(1, commit, currentFiles, null, null);

        ArrayList<MyFile> curCopy = new ArrayList<>(currentFiles);
        ArrayList<MyFile> lastFiles = new ArrayList<>(this.files);
        ArrayList<MyFile> changedBase = new ArrayList<>(currentFiles);

        removeAllByName(curCopy, lastFiles);        //created
        removeAllByName(lastFiles, currentFiles);   //deleted
        removeAllByName(changedBase, lastFiles);    //probably changed -> check last modify
        removeAllByName(changedBase, curCopy);
        /*var a = changedBase.get(0).date;
        var b = revisions.get(revisions.size() - 1).date.getTime();
        boolean f = a > b;*/
        var changed = changedBase
                .stream()
                .filter(x -> x.date > revisions.get(revisions.size() - 1).date.getTime())
                .collect(Collectors.toCollection(ArrayList::new));
        return new Revision(revisionId, commit, curCopy, lastFiles, changed);
    }

    void removeAllByName(ArrayList<MyFile> from, ArrayList<MyFile> thiss) {
        for (MyFile file : thiss)
            from.removeIf(x -> x.name.equals(file.name));
    }

    private File[] getCurrentFiles() {
        String jarName = "";
        try {
            jarName = new File(getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getName();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        File parent = new File(fileVsc.getAbsolutePath()).getParentFile();
        File[] files = parent.listFiles();
        String finalJarName = jarName; //шоб компилер не ругался
        return Arrays.stream(files).filter(x -> !x.getName().equals(vscName) && !x.getName().equals(finalJarName)).toArray(File[]::new);
    }

    public String status() {
        var revs = new ArrayList<Revision>();
        revs.add(createRevision(getCurrentFiles(), -1, ""));
        return getChanges(revs);
    }

    private String getChanges(ArrayList<Revision> revs) {
        var c = new ArrayList<MyFile>();
        var d = new ArrayList<MyFile>();
        var m = new ArrayList<MyFile>();
        for (Revision r : revs) {
            if (r.created != null) c.addAll(r.created);
            if (r.deleted != null) d.addAll(r.deleted);
            if (r.modified != null) m.addAll(r.modified);
        }
        var cCopy = new ArrayList<>(c);
        removeAllByName(c, d);
        var toDel = new ArrayList<MyFile>();
        var toIns = new ArrayList<MyFile>();

        for (MyFile file : c) {
            var match = m.stream().filter(x -> file.name.equals(x.name)).findFirst();
            if (match.isPresent()) {
                MyFile ge = match.get();
                toIns.add(ge);
                toDel.add(file);
            }
        }
        c.removeAll(toDel);
        c.addAll(toIns);
        removeAllByName(m, d);
        removeAllByName(m, c);
        removeAllByName(d, cCopy);

        /*c.removeAll(d);
        c.removeAll(m);
        m.removeAll(d);
        m.removeAll(c);
        d.removeAll(cCopy);*/
        StringBuilder mes = new StringBuilder();
        if (c.size() == 0 && d.size() == 0 && m.size() == 0)
            return "No changes.";
        c.sort(Comparator.comparing(x -> x.name));
        d.sort(Comparator.comparing(x -> x.name));
        m.sort(Comparator.comparing(x -> x.name));
        appendFilesToBuilder(mes, c, "Created", '+');
        appendFilesToBuilder(mes, d, "Deleted", '-');
        appendFilesToBuilder(mes, m, "Modified", '*');
        return mes.toString();
    }

    private void appendFilesToBuilder(StringBuilder message, ArrayList<MyFile> files, String filesName, Character separator) {
        message.append(filesName).append(":\n");
        for (MyFile file : files) {
            message.append(String.format("%c %s (%d bytes)\n", separator, file.name, file.size));
        }
    }

    public String log() {
        //TODO log
        return "";
    }

    public String diff(int first, int second) {
        if (first == second)
            return "No changes.";
        int one = Math.min(first, second);
        int two = Math.max(first, second);
        if (one < 1)
            return "The revision number must be greater than 0";
        if (two > revisions.size())
            return "Revision " + two + " hasn't been created yet.";

        return getChanges(new ArrayList<>(revisions.subList(one, two)));
    }

    public String checkout(int number) {
        //TODO checkout
        return "";
    }
}
