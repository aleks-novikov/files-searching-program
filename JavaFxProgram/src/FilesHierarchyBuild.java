import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

public class FilesHierarchyBuild implements Runnable {
    private TreeItem parentFolder;
    private List<String> files;
    Program program;

    FilesHierarchyBuild(TreeItem parentFolder, List<String> files, Program program) {
        this.parentFolder = parentFolder;
        this.files = files;
        this.program = program;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (String filePath : files) {
            try {
                System.out.println(filePath);
                buildTree(parentFolder, filePath);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        Program.updateProgramTree (parentFolder);
    }

    public void buildTree(TreeItem parentNode, String filePath) throws IllegalStateException {
        String fileFolder = null;
        if (!filePath.contains("\\")) {
            parentNode.getChildren().add(new TreeItem<>(filePath));
        } else {
            fileFolder = filePath.substring(0, filePath.indexOf("\\"));
            if (folderIsNotInTree(parentNode, fileFolder)) {
                //добавление подузла, если его не существует в данном род. узле
                TreeItem subFolder = new TreeItem<>(fileFolder);
                parentNode.getChildren().add(subFolder);
                filePath = filePath.replace(fileFolder + "\\", "");
                buildTree(subFolder, filePath);
            } else {
                //если такой узел уже существует, идём дальше по пути файла
                filePath = filePath.replace(fileFolder + "\\", "");
                if (!filePath.contains("\\")) {
                    addChildNode(parentNode, fileFolder, filePath);
                    return;
                } else {
                    String subNodeName = filePath.substring(0, filePath.indexOf("\\"));
                    TreeItem node = getEqualNode(parentNode, subNodeName);

                    if (node == null) {
                        node = addChildNode(parentNode, fileFolder, subNodeName);
                        buildTree(node, filePath);
                    } else {
                        filePath = filePath.replace(subNodeName + "\\", "");
                        buildTree(node, filePath);
                    }
                }
            }
        }
    }

    private TreeItem addChildNode(TreeItem parentNode, String fileFolder, String filePath) {
        ObservableList<TreeItem> nodes = parentNode.getChildren();
        for (TreeItem item : nodes) {
            if (item.toString().equals(new TreeItem<>(fileFolder).toString())) {
                item.getChildren().add(new TreeItem<>(filePath));
                return item;
            }
        }
        return null;
    }

    private TreeItem getEqualNode(TreeItem parentNode, String treeName) throws NullPointerException {
        ObservableList<TreeItem> nodes = parentNode.getChildren();
        for (TreeItem node : nodes) {
            if (node.toString().equals(new TreeItem<>(treeName).toString())) {
                return node;
            } else if (node.getChildren() != null
                    && !node.getValue().toString().contains(".")) {
                return getEqualNode(node, treeName);
            }
        }

        return null;
    }

    private boolean folderIsNotInTree(TreeItem parentFolder, String folder) {
        for (Object item : parentFolder.getChildren()) {
            if (item.toString().equals(new TreeItem(folder).toString())) {
                return false;
            }
        }
        return true;
    }
}