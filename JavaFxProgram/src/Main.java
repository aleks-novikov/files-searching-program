import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private static Stage stage;
    private static String rootFolder;
    public static TreeView treeFiles;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Main.stage = stage;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("WindowAppearance.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Files Searching");
            stage.setWidth(400);
            stage.setHeight(540);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getFolder() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        File initFolder = new File("G:\\Java\\Projects\\JavaFxProgram\\test");
        dirChooser.setInitialDirectory(initFolder);
        rootFolder = dirChooser.showDialog(stage).toString();
        if (rootFolder == null) {
            showMessage("Пожалуйста, выберите папку");
        }
    }

    static void getInformation(String text, String extension) {
        text = "78";
        rootFolder = "G:\\Java\\Projects\\JavaFxProgram\\test";
        if (rootFolder == null) {
            showMessage("Пожалуйста, выберите папку");
        } else if (text.equals("")) {
            showMessage("Пожалуйста, введите текст для поиска");
        } else if (extension.equals("")) {
            showMessage("Пожалуйста, введите расширение файла");
        } else {
//            try {
//                List<Path> foundedFiles = FilesSearching.filesSearching(rootFolder, text, extension);
//                TreeMap<Path, String> filesAndFolders = FilesSearching.getFilesFolders(foundedFiles);
            new Controller().newTree(rootFolder, extension, text);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
