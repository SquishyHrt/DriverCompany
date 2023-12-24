package fxControllers;


import hibernateControllers.UserHib;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import utils.GlobalUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ForumPage implements Initializable {
    @FXML
    public Button backButton, addCommentButton, createNewPostButton, forumDeletePostButton, forumDeleteCommentButton, forumUpdateCommentButton;
    @FXML
    public TextField createNewPostTextField;
    @FXML
    public TreeView<Comment> treeViewForum;
    @FXML
    public TextArea textAreaForum;
    @FXML
    public ListView<Forum> listViewForum;

    private User loggedUser;
    private Scene previousScene;
    private UserHib userHib;

    private Forum currentForum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeViewForum.setCellFactory(tv -> new CommentTreeCell());
        treeViewForum.setShowRoot(false);
        treeViewForum.setRoot(new TreeItem<>());
    }

    public void setUser(User user, Scene previousScene, UserHib userHib) {
        this.loggedUser = user;
        this.previousScene = previousScene;
        this.userHib = userHib;
        listViewForum.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Forum.class)));
        if (user instanceof Manager && ((Manager) user).isAdmin()) {
            forumDeleteCommentButton.setVisible(true);
            forumUpdateCommentButton.setVisible(true);
            forumDeletePostButton.setVisible(true);
        }
    }

    public void openBackMenu() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
        stage.show();
    }

    public void createNewPost() {
        if (createNewPostTextField.getText().isEmpty()) {
            GlobalUtils.createError("Error", "Please enter a title for your post");
            return;
        }

        Forum forum = new Forum(createNewPostTextField.getText());
        listViewForum.getItems().add(forum);
        createNewPostTextField.clear();
        userHib.createObject(forum, Forum.class);
        System.out.println("Forum created");
    }

    public void repopulateTreeView(List<Comment> comments) {
        treeViewForum.getRoot().getChildren().clear();
        TreeItem<Comment> rootItem = new TreeItem<>();
        treeViewForum.setRoot(rootItem);
        treeViewForum.setShowRoot(false);
        treeViewForum.getRoot().setExpanded(true);

        for (Comment comment : comments) {
            addTreeItem(rootItem, comment);
        }
    }

    private void addTreeItem(TreeItem<Comment> parent, Comment comment){
        TreeItem<Comment> treeItem = new TreeItem<>(comment);
        parent.getChildren().add(treeItem);
        comment.getReplies().forEach(sub->addTreeItem(treeItem, sub));
    }

    public void onClickListViewForum() {
        Forum forum = userHib.getObjectById(Forum.class, listViewForum.getSelectionModel().getSelectedItem().getId());
        textAreaForum.clear();
        if (forum == null) {
            return;
        }

        currentForum = forum;
        repopulateTreeView(forum.getComments());
    }

    public void addComment() {
        if (textAreaForum.getText() == null || textAreaForum.getText().isEmpty()) {
            GlobalUtils.createError("Error", "Please enter a comment");
            return;
        }

        if (treeViewForum.getRoot().getChildren().isEmpty() || treeViewForum.getSelectionModel().getSelectedItem() == null) {
            String commentText = textAreaForum.getText();
            Comment comment = new Comment(commentText, loggedUser.getLogin());
            currentForum.getComments().add(comment);

            comment.setForum(currentForum);
            comment.setParent(null);
            userHib.createObject(comment, Comment.class);
        } else {
            TreeItem<Comment> selectedCommentItem = treeViewForum.getSelectionModel().getSelectedItem();

            Comment parentComment = selectedCommentItem.getValue();
            Comment parent = userHib.getObjectById(Comment.class, parentComment.getId());
            Comment newComment = new Comment(textAreaForum.getText(), loggedUser.getLogin());
            parent.getReplies().add(newComment);

            newComment.setParent(parent);
            userHib.updateObject(parent);
        }

        repopulateTreeView(userHib.getObjectById(Forum.class, currentForum.getId()).getComments());
        System.out.println("Comment added");
        textAreaForum.clear();
    }

    public void deletePost() {
        if (listViewForum.getSelectionModel().getSelectedItem() == null) {
            GlobalUtils.createError("Input error", "Select a Post before deleting it");
        }
        Forum currentForum = userHib.getObjectById(Forum.class, listViewForum.getSelectionModel().getSelectedItem().getId());
        for (Comment comment : currentForum.getComments()) {
            comment = userHib.getObjectById(Comment.class, comment.getId());
            deleteAllComments(comment);
            userHib.deleteObject(comment);
        }
        userHib.deleteObject(currentForum);
        listViewForum.getItems().remove(listViewForum.getSelectionModel().getSelectedItem());
        treeViewForum.getRoot().getChildren().clear();
    }


    // Recursive function to delete all comments
    public void deleteAllComments(Comment comment) {
        for (Comment reply : comment.getReplies()) {
            deleteAllComments(reply);
        }

        if (comment.getReplies().isEmpty()) {
            comment.setParent(null);
            comment.setForum(null);
            System.out.println("Deleting comment: " + comment.getText());
            userHib.deleteObject(comment);
        }
    }

    public void deleteComment() {
        if (treeViewForum.getSelectionModel().getSelectedItem() == null) {
            GlobalUtils.createError("Input error", "Select a comment before deleting it");
        }
        Comment currentComment = userHib.getObjectById(Comment.class, treeViewForum.getSelectionModel().getSelectedItem().getValue().getId());
        deleteAllComments(currentComment);
        repopulateTreeView(userHib.getObjectById(Forum.class, currentForum.getId()).getComments());
        textAreaForum.clear();
        userHib.deleteObject(currentComment);
        System.out.println("Comment deleted");
    }

    public void updateComment() {
        if (treeViewForum.getSelectionModel().getSelectedItem() == null || textAreaForum.getText() == null) {
            GlobalUtils.createError("Input error", "Select a comment you want to update or enter a text");
        }

        Comment currentComment = userHib.getObjectById(Comment.class, treeViewForum.getSelectionModel().getSelectedItem().getValue().getId());
        currentComment.setText(textAreaForum.getText());
        userHib.updateObject(currentComment);
        repopulateTreeView(userHib.getObjectById(Forum.class, currentForum.getId()).getComments());
    }

    public void mouseClickedTreeView() {
        textAreaForum.clear();
        if (treeViewForum.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Comment currentComment = userHib.getObjectById(Comment.class, treeViewForum.getSelectionModel().getSelectedItem().getValue().getId());
        textAreaForum.setText(currentComment.getText());
    }
}
