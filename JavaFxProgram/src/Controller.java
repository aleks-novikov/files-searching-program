import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextField userExtensionTxtFld;
    @FXML
    private Button getFolderButton;
    @FXML
    private Button findText;
    @FXML
    private RadioButton userExtension;
    @FXML
    private RadioButton defaultExtension;
    @FXML
    private TextField getText;
    @FXML
    private TreeView treeFiles;

    private TreeItem<String> rootFolder;
    private TreeItem<String> subFolder;
    private String fileExtension;
    private String text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.treeFiles = treeFiles;
    }

    @FXML
    public void pressButton(ActionEvent ae) {
        if (ae.getSource() == getFolderButton) {
            Main.getFolder();
        } else if (ae.getSource() == findText) {
            Main.getInformation(getText.getCharacters().toString(), getExtension());
        }
    }

    private String getExtension() {
        if (defaultExtension.isSelected()) {
            return defaultExtension.getText();
        } else if (userExtensionTxtFld.getCharacters() != null) {
            return userExtensionTxtFld.getCharacters().toString();
        }
        return null;
    }

    @FXML
    public void radioBtnChanged() {
        if (userExtension.isSelected()) {
            userExtensionTxtFld.setDisable(false);
        } else {
            userExtensionTxtFld.setDisable(true);
        }
    }

    public void newTree(String folder, String extension, String text) {
        treeFiles = Main.treeFiles;
        rootFolder = new TreeItem<>(getName(folder));
        fileExtension = extension;
        this.text = text;
        getSubfolder(new File(folder), rootFolder);
        treeFiles.setRoot(rootFolder);

        treeFiles.getSelectionModel().selectedItemProperty().addListener
                ((ChangeListener<TreeItem<String>>) (changed, oldValue, newValue) -> {
                    TreeItem<String> parent = newValue.getParent();
                    StringBuilder str = new StringBuilder(newValue.getValue());
                    while (!parent.getValue().equals(getName(folder))) {
                        str.insert(0, parent.getValue() + "\\");
                        parent = parent.getParent();
                    }
                    System.out.println(folder + "\\" + str);
                });
    }

    private void getSubfolder(File folder, TreeItem parentFolder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            String fileName = file.toString();
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

            if ((file.isDirectory())) {
                if (!folderIsInTree(parentFolder, file)) {
                    subFolder = new TreeItem<>(fileName);
                    parentFolder.getChildren().add(subFolder);
                    getSubfolder(file, subFolder);
                }
            } else if (file.isFile() && getContent(file, text) && file.toString().endsWith(fileExtension)) {
                parentFolder.getChildren().add(new TreeItem<>(fileName));
            }
        }
    }

    private static boolean folderIsInTree(TreeItem parentFolder, File folder) {
        for (Object item : parentFolder.getChildren()) {
            if (item.toString().equals(folder)) {
                return true;
            }
        }
        return false;
    }

    private static boolean getContent(File file, String text) {
        Path path = Paths.get(file.toURI());
        try {
            return new String(Files.readAllBytes(path)).contains(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getName(String name) {
        return name.substring(name.lastIndexOf("\\") + 1);
    }
}