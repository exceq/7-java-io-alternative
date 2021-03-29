import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
@Data
public class Revision {
    int revisionId;
    Date date;
    String comment;
    int countFiles;
    ArrayList<MyFile> created = new ArrayList<>();
    ArrayList<MyFile> deleted = new ArrayList<>();
    ArrayList<MyFile> modified = new ArrayList<>();

    public Revision(){}

    public Revision(int revisionId, String comment, int countFiles, ArrayList<MyFile> created, ArrayList<MyFile> deleted, ArrayList<MyFile> modified) {
        this.revisionId = revisionId;
        this.date = new Date();
        this.comment = comment;
        this.countFiles = countFiles;
        this.created = created;
        this.deleted = deleted;
        this.modified = modified;
    }
}
