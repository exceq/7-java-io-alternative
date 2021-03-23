import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Revision {
    int revisionId;
    Date date;
    String comment;
    ArrayList<File> created = new ArrayList<>();
    ArrayList<File> deleted = new ArrayList<>();
    ArrayList<File> modified = new ArrayList<>();
   /* List ccreated;
    File[] ddeleted;
    File[] mmodified;

    public Revision(int revisionId, String comment, List ccreated, List ddeleted, List mmodified) {
        this.revisionId = revisionId;
        this.date = date;
        this.comment = comment;
        this.ccreated = ccreated;
        this.ddeleted = ddeleted;
        this.mmodified = mmodified;
    }*/

    public Revision(int revisionId, String comment, ArrayList<File> created, ArrayList<File> deleted, ArrayList<File> modified) {
        this.revisionId = revisionId;
        this.date = new Date();
        this.comment = comment;
        this.created = created;
        this.deleted = deleted;
        this.modified = modified;
    }
}
