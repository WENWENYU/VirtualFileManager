import java.util.*;

public class VirtualFiles {
    public static VirtualFile createNewFile(final VirtualFileSystem vfs, final String path) throws VFSException {
        Objects.requireNonNull(vfs);
        Objects.requireNonNull(path);
        VirtualFile newFile;
        if (!exists(vfs, path)) {
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            VirtualFile parentDirVirtualFile = getVirtualDirectoryByPath(vfs, parentDir);
            newFile = new VirtualFile(path);
            newFile.setDirectory(false);
            parentDirVirtualFile.addFile(newFile);
        } else {
            throw new VFSException("file already exist: "+path);
        }
        return newFile;
    }

    public static VirtualFile createNewDirectory(final VirtualFileSystem vfs, final String path) throws VFSException {
        Objects.requireNonNull(vfs);
        Objects.requireNonNull(path);
        VirtualFile newDirectory;
        if (!exists(vfs, path)) {
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            VirtualFile parentDirVirtualFile = getVirtualDirectoryByPath(vfs, parentDir);
            newDirectory = new VirtualFile(path);
            newDirectory.setDirectory(true);
            parentDirVirtualFile.addFile(newDirectory);
        } else {
            throw new VFSException("directory already exist: "+path);
        }
        return newDirectory;
    }

    public static boolean exists(final VirtualFileSystem vfs, final String path) {
        Objects.requireNonNull(vfs);
        Objects.requireNonNull(path);
        boolean result = false;
        final Set<String> roots = vfs.getRoots();
        for (String root : roots) {
            final VirtualFile vfsRoot = vfs.getRoot(root);
            if (root.equalsIgnoreCase(path)) {
                result = true;
                break;
            } else {
                if (path.substring(0, 2).equalsIgnoreCase(root)) {//go down if we in the right root
                    result = exists0(vfsRoot, path);
                }
            }
        }
        return result;
    }

    private static boolean exists0(final VirtualFile root, final String path) {
        boolean result = false;
        String origPath = path.toLowerCase();
        final Map<String, VirtualFile> innerFiles = root.getInnerFiles();
        final Set<String> inFiles = innerFiles.keySet();
        for (String vfPath : inFiles) {

//            if (vfPath.toLowerCase().endsWith(path)) {
            if (vfPath.equalsIgnoreCase(path)) {
                result = true;
                break;
            } else if (innerFiles.get(vfPath).isDirectory()) {
                result = exists0(innerFiles.get(vfPath), path);
            }
        }
        return result;
    }

    /**
     * Returns Virtual Directory by String path
     * if not exist create necessary Directory's starting from root
     *
     * @param vfs
     * @param path
     * @return
     */
    public static VirtualFile getVirtualDirectoryByPath(final VirtualFileSystem vfs, final String path) {
        VirtualFile result = null;
        if (exists(vfs, path)) {
            for (String root : vfs.getRoots()) {
                VirtualFile vf = vfs.getRoot(root);
                if (path.substring(0, 2).equalsIgnoreCase(root)) {      //if we in the right root
                    if (root.equalsIgnoreCase(path)) {
                        result = vf;
                    } else {
                        result = findFVbyPath(vf, path);
                    }
                    if (result != null) {
                        break;
                    }
                }
            }
        } else {
            result = new VirtualFile(path);
            result.setDirectory(true);
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            VirtualFile parentDirVirtualFile = getVirtualDirectoryByPath(vfs, parentDir);
            parentDirVirtualFile.addFile(result);
        }
        return result;
    }

    private static VirtualFile findFVbyPath(VirtualFile root, String path) {
        VirtualFile result = null;
        final Map<String, VirtualFile> innerFiles = root.getInnerFiles();

        for (VirtualFile vf : innerFiles.values()) {
            if (vf.getPath().equalsIgnoreCase(path)) {
                result = vf;
                break;
            } else {
                result = findFVbyPath(vf, path);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> getListOfVirtualFiles(final VirtualFileSystem vfs, final String path) {
        Objects.requireNonNull(vfs);
        Objects.requireNonNull(path);
        List<String> vfList = new ArrayList<>();
        if (exists(vfs, path)) {
            VirtualFile result = null;
            for (String root : vfs.getRoots()) {
                VirtualFile vf = vfs.getRoot(root);
                result = findFVbyPath(vf, path);
                if (result != null) {
                    break;
                }
            }
            Objects.requireNonNull(result);
            Set<String> vfSet = result.getInnerFiles().keySet();
            vfList.addAll(vfSet);
            for (String vfFromSet: vfSet){
                VirtualFile vf = findFVbyPath(result, vfFromSet);
                if (vf.isDirectory()){
                    vfList.addAll(getListOfVirtualFiles(vfs, vf.getPath()));
                }
            }
        }
        return vfList;
    }
}
