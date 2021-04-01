import lombok.Data;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class VersionControl {
    private File vcsFile;
    private String vcsDataName;
    private ArrayList<Revision> revisions = new ArrayList<>();
    private ArrayList<MyFile> files = new ArrayList<>();
    private int currentRevision;

    public VersionControl() {
    }

    private VersionControl(String vcsDataName) {
        try {
            File file = new File(vcsDataName);
            file.createNewFile();
            this.vcsFile = file;
            this.vcsDataName = file.getName();
        } catch (IOException e) {
            System.out.println("Error: failed to create file");
        }
    }

    public static VersionControl init(String vscDataName) {
        return new VersionControl(vscDataName);
    }

    public String commit(String commit) {
        if (currentRevision != revisions.size() - 1 && currentRevision != 0)
            return "Do \"checkout " + (revisions.size() - 1) + "\" and try again";
        File[] files = getCurrentFiles();
        Revision rev = createRevision(files, revisions.size(), commit);
        revisions.add(rev);
        this.files.addAll(rev.created);
        if (rev.deleted != null)
            rev.deleted.forEach(d -> this.files.removeIf(f -> f.name.equals(d.name)));
        currentRevision = rev.revisionId;
        return "Created revision " + rev.revisionId;
    }

    public String status() {
        return printChanges(createRevision(getCurrentFiles(), -1, ""));
    }

    public String log() {
        if (revisions.size() == 1)
            return "no log";
        StringBuilder builder = new StringBuilder();
        for (Revision rev : revisions.subList(1, revisions.size())) {
            builder.append(String.format("Revision %d\n", rev.revisionId));
            builder.append(String.format("Date: %s\n", rev.date));
            builder.append(String.format("Files: %d\n", rev.countFiles));
            builder.append(String.format("Comment: %s\n\n", rev.comment));
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    public String diff(int first, int second) {
        if (first == second)
            return "No changes.";
        int one = Math.min(first, second);
        int two = Math.max(first, second);
        if (one < 1)
            return "The revision number must be greater than 0";
        if (two > revisions.size() - 1)
            return "Revision " + two + " hasn't been created yet.";

        return printChanges(getResultRevision(new ArrayList<>(revisions.subList(one + 1, two + 1))));
    }

    public String checkout(int number) {
        if (number < 1)
            return "The revision number must be greater than 0";
        if (number > revisions.size() - 1)
            return "Revision " + number + " hasn't been created yet.";
        if (number == currentRevision)
            return "You are already on Revision " + number;
        boolean back = number < currentRevision;

        var b = getResultRevision(new ArrayList<>(
                revisions.subList(Math.min(number+1, currentRevision+1), Math.max(number+1, currentRevision+1))));
        if (back) {
            var t = new ArrayList<MyFile>(b.created);
            b.created = new ArrayList<>(b.deleted);
            b.deleted = t;
        }
        b.modified = b.modified.stream().distinct().collect(Collectors.toCollection(ArrayList::new));

        deleteFiles(b.deleted, true);
        deleteFiles(b.deleted, false);

        modifyFiles(b.created, true);
        modifyFiles(b.modified, false);
        currentRevision = number;
        return printChanges(b);
    }

    private void deleteFiles(ArrayList<MyFile> files, boolean deleteCommonFiles){
        files.stream()
                .filter(x-> x.isDirectory ^ deleteCommonFiles)
                .sorted(getComp().reversed())
                .forEach(file -> {
                    try {
                        Files.delete(Paths.get(file.path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private int count(String str, char c){
        return (int)new StringBuilder(str).chars().filter(x->x==(int)c).count();
    }

    private Comparator<MyFile> getComp(){
        return Comparator.comparing(f -> count(f.path,'\\'));
    }
    private void modifyFiles(ArrayList<MyFile> files, boolean create) {
        files.stream()
                .filter(x -> x.isDirectory && !new File(x.path).exists())
                .sorted(getComp())
                .map(x -> Paths.get(x.path))
                .forEach(dir -> {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        files.stream().filter(x -> !x.isDirectory).forEach(file -> {
            File f = new File(file.path);
            try {
                if (create)
                    f.createNewFile();
                FileWriter writer = new FileWriter(f, false);
                writer.write(file.content);
                writer.flush();
                writer.close();
                f.setLastModified(file.date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Revision createRevision(File[] files, int revisionId, String commit) {
        ArrayList<MyFile> currentFiles = MyFile.getMyFileList(new ArrayList<>(Arrays.asList(files)));
        if (revisions.size() == 0)
            return new Revision(0, commit, currentFiles.size(), currentFiles, null, null);

        ArrayList<MyFile> curCopy = new ArrayList<>(currentFiles);
        ArrayList<MyFile> lastFiles = new ArrayList<>(this.files);
        ArrayList<MyFile> changedBase = new ArrayList<>(currentFiles);

        removeAllByPath(curCopy, lastFiles);        //created
        removeAllByPath(lastFiles, currentFiles);   //deleted
        removeAllByPath(changedBase, lastFiles);    //probably changed -> check last modify
        removeAllByPath(changedBase, curCopy);
        var changed = changedBase
                .stream()
                .filter(x -> x.date > revisions.get(revisions.size() - 1).date.getTime())
                .collect(Collectors.toCollection(ArrayList::new));
        return new Revision(revisionId, commit, currentFiles.size(), curCopy, lastFiles, changed);
    }

    void removeAllByPath(ArrayList<MyFile> from, ArrayList<MyFile> it) {
        it.forEach(file -> from.removeIf(x -> x.path.equals(file.path)));
    }

    private File[] getCurrentFiles() {
        File jarnik = new File("vcs.jar");
        File parent = jarnik.getAbsoluteFile().getParentFile();
        var ignore = new ArrayList<String>(List.of(jarnik.getName(), parent.getName(), vcsDataName));
        File[] files = new File[0];
        try {
            files = Files
                    .walk(Paths.get(parent.getAbsolutePath()))
                    .map(Path::toFile)
                    .filter(x -> !ignore.contains(x.getName()))
                    .toArray(File[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private Revision getResultRevision(ArrayList<Revision> revs) {
        var c = new ArrayList<MyFile>();
        var d = new ArrayList<MyFile>();
        var m = new ArrayList<MyFile>();
        for (Revision r : revs) {
            if (r.created != null) c.addAll(r.created);
            if (r.deleted != null) d.addAll(r.deleted);
            if (r.modified != null) m.addAll(r.modified);
        }
        var cCopy = new ArrayList<>(c);
        removeAllByPath(c, d);
        var toDel = new ArrayList<MyFile>();
        var toIns = new ArrayList<MyFile>();

        for (MyFile file : c) {
            var match = m.stream().filter(x -> file.path.equals(x.path)).findFirst();
            if (match.isPresent()) {
                MyFile ge = match.get();
                toIns.add(ge);
                toDel.add(file);
            }
        }
        c.removeAll(toDel);
        c.addAll(toIns);
        removeAllByPath(m, d);
        removeAllByPath(m, c);
        removeAllByPath(d, cCopy);
        return new Revision(-1, "", -1, c, d, m);
    }

    private String printChanges(Revision revision) {
        StringBuilder mes = new StringBuilder();
        var c = revision.created;
        var d = revision.deleted;
        var m = revision.modified;
        if (c.size() == 0 && d.size() == 0 && m.size() == 0)
            return "No changes.";
        Stream.of(c,d,m).forEach(x -> x.sort(Comparator.comparing(y -> y.name)));
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
}