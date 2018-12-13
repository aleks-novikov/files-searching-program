import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

public class FilesHierarchyBuild {
    private TreeItem parentFolder;
    private List<String> files;
    private String extension;
    private TreeItem foundedNode = null;

    private TreeItem getFoundedNode() {
        return foundedNode;
    }

    private void setFoundedNode(TreeItem foundedNode) {
        this.foundedNode = foundedNode;
    }

    FilesHierarchyBuild(TreeItem parentFolder, List<String> files, String extension) {
        this.parentFolder = parentFolder;
        this.files = files;
        this.extension = extension;
    }

    void start() {
        for (String filePath : files) {
            addNode(0, filePath.split("\\\\"), parentFolder);
        }
    }

    private void addNode(int i, String[] nodes, TreeItem rootNode) {
        String node = nodes[i];
        if (node.endsWith(extension)) {
            rootNode.getChildren().add(new TreeItem<>(node));
        } else if (folderIsNotInTree(rootNode, node)) {
            TreeItem subNode = new TreeItem<>(node);
            rootNode.getChildren().add(subNode);
            addNode(++i, nodes, subNode);
        } else {
            searchSameNode(rootNode, node);
            if (getFoundedNode() != null) {
                addNode(++i, nodes, getFoundedNode());
            }
        }
    }

    private void searchSameNode(TreeItem parentNode, String nodeName) {
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