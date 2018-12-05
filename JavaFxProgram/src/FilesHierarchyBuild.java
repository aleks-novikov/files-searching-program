import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

public class FilesHierarchyBuild implements Runnable {
    private TreeItem parentFolder;
    private List<String> files;
    Program program;

    public TreeItem getFoundedNode() {
        return foundedNode;
    }

    public void setFoundedNode(TreeItem foundedNode) {
        this.foundedNode = foundedNode;
    }

    public TreeItem foundedNode = null;

    FilesHierarchyBuild(TreeItem parentFolder, List<String> files, Program program) {
        this.parentFolder = parentFolder;
        this.files = files;
        this.program = program;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (String filePath : files) {
            newBuildTree(filePath, parentFolder);
        }
//        Program.updateProgramTree(parentFolder);
    }


    public void newBuildTree(String filePath, TreeItem root) {
        String[] nodes = filePath.split("\\\\");
        addLeafs(0, nodes, root);
    }

    public void addLeafs(int i, String[] nodes, TreeItem root) {
        String node = nodes[i];
        if (node.endsWith(".txt")) {
            root.getChildren().add(new TreeItem<>(node));
        } else if (folderIsNotInTree(root, node)) {
            TreeItem subNode = new TreeItem<>(node);
            root.getChildren().add(subNode);
            addLeafs(++i, nodes, subNode);
        } else {
            searchSame(root, node);
            if (getFoundedNode() != null) {
                addLeafs(++i, nodes, getFoundedNode());
            }
        }
    }

    public void buildTree(TreeItem parentNode, String filePath) throws IllegalStateException {
        String fileFolder = null;
        if (!filePath.contains("\\")) {
            parentNode.getChildren().add(new TreeItem<>(filePath));
        } else {
            fileFolder = filePath.substring(0, filePath.indexOf("\\"));

            //добавление подузла, если его не существует в данном род. узле
            if (folderIsNotInTree(parentNode, fileFolder)) {
                TreeItem subFolder = new TreeItem<>(fileFolder);
                parentNode.getChildren().add(subFolder);
                filePath = filePath.replace(fileFolder + "\\", "");
                buildTree(subFolder, filePath);
            } else {

                //если такой узел уже существует, идём дальше по пути файла
                filePath = filePath.replace(fileFolder + "\\", "");
                if (!filePath.contains("\\")) {  //это файл, добавляем его к родителю и заканчиваем перебор узла
                    addChildNode(parentNode, fileFolder, filePath);
                    return;
                }

                String subNodeName = filePath.substring(0, filePath.indexOf("\\"));
                TreeItem node = searchSameNode(parentNode, subNodeName);
                System.out.println();

                if (node != null) {
                    filePath = filePath.replace(subNodeName + "\\", "");
                    buildTree(node, filePath);
                }
            }
                /*
                if (node == null) {
//                    String str = filePath.substring(0, filePath.indexOf("\\"));
//                    if (str.equals(subNodeName)) {
//                        addChildNode(parentNode, filePath, subNodeName);
//                        return;
//                    }
                    node = addChildNode(parentNode, fileFolder, subNodeName);
                    buildTree(node, filePath);
                } */
//            }
        }
    }
//}

    private TreeItem addChildNode(TreeItem parentNode, String fileFolder, String filePath) {
        ObservableList<TreeItem> nodes = parentNode.getChildren();
        for (TreeItem item : nodes) {
            if (nodesAreSame(item, fileFolder)) {
                item.getChildren().add(new TreeItem<>(filePath));
                return item;
            }
        }
        return null;
    }

    private TreeItem searchSameNode(TreeItem parentNode, String nodeName) throws NullPointerException {
        ObservableList<TreeItem> nodes = parentNode.getChildren();
        for (TreeItem node : nodes) {
            if (nodesAreSame(node, nodeName)) {
                return node;
            } else if (node.getChildren() != null && !node.getValue().toString().contains(".")) {
                return searchSameNode(node, nodeName);
            }
        }
        return null;
    }

    private void searchSame(TreeItem parentNode, String nodeName) {
        ObservableList<TreeItem> nodes = parentNode.getChildren();
        for (TreeItem node : nodes) {
            if (nodesAreSame(node, nodeName)) {
                setFoundedNode(node);
            } else if (node.getChildren() != null && !node.getValue().toString().contains(".")) {
                searchSameNode(node, nodeName);
            }
        }
    }


    private boolean folderIsNotInTree(TreeItem<String> parentFolder, String folder) {
        for (TreeItem item : parentFolder.getChildren()) {
            if (nodesAreSame(item, folder)) {
                return false;
            }
        }
        return true;
    }

    private boolean nodesAreSame(TreeItem existNode, String curNodeName) {
        return existNode.toString().equals(new TreeItem<>(curNodeName).toString());
    }
}