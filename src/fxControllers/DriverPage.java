package fxControllers;

import hibernateControllers.UserHib;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Checkpoint;
import model.Destination;
import model.Driver;
import utils.GlobalUtils;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DriverPage implements Initializable {
    @FXML public ListView<Destination> listViewMyTrips, listViewAvailableTrip;
    @FXML public ListView<Checkpoint> listViewMyCheckpoint, listViewCheckpointAvailable;
    @FXML public Label myStartCityLabel, myEndCityLabel, myDepDateLabel, myArriDateLabel;
    @FXML public Label availableStartCityLabel, availableEndCityLabel, availableDepDateLabel, availableArriDateLabel;
    @FXML public Label dateLabel, usernameLabel;
    @FXML public Button addMyCheckpointButton, deleteMyCheckpointButton, selectAvailableTripButton, logOutButton, removeMyTripButton, forumButton;

    private Driver loggedDriver;
    private UserHib userHib;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearAllMyTripsFields();
        clearAllAvailableTrip();
        dateLabel.setText(LocalDate.now().toString());
    }

    private void fillPageInfoFromDB() {
        List<Destination> allDestination = userHib.getAllRecords(Destination.class);
        List<Destination> availableDestination = new ArrayList<>();
        for (Destination d: allDestination) {
            if (d.getDriver() == null)
                availableDestination.add(d);
        }

        listViewAvailableTrip.setItems(FXCollections.observableArrayList(availableDestination));

        List<Destination> driverDestinations = loggedDriver.getMyDestinations();
        listViewMyTrips.setItems(FXCollections.observableArrayList(driverDestinations));
    }

    public void setInfo(Driver driver, EntityManagerFactory entityManagerFactory) {
        this.loggedDriver = driver;
        this.userHib = new UserHib(entityManagerFactory);

        usernameLabel.setText(driver.getLogin());

        fillPageInfoFromDB();
    }

    public void mouseClickMyTrip() {
        if (listViewMyTrips.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Destination currentDestination = userHib.getObjectById(Destination.class, listViewMyTrips.getSelectionModel().getSelectedItem().getId());
        myStartCityLabel.setText(currentDestination.getStartCity());
        myEndCityLabel.setText(currentDestination.getEndCity());
        listViewMyCheckpoint.setItems(FXCollections.observableArrayList(currentDestination.getCheckpointList()));
        myDepDateLabel.setText(currentDestination.getDepartureDate().toString());
        myArriDateLabel.setText(currentDestination.getArrivalDate().toString());
    }

    public void addMyCheckpoint() {
        if (listViewMyTrips.getSelectionModel().getSelectedItem() == null) {
            GlobalUtils.createError("Input error", "You have to select your trip first");
            return;
        }

        Checkpoint newCheckpoint = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/checkpoint-page.fxml"));
            Parent parent = fxmlLoader.load();

            CheckpointPage destinationPage = fxmlLoader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("DestinationDeezNuts");
            stage.setScene(scene);
            stage.showAndWait();

            newCheckpoint = destinationPage.checkpoint;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newCheckpoint == null) {
            GlobalUtils.createError("Input error", "Error while creating new checkpoint");
            return;
        }

        Destination currentDestination = userHib.getObjectById(Destination.class, listViewMyTrips.getSelectionModel().getSelectedItem().getId());
        newCheckpoint.setAssignedDestination(currentDestination);
        currentDestination.getCheckpointList().add(newCheckpoint);

        listViewMyCheckpoint.getItems().add(newCheckpoint);
        userHib.updateObject(currentDestination);
    }

    public void deleteMyCheckpoint() {
        if (listViewMyTrips.getSelectionModel().getSelectedItem() == null || listViewMyCheckpoint.getItems().size() == 0 || listViewMyCheckpoint.getSelectionModel().getSelectedItem() == null) {
            GlobalUtils.createError("Input error", "You have to select your trip first");
            return;
        }
        listViewMyCheckpoint.getItems().remove(listViewMyCheckpoint.getSelectionModel().getSelectedItem());
        Destination currentDestination = userHib.getObjectById(Destination.class, listViewMyTrips.getSelectionModel().getSelectedItem().getId());

        for (Checkpoint c: currentDestination.getCheckpointList()) {
            userHib.deleteObject(userHib.getObjectById(Checkpoint.class, c.getId()));
        }

        currentDestination.setCheckpointList(listViewMyCheckpoint.getItems());

        for (Checkpoint c: listViewMyCheckpoint.getItems()) {
            c.setAssignedDestination(currentDestination);
        }

        userHib.updateObject(currentDestination);
    }

    public void removeMyTrip() {
        if (listViewMyTrips.getSelectionModel().getSelectedItem() == null || listViewMyTrips.getItems().size() == 0) {
            GlobalUtils.createError("Input error", "You have to select your trip first");
            return;
        }
        Destination currentDestination = userHib.getObjectById(Destination.class, listViewMyTrips.getSelectionModel().getSelectedItem().getId());
        if (currentDestination != null) {
            currentDestination.setDriver(null);
            userHib.updateObject(currentDestination);
            loggedDriver = userHib.getObjectById(Driver.class, loggedDriver.getId());
            loggedDriver.getMyDestinations().remove(currentDestination);
            userHib.updateObject(loggedDriver);

            listViewMyTrips.getItems().remove(listViewMyTrips.getSelectionModel().getSelectedItem());
            listViewAvailableTrip.getItems().add(currentDestination);
            clearAllMyTripsFields();
        }
    }

    public void mouseClickSelectTrip() {
        if (listViewAvailableTrip.getSelectionModel().getSelectedItem() == null || listViewAvailableTrip.getItems().size() == 0)
            return;

        Destination currentDestination = userHib.getObjectById(Destination.class, listViewAvailableTrip.getSelectionModel().getSelectedItem().getId());
        availableStartCityLabel.setText(currentDestination.getStartCity());
        availableEndCityLabel.setText(currentDestination.getEndCity());
        availableDepDateLabel.setText(currentDestination.getDepartureDate().toString());
        availableArriDateLabel.setText(currentDestination.getArrivalDate().toString());
        listViewCheckpointAvailable.setItems(FXCollections.observableArrayList(currentDestination.getCheckpointList()));
    }

    public void selectAvailableTrip() {
        if (listViewAvailableTrip.getSelectionModel().getSelectedItem() == null || listViewAvailableTrip.getItems().size() == 0) {
            GlobalUtils.createError("Input error", "You have to select your trip first");
            return;
        }

        Destination currentDestination = userHib.getObjectById(Destination.class, listViewAvailableTrip.getSelectionModel().getSelectedItem().getId());
        loggedDriver = userHib.getObjectById(Driver.class, loggedDriver.getId());
        currentDestination.setDriver(loggedDriver);
        userHib.updateObject(currentDestination);
        loggedDriver.getMyDestinations().add(currentDestination);
        userHib.updateObject(loggedDriver);
        listViewAvailableTrip.getItems().remove(listViewAvailableTrip.getSelectionModel().getSelectedItem());
        listViewMyTrips.getItems().add(currentDestination);
        clearAllAvailableTrip();
    }

    public void logOut() {
        GlobalUtils.logOut(loggedDriver, logOutButton);
    }

    @FXML
    private void clearAllMyTripsFields() {
        myStartCityLabel.setText(" ");
        myEndCityLabel.setText(" ");
        myDepDateLabel.setText(" ");
        myArriDateLabel.setText(" ");
        listViewMyCheckpoint.getItems().clear();
    }

    private void clearAllAvailableTrip() {
        availableStartCityLabel.setText(" ");
        availableEndCityLabel.setText(" ");
        availableDepDateLabel.setText(" ");
        availableArriDateLabel.setText(" ");
        listViewCheckpointAvailable.getItems().clear();
    }

    public void openForum() {
        Scene currentScene = forumButton.getScene();
        Stage stage = (Stage) forumButton.getScene().getWindow();
        GlobalUtils.openForum(loggedDriver, currentScene, stage, userHib);
    }
}
