import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SearchFiles implements Runnable {
    private File folder;
    private TreeItem parentFolder;
    private Thread thread;

    SearchFiles(File rootFolder, TreeItem parentFolder) {
        this.folder = rootFolder;
        this.parentFolder = parentFolder;
        thread = new Thread(this);
        thread.start();
    }

    public void interruptThread() {
        thread.interrupt();
    }

    @Override
    public void run() {
        getSubfolderFiles(folder, parentFolder);
//        Program.showMessage("Поиск файлов завершён!");
    }

    private void getSubfolderFiles(File folder, TreeItem parentFolder) {
        if (folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                String fileName = file.toString();
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

                if ((file.isDirectory())) {
                    if (!folderIsInTree(parentFolder, file)) {
                        TreeItem subFolder = new TreeItem<>(fileName);
                        parentFolder.getChildren().add(subFolder);
                        getSubfolderFiles(file, subFolder);
                    }
                    //если файл соответствует критериям, добавляем его название в иерархию
                } else if (file.isFile() && getContent(file, Program.searchedText)
                        && file.toString().endsWith(Program.filesExtension)) {
                    parentFolder.getChildren().add(new TreeItem<>(fileName));
                }
            }
        }
    }

    private boolean folderIsInTree(TreeItem parentFolder, File folder) {
        for (Object item : parentFolder.getChildren()) {
            if (item.toString().equals(folder)) {
                return true;
            }
        }
        return false;
    }

    private boolean getContent(File file, String text) {
        Path path = Paths.get(file.toURI());
        try {
            return new String(Files.readAllBytes(path)).contains(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}