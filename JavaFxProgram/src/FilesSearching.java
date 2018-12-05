import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilesSearching implements Runnable {
    private String rootFolder, text, extension;

    public FilesSearching(String rootFolder, String text, String extension) {
        this.rootFolder = rootFolder;
        this.text = text;
        this.extension = extension;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            filesSearching(rootFolder, text, extension);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filesSearching(String rootFolder, String text, String extension) throws IOException {
        List<Path> filesPath = Files.walk(Paths.get(rootFolder))
                .filter(f -> f.toString().endsWith(extension) && getContent(f, text))
                .collect(Collectors.toList());

        List<String> foundedFiles = new ArrayList<>(filesPath.size());
        for (Path file : filesPath) {
            foundedFiles.add(file.toString().replace(rootFolder + "\\", ""));
        }
        Program.newFilesTree (foundedFiles);
    }

    private boolean getContent(Path file, String text) {
        try {
            return new String(Files.readAllBytes(file)).contains(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}