import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MyFile {
    String fileName;
    String path;
    long size;
    long date;

    public MyFile(File file) {
        try {
            fileName = file.getName();
            path = file.getPath();
            size = file.length();
            date = file.lastModified();
        } catch (Exception e){
            System.out.println("Oops! MyFile is broken!");
        }
    }

    static ArrayList<MyFile> getMyFileList(File[] files, String vscName)
    {
        ArrayList<MyFile> result = new ArrayList<>();
        for (File file: files) {
            if (!file.getName().equals(vscName))
                result.add(new MyFile(file));
        }
        return result;
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFile myFile = (MyFile) o;
        return size == myFile.size && date == myFile.date && Objects.equals(fileName, myFile.fileName) && Objects.equals(path, myFile.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, path, size, date);
    }
}
