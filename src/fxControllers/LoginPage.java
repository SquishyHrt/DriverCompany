package fxControllers;

import hibernateControllers.UserHib;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Driver;
import model.Manager;
import model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

import static utils.GlobalUtils.createError;

public class LoginPage {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("DriverCompany2");
    UserHib userHib = new UserHib(entityManagerFactory);

    public void validate() throws IOException {
        if (loginField.getText() == null || loginField.getText().trim().isEmpty()) {
            createError("Error login", "Enter a login value");
            return;
        }
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            createError("Error password", "Enter a password value");
            return;
        }

        User user = userHib.findUserByCredentials(loginField.getText(), passwordField.getText());
        if (user == null) {
            createError("Error login", "Wrong login or password");
            return;
        }

        if (user instanceof Manager) {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/manager-page.fxml"));
            Parent parent = fxmlLoader.load();

            ManagerPage managerPage = fxmlLoader.getController();
            managerPage.setInfo((Manager) user, entityManagerFactory);

            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("ManagerDeezNuts");
            stage.setScene(scene);
            stage.show();
        } else if (user instanceof Driver) {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/driver-page.fxml"));
            Parent parent = fxmlLoader.load();

            DriverPage driverPage = fxmlLoader.getController();
            driverPage.setInfo((Driver) user, entityManagerFactory);

            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("DriverDeezNuts");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void register() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/registration-page.fxml"));
        Parent parent = fxmlLoader.load();
        RegistrationPage registrationPage = fxmlLoader.getController();
        registrationPage.setData(entityManagerFactory);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("RegisterDeezNuts");
        stage.setScene(scene);
        stage.show();
    }

    public void enterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getCode().toString().equals("ENTER")) {
            try {
                validate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
