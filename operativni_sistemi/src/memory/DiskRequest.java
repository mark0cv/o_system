package memory;


import java.io.File;

public class DiskRequest {
    private FileInMemory file;
    private File diskFile;

    public DiskRequest(FileInMemory file, File diskFile) {
        this.file = file;
        this.diskFile = diskFile;
    }

    public FileInMemory getFile() {
        return file;
    }

    public File getDiskFile() {
        return diskFile;
    }

    public String getFileName() {
        return file.getName();
    }

    @Override
    public String toString() {
        return "DiskRequest{" +
                "file=" + file.getName() +
                ", diskFile=" + diskFile.getPath() +
                '}';
    }
}