package memory;

import java.io.File;
import java.util.ArrayList;

public class FileInMemory {
    private String name;
    private int size;
    private FileInMemory parentFile;
    private ArrayList<FileInMemory>childrenFiles;
    private ArrayList<String>content;
    public FileInMemory(String fileName,int size,FileInMemory parentFile){
        this.name=fileName;
        this.size=size;
        this.childrenFiles=new ArrayList<>();
        this.content=new ArrayList<>();
        this.parentFile=parentFile;
    }
    public void addChild(FileInMemory child){
        this.childrenFiles.add(child);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<FileInMemory> getChildrenFiles() {
        return childrenFiles;
    }

    public FileInMemory getParentFile() {
        return parentFile;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setChildrenFiles(ArrayList<FileInMemory> childrenFiles) {
        this.childrenFiles = childrenFiles;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
