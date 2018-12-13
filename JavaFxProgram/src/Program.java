import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Program extends Application {
    public static Program program;
    static TreeView treeFiles;
    static TextArea fileContent;
    static String filesExtension;
    private Stage stage;
    private String rootFolder;
    private String searchedText;

    public static void main(String[] args) {
        new Program().programStart();
    }

    private void programStart() {
        program = this;
        launch();
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    private void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    private void setFilesExtension(String filesExtension) {
        Program.filesExtension = filesExtension;
    }

    private Stage getStage() {
        return stage;
    }

    private String getRootFolder() {
        return rootFolder;
    }

    String getSearchedText() {
        return searchedText;
    }

    private String getFilesExtension() {
        return filesExtension;
    }

    @Override
    public void start(Stage stage) {
        try {
            program.setStage(stage);
            Parent root = FXMLLoader.load(getClass().getResource("WindowAppearance.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Поиск файлов");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getFolder() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        File initFolder = new File(System.getProperty("user.dir"));
        dirChooser.setInitialDirectory(initFolder);
        String choosenPath = dirChooser.showDialog(this.getStage()).toString();
        this.setRootFolder(choosenPath);
    }

    void getInformation(String searchedText, String filesExtension) {
        this.setSearchedText(searchedText);
        this.setFilesExtension(filesExtension);
        if (this.getRootFolder() == null) {
            showMessage("Пожалуйста, выберите папку");
            getFolder();
        } else if (this.getSearchedText().equals("")) {
            showMessage("Пожалуйста, введите текст для поиска");
        } else if (this.getFilesExtension().equals("")) {
            showMessage("Пожалуйста, введите расширение файла");
        } else {
            new FilesSearching(program.getRootFolder(), program.getSearchedText(), program.getFilesExtension());
        }
    }

    static void newFilesTree(List foundedFiles) {
        TreeItem<String> rootFolder = new TreeItem<>(getName(program.getRootFolder()));
        //поиск файлов согласно заданным критериям
        new FilesHierarchyBuild(rootFolder, foundedFiles, program.getFilesExtension()).start();
        updateProgramTree(rootFolder);
    }

    private static void updateProgramTree(TreeItem root) {
        Runnable updater = () -> {
            //отображение иерархии найденных файлов
            Program.treeFiles.setRoot(root);
            Controller.getSelectedFilePath(program.treeFiles, program.getRootFolder());
        };
        Platform.runLater(updater);
    }

    static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String getName(String name) {
        return name.substring(name.lastIndexOf("\\") + 1);
    }
}