import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {
    private VirtualFileSystem ram;

    public FileManager() {
        ram = new VirtualFileSystem();
        ram.addRoot("c:");
        ram.addRoot("d:");
    }

    public boolean checkExist(String stringPath) {
        Objects.requireNonNull(stringPath);
        boolean result = false;
        if (stringPath.startsWith("file")) {
            result = Files.exists(Paths.get(stringPath.substring(7)));
        } else if (stringPath.startsWith("ram")) {
            result = VirtualFiles.exists(ram, stringPath.substring(6));
        }
        return result;
    }

    public List<String> getListOfFiles(String stringPath) {
        Objects.requireNonNull(stringPath);
        List<String> result = new ArrayList<>();
        if (stringPath.startsWith("file")) {
            result = getFilesByPath(Paths.get(stringPath.substring(7)));
        } else if (stringPath.startsWith("ram")) {
            result = VirtualFiles.getListOfVirtualFiles(ram, stringPath.substring(6));
        }
        return result;
    }

    private List<String> getFilesByPath(Path path) {
        List<String> result = new ArrayList<>();
        if (Files.isDirectory(path)) {
            try (final DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(path);) {
                for (Path tmp : pathDirectoryStream) {
                    result.add(tmp.toString());
                    result.addAll(getFilesByPath(tmp));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void createDir(String stringPath) throws VFSException, IOException {
        Objects.requireNonNull(stringPath);
        if (stringPath.startsWith("file")) {
            Files.createDirectory(Paths.get(stringPath.substring(7)));
        } else if (stringPath.startsWith("ram")) {
            VirtualFiles.createNewDirectory(ram, stringPath.substring(6));
        }
    }

    public void createEmptyFile(String stringPath) throws VFSException, IOException {
        Objects.requireNonNull(stringPath);
        if (stringPath.startsWith("file")) {
            Files.createFile(Paths.get(stringPath.substring(7)));
        } else if (stringPath.startsWith("ram")) {
            VirtualFiles.createNewFile(ram, stringPath.substring(6));
        }
    }

    public static void main(String[] args) throws IOException, VFSException {
        final FileManager fileManager = new FileManager();

        System.out.println(fileManager.checkExist("file://C:/temp/test.txt"));

        System.out.println(fileManager.checkExist("ram://c:/temp/test.txt"));

        fileManager.createDir("file://C:/temp/dir1");
        fileManager.createDir("file://C:/temp/dir1/dir2");
        fileManager.createEmptyFile("file://C:/temp/dir1/empty.file");
        fileManager.createEmptyFile("file://C:/temp/dir1/dir2/empty.file");
        System.out.println(fileManager.checkExist("file://C:/temp/dir1/empty.file"));

        fileManager.createDir("ram://C:/temp/dir1");
//        fileManager.createDir("ram://C:/temp/dir1");
        fileManager.createDir("ram://C:/temp/dir1/dir2");
        fileManager.createEmptyFile("ram://C:/temp/dir1/empty.file");
        fileManager.createEmptyFile("ram://C:/temp/dir1/empty2.file");
//        fileManager.createEmptyFile("ram://C:/temp/dir1/empty2.file");
        System.out.println(fileManager.checkExist("ram://C:/temp/dir1/empty.file"));


        System.out.println(fileManager.getListOfFiles("ram://C:/temp"));
        System.out.println(fileManager.getListOfFiles("file://C:/temp"));
    }
}
