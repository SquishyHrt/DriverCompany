package model;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

public class CommentTreeCell extends TreeCell<Comment> {
    @Override
    protected void updateItem(Comment item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getText());
            TreeItem<Comment> treeItem = getTreeItem();
            if (treeItem != null && treeItem.isExpanded()) {
                getTreeView().getSelectionModel().select(treeItem);
            }
        }
    }
}
