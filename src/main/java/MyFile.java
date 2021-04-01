import lombok.Data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class MyFile {
    String name;
    String path;
    String content;
    long size;
    long date;
    boolean isDirectory;

    public MyFile() {}

    public MyFile(File file) {
        try {
            file = file.getAbsoluteFile();
            name = file.getName();
            path = file.getAbsolutePath();
            if (!file.isDirectory())
                content = new String(Files.readAllBytes(Paths.get(path)));
            else
                isDirectory = true;
            size = file.length();
            date = file.lastModified();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static ArrayList<MyFile> getMyFileList(ArrayList<File> files)
    {
        return files.stream()
                .map(MyFile::new)
                .collect(Collectors.toCollection(ArrayList<MyFile>::new));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFile myFile = (MyFile) o;
        return size == myFile.size && date == myFile.date && Objects.equals(name, myFile.name) && Objects.equals(path, myFile.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, size, date);
    }
}