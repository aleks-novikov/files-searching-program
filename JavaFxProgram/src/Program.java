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
    public static TreeView treeFiles;
    public static TextArea fileContent;

    /*    public static String rootFolder;
        public static String searchedText;
        public static String filesExtension;
        public static Stage stage;*/
    public static String filesExtension;
    private Stage stage;
    private String rootFolder;
    private String searchedText;
//    private String filesExtension;

    public static void main(String[] args) {
        new Program().programStart();
    }

    void programStart() {
        program = this;
        launch();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    public void setFilesExtension(String filesExtension) {
        this.filesExtension = filesExtension;
    }

    public Stage getStage() {
        return stage;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public String getSearchedText() {
        return searchedText;
    }

    public String getFilesExtension() {
        return filesExtension;
    }

    @Override
    public void start(Stage stage) {
        try {
            program.setStage(stage);
            Parent root = FXMLLoader.load(getClass().getResource("WindowAppearance.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFolder() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        File initFolder = new File("G:\\Java");
        dirChooser.setInitialDirectory(initFolder);
        this.setRootFolder(dirChooser.showDialog(this.getStage()).toString());
        if (rootFolder == null) {
            showMessage("Пожалуйста, выберите папку");
        }
    }

    public void getInformation(String searchedText, String filesExtension) {
        this.setSearchedText("Java");
        this.setFilesExtension("txt");
        this.setRootFolder("G:\\Java");
        if (this.getRootFolder() == null) {
            showMessage("Пожалуйста, выберите папку");
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
        new FilesHierarchyBuild(rootFolder, foundedFiles, program);
    }

    public static void updateProgramTree(TreeItem rootFolder) {
        Runnable updater = () -> {
            Program.treeFiles.setRoot(rootFolder);
            //отображение иерархии найденных файлов
            Controller.getSelectedFilePath(program.treeFiles, program.getRootFolder());
        };
        Platform.runLater(updater);
    }

    private static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String getName(String name) {
        return name.substring(name.lastIndexOf("\\") + 1);
    }
}