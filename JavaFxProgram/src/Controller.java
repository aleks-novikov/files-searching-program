import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
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
    private Button cancel;
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

    private TreeItem<String> rootFolder;
    private static String selectedFileName;  //при открытии файла в новой вкладке эта переменная - её имя
    private static SearchFiles searchFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Program.treeFiles = treeFiles;
        Program.fileContent = fileContent;
    }

    @FXML
    public void pressButton(ActionEvent ae) {
        if (ae.getSource() == getFolderButton) {
            Program.getFolder();
        } else if (ae.getSource() == findText) {
            Program.getInformation(getText.getCharacters().toString(), getExtension());
        } else if (ae.getSource() == cancel) {
            if (searchFiles != null) {
                searchFiles.interruptThread();
            }
        }
        else if (ae.getSource() == exit) {
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

    public void newFilesTree(String folder) {
        treeFiles = Program.treeFiles;
        rootFolder = new TreeItem<>(getName(folder));

        //поиск файлов согласно заданным критериям
        searchFiles = new SearchFiles(new File(folder), rootFolder);

        //отображение иерархии найденных файлов
        treeFiles.setRoot(rootFolder);
        getSelectedFilePath(treeFiles, folder);
    }

    private void getSelectedFilePath(TreeView root, String folder) {
        //добавляем Listener к TreeView, чтобы получить путь к выделенному файлу
        root.getSelectionModel().selectedItemProperty().addListener
                ((ChangeListener<TreeItem<String>>) (changed, oldValue, newValue) -> {

                    if (newValue.toString().contains(Program.filesExtension)) { //проверка на то, выбран ли файл или директория
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

    private void showFileData(String filePath) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "CP1251"));) {
            StringBuilder data = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                data.append(line);
                line = br.readLine();
            }
            Program.fileContent.setWrapText(true);
            Program.fileContent.setText(data.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void textSelectionChange(ActionEvent ae) {
        String text = fileContent.getText();
        int from = fileContent.getCaretPosition();
        int to = text.indexOf(" ", from + 1);

        if (to < 0) {
            to = text.length();
            fileContent.selectRange(from + 1, to);
            return;
        }

        if (ae.getSource() == moveOn) {
            shiftPositionAhead(from, to);

           /*  String text = fileContent.getText();
             String[] words = text.split(" ");
             int[] positions = new int[words.length];
             for (int i = 0; i < words.length; i++) {
                 positions[i] = text.indexOf(words[i]);
             }

             int from = fileContent.getCaretPosition()+1;
             int to = 0;
             int i = 0;
             do {
                 if (from > positions[i] && from <= positions[i + 1]) {
                     from = positions[i];
                     to = positions[i + 1]-1;
                     break;
                 } else {
                     i++;
                 }
             } while (i < positions.length);*/
        } else if (ae.getSource() == moveBack) {
            if (from == 0) {
                fileContent.selectRange(0, text.indexOf(" ", 1));
                return;
            }

            int gapAmount = 0;
            do {
                String str = text.substring(from, from + 1);
                if (str.equals(" ") && gapAmount == 0) {
                    to = from;
                    gapAmount++;
                }
                from--;
            }
            while (from > 0 && gapAmount < 2);

            fileContent.selectRange(from, to);

        } else if (ae.getSource() == selectAll) {
            fileContent.selectRange(0, text.length());
        }
    }

    private void shiftPositionAhead(int from, int to) {
        if (from != 0) {
            to = fileContent.getCaretPosition() + 1;
            from = fileContent.getText().indexOf(" ", to);
        }
        if (from > to) {
            int i = from;
            from = to;
            to = i;
        }
        fileContent.selectRange(from, to);
    }

    @FXML
    public void setNewFileTab() {
        if (selectedFileName != null) {
            Tab tab = new Tab(selectedFileName);
            TextArea tabArea = new TextArea(fileContent.getText());
            tabArea.setWrapText(true);
            tab.setContent(tabArea);
            tabPane.getTabs().add(tab);
        }
    }

    public static String getName(String name) {
        return name.substring(name.lastIndexOf("\\") + 1);
    }
}