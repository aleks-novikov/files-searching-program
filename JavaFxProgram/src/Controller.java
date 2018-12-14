import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button getFolderButton;
    @FXML
    private Button findText;
    @FXML
    private Button moveOn;
    @FXML
    private Button moveBack;
    @FXML
    private Button selectAll;
    @FXML
    private Button exit;
    @FXML
    public TextField userExtensionTxtFld;
    @FXML
    private TextField getText;
    @FXML
    private RadioButton userExtension;
    @FXML
    private RadioButton defaultExtension;
    @FXML
    private TreeView treeFiles;
    @FXML
    private TextArea fileContent;
    @FXML
    private TabPane tabPane;

    private Program program;
    private static String selectedFileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        program = Program.program;
        Program.treeFiles = treeFiles;
        Program.fileContent = fileContent;
    }

    @FXML
    public void pressButton(ActionEvent ae) {
        if (ae.getSource() == getFolderButton) {
            program.getFolder();
        } else if (ae.getSource() == findText) {
            program.getInformation(getText.getCharacters().toString(), getExtension());
        } else if (ae.getSource() == exit) {
            System.exit(0);
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

    static void getSelectedFilePath(TreeView root, String folder) {
        //добавляем Listener к TreeView, чтобы получить путь к выделенному файлу
        root.getSelectionModel().selectedItemProperty().addListener
                ((ChangeListener<TreeItem<String>>) (changed, oldValue, newValue) -> {

                    if ((newValue != null) && (newValue.toString().contains(Program.filesExtension))) {
                        TreeItem<String> parent = newValue.getParent();
                        StringBuilder path = new StringBuilder(newValue.getValue());
                        selectedFileName = path.toString();

                        //продолжаем двигаться по иерархии файла, пока не дойдём до папки folder, указанной пользователем
                        while (!parent.getValue().equals(Controller.getName(folder))) {
                            path.insert(0, parent.getValue() + "\\");
                            parent = parent.getParent();
                        }
                        showFileData(folder + "\\" + path);
                    }
                });
    }

    private static void showFileData(String filePath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "CP1251"))) {
            StringBuilder data = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                data.append(line);
                line = br.readLine();
            }
            Program.fileContent.setWrapText(true);
            Program.fileContent.setText(data.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage() + ", " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectFoundedText(ActionEvent ae) {
        String allText = fileContent.getText();

        if (allText.equals("")) {
            Runnable message = () -> Program.showMessage("Для навигации по найденному тексту, пожалуйста, выберите файл");
            Platform.runLater(message);
        }

        String searchedText = program.getSearchedText();
        int from = fileContent.getCaretPosition();
        int to;
        if (ae.getSource() == moveOn) {
            from = allText.indexOf(searchedText, from + 1);
            if (from == -1) {
                from = allText.indexOf(searchedText);
            }
        } else if (ae.getSource() == moveBack) {
            if (from != 0) {
                allText = allText.substring(0, from - 1);
            }
            from = allText.lastIndexOf(searchedText);
            if (from == -1) {
                from = fileContent.getText().lastIndexOf(searchedText);
            }
        }
        to = from + searchedText.length();
        fileContent.selectRange(from, to);

        if (ae.getSource() == selectAll) {
            //TODO реализовать функционал с помощью сторонней библиотеки, textArea JavaFx не поддерживает выделение нескольких слов одновременно
            fileContent.selectRange(0, allText.length());
        }
    }

    @FXML
    public void setNewFileTab() {
        if (selectedFileName != null) {
            Tab tab = new Tab(selectedFileName);
            TextArea tabArea = new TextArea(fileContent.getText());
            tabArea.setWrapText(true);
            tab.setContent(tabArea);
            tabPane.getSelectionModel().select(tab);
            tabPane.getTabs().add(tab);
        }
    }

    private static String getName(String name) {
        return name.substring(name.lastIndexOf("\\") + 1);
    }
}