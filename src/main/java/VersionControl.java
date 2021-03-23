import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class VersionControl {
    File fileVsc;
    String vscName;
    ArrayList<Revision> revisions = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();

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
        File parent = new File(fileVsc.getAbsolutePath()).getParentFile();
        File[] files = parent.listFiles();
        files = Arrays.stream(files).filter(x -> !x.getName().equals(vscName)).toArray(File[]::new);
        if (revisions.size() == 0) {
            Revision rev = new Revision(1,commit, new ArrayList<File>(Arrays.asList(files)),null,null);
            revisions.add(rev);
            this.files.addAll(rev.created);
        } else {
            Revision rev = createRevision(files,revisions.size()+1,commit);
            revisions.add(rev);
            this.files.addAll(rev.created);
            for (File file: rev.deleted) {
                this.files.removeIf(x->x.getName().equals(file.getName()));
            }
        }
        String message = "Created revision " + (revisions.size());
        return message;
    }

    private Revision createRevision(File[] files, int revisionId, String commit){
        /*
        1(lf)   file1 file2
        cur file2* file3                del file1 created file3

        curcopy file2 file3
        cc - lf file3           created

        lf - cur file1          deleted

        cb file1 file2
        cb-lf file2             changed base -> check last modify
         */
        ArrayList<File> currentFiles = new ArrayList<>(Arrays.asList(files));
        ArrayList<File> curCopy = new ArrayList<>(currentFiles);
        ArrayList<File> lastFiles = new ArrayList<>(revisions.get(revisions.size() - 1).created);
        ArrayList<File> changedBase = new ArrayList<>(lastFiles);

        //curCopy.removeAll(lastFiles);       //created
        removeAllByName(curCopy,lastFiles);
        //lastFiles.removeAll(currentFiles);  //deleted
        removeAllByName(lastFiles,currentFiles);
        //changedBase.removeAll(lastFiles);   //probably changed -> check last modify
        removeAllByName(changedBase,lastFiles);
        changedBase = changedBase
                .stream()
                .filter(x -> x.lastModified() > revisions.get(revisions.size() - 1).date.getTime())
                .collect(Collectors.toCollection(ArrayList::new));
        Revision newRev = new Revision(revisionId,commit,curCopy,lastFiles,changedBase);
        return newRev;
    }

    void removeAllByName(ArrayList<File> from, ArrayList<File> thiss){
        for (File file: thiss) {
            from.removeIf(x->x.getName().equals(file.getName()));
        }

    }

    public String status() {

        return "";
    }

    public String log() {
        //TODO
        return "";
    }

    public String diff(int first, int second) {
        //TODO
        return "";
    }

    public String checkout(int number) {
        //TODO
        return "";
    }
}
