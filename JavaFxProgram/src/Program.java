import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Program extends Application implements Runnable {
    public static TreeView treeFiles;
    public static TextArea fileContent;
    public static String searchedText;
    public static String filesExtension;
    private static Stage stage;
    private static String rootFolder;

    @Override
    public void run() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Program.stage = stage;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("WindowAppearance.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getFolder() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
//        File initFolder = new File("G:\\Java\\Projects\\JavaFxProgram\\test");
        File initFolder = new File("G:\\Java");
        dirChooser.setInitialDirectory(initFolder);
        rootFolder = dirChooser.showDialog(stage).toString();
        if (rootFolder == null) {
            showMessage("Пожалуйста, выберите папку");
        }
    }

    public static void getInformation(String searchedText, String filesExtension) {
//        Program.searchedText = searchedText;
//        Program.filesExtension = filesExtension;
        Program.filesExtension = "txt";
        Program.searchedText = "Java";
        rootFolder = "G:\\Java";
        if (rootFolder == null) {
            showMessage("Пожалуйста, выберите папку");
        } else if (Program.searchedText.equals("")) {
            showMessage("Пожалуйста, введите текст для поиска");
        } else if (Program.filesExtension.equals("")) {
            showMessage("Пожалуйста, введите расширение файла");
        } else {
            new Controller().newFilesTree(rootFolder);
        }
    }

    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}