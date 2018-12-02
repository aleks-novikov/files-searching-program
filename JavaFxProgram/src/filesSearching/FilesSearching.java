package filesSearching;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FilesSearching {
    public static List<Path> filesSearching(String rootFolder, String text, String extension) throws IOException {
        return Files.walk(Paths.get(rootFolder))
                .filter(f -> f.toString().endsWith(extension) && getContent(f, text))
                .collect(Collectors.toList());
    }

    private static boolean getContent(Path file, String text) {
        try {
            return new String(Files.readAllBytes(file)).contains(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static TreeMap<Path, String> getFilesFolders(List<Path> foundedFiles) {
        TreeMap<Path, String> folders = new TreeMap<>();
        for (Path file : foundedFiles) {
            String fold = file.getParent().toString();
            fold = fold.substring(fold.lastIndexOf("\\") + 1);
            folders.put(file, fold);
        }
        return folders;
    }
}