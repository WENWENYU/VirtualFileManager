import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VirtualFileSystem {
    private Map<String, VirtualFile> roots;

    public VirtualFileSystem() {
        roots = new HashMap<>();
    }

    public void addRoot(String path){
        final VirtualFile root = new VirtualFile(path);
        root.setDirectory(true);
        roots.put(path, root);
    }

    public VirtualFile getRoot(String root){
        return roots.get(root);
    }

    public Set<String> getRoots(){
        return roots.keySet();
    }


}

