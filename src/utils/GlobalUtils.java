package utils;

import fxControllers.ForumPage;
import fxControllers.LoginPage;
import hibernateControllers.UserHib;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class GlobalUtils {
    public static void createError(String text1, String text2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(text1);
        alert.setHeaderText(null);
        alert.setContentText(text2);
        alert.showAndWait();
    }

    public static void logOut(User loggedUser, Button logOutButton) {
        try {
            System.out.println("User: " + loggedUser.getLogin() + " logged out");
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/login-page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.setTitle("LoginDeezNuts");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openForum(User user, Scene previousScene, Stage stage, UserHib userHib) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/forum-page.fxml"));
            Parent parent = fxmlLoader.load();

            ForumPage forumPage = fxmlLoader.getController();
            forumPage.setUser(user, previousScene, userHib);

            Scene scene = new Scene(parent);
            stage.setTitle("ForumDeezNuts");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
