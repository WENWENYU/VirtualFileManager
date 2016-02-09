import java.util.HashMap;
import java.util.Map;

public class VirtualFile {
    private Map<String, VirtualFile> innerFiles;
    private byte[] entryFile;
    private boolean directory;
    private String path;
    private String parentPath;


    public VirtualFile(String path) {
        this.path = path;
        this.innerFiles = new HashMap<>();
    }

    public void addFile(VirtualFile newFile) {
        newFile.setParentPath(path);
        innerFiles.put(newFile.getPath(), newFile);
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getPath() {
        return path;
    }

    public void setInnerFiles(Map<String, VirtualFile> innerFiles) {
        this.innerFiles = innerFiles;
    }

    public Map<String, VirtualFile> getInnerFiles() {
        return innerFiles;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setEntryFile(byte[] entryFile) {
        this.entryFile = entryFile;
    }

    public byte[] getEntryFile() {
        return entryFile;
    }
}
