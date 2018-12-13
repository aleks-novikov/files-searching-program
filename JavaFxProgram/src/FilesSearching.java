import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilesSearching implements Runnable {
    private String rootFolder, text, extension;

    FilesSearching(String rootFolder, String text, String extension) {
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
            System.out.println(e.getCause() + ", " + e.getMessage());
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

        if (foundedFiles.size() == 0) {
            Runnable message = () -> Program.showMessage("Файлы с заданными параметрами не найдены!");
            Platform.runLater(message);
        } else {

            System.out.println("Список найденных файлов:");
            for (String file : foundedFiles) {
                System.out.println(file);
            }
            Program.newFilesTree(foundedFiles);
        }
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