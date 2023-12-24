package fxControllers;

import hibernateControllers.UserHib;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Driver;
import model.Manager;
import utils.GlobalUtils;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegistrationPage implements Initializable {
    @FXML
    public TextField loginField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordField;
    @FXML
    public TextField phoneNumberField;
    @FXML
    public DatePicker birthDateField;
    @FXML
    public CheckBox checkBoxIsAdmin;
    @FXML
    public RadioButton radioDriver;
    @FXML
    public RadioButton radioManager;
    @FXML
    public TextField managerEmailField;
    @FXML
    public DatePicker medCertDateField;
    @FXML
    public TextField medCertificateNumberField;
    @FXML
    public TextField driverLicenseField;
    @FXML
    public Button returnButton;
    @FXML
    public Button createButton;

    private EntityManagerFactory entityManagerFactory;
    private UserHib userHib;

    public void setData(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.userHib = new UserHib(entityManagerFactory);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        radioManager.setSelected(true);
        checkBoxIsAdmin.setVisible(false);
        disableMDFields();
    }

    public void returnToPrevious() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("LoginDeezNuts");
        stage.setScene(scene);
        stage.show();
    }

    private boolean ensureBaseFilled() {
        return loginField.getText() != null && !loginField.getText().trim().isEmpty() && passwordField.getText() != null && !passwordField.getText().trim().isEmpty() && nameField.getText() != null && !nameField.getText().trim().isEmpty() && surnameField.getText() != null && !surnameField.getText().trim().isEmpty() && birthDateField.getValue() != null && phoneNumberField.getText() != null && !phoneNumberField.getText().trim().isEmpty();
    }

    private boolean ensureDriverFilled() {
        return medCertDateField.getValue() != null && medCertificateNumberField.getText() != null && !medCertificateNumberField.getText().trim().isEmpty() && driverLicenseField.getText() != null && !driverLicenseField.getText().trim().isEmpty();
    }

    private boolean ensureManagerFilled() {
        return managerEmailField.getText() != null && !managerEmailField.getText().trim().isEmpty();
    }

    private boolean ensureSamePassword() {
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty() || repeatPasswordField.getText() == null || repeatPasswordField.getText().trim().isEmpty()) {
            return false;
        }
        return Objects.equals(passwordField.getText(), repeatPasswordField.getText());
    }

    public void createNewUser() throws IOException, SQLException {
        if (radioDriver.isSelected()) {
            if (ensureBaseFilled() && ensureDriverFilled() && ensureSamePassword()) {
                Driver driver = new Driver(loginField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), LocalDate.parse(birthDateField.getValue().toString()), phoneNumberField.getText(), LocalDate.parse(medCertDateField.getValue().toString()), medCertificateNumberField.getText(), driverLicenseField.getText());
                if (!userHib.checkUserDuplicatesDB(driver, driver.getClass())) {
                    userHib.createObject(driver, Driver.class);
                    System.out.println("New Driver created");
                    returnToPrevious();
                }
            } else {
                GlobalUtils.createError("Input error", "Fill every fields and ensure that passwords are the same");
            }
        } else {
            if (ensureBaseFilled() && ensureManagerFilled() && ensureSamePassword()) {
                Manager manager = new Manager(loginField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), LocalDate.parse(birthDateField.getValue().toString()), phoneNumberField.getText(), managerEmailField.getText());
                if (!userHib.checkUserDuplicatesDB(manager, manager.getClass())) {
                    userHib.createObject(manager, Manager.class);
                    System.out.println("New Manager created");
                    returnToPrevious();
                }
            } else {
                GlobalUtils.createError("Input error", "Fill every fields and ensure that passwords are the same");
            }
        }
    }

    public void disableMDFields() {
        if (radioDriver.isSelected()) {
            medCertificateNumberField.setDisable(false);
            driverLicenseField.setDisable(false);
            medCertDateField.setDisable(false);
            managerEmailField.setDisable(true);
            checkBoxIsAdmin.setVisible(false);
        } else {
            medCertificateNumberField.setDisable(true);
            driverLicenseField.setDisable(true);
            medCertDateField.setDisable(true);
            managerEmailField.setDisable(false);
        }
    }
}
