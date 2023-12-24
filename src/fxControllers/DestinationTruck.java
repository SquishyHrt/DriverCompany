package fxControllers;

import hibernateControllers.UserHib;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Truck;

import java.util.ArrayList;
import java.util.List;

public class DestinationTruck {
    @FXML
    public ListView<Truck> destTruckListView;

    public Truck selectedTruck;

    private UserHib userHib;

    public void setTruckList(UserHib userHib) {
        List<Truck> allTrucks = userHib.getAllRecords(Truck.class);
        List<Truck> sortedTrucks = new ArrayList<>();
        for (Truck t: allTrucks) {
            if (t.getCurrentDestination() == null) {
                sortedTrucks.add(t);
            }
        }
        destTruckListView.setItems(FXCollections.observableArrayList(sortedTrucks));
        this.userHib = userHib;
    }

    public void selectTruckClick() {
        selectedTruck = destTruckListView.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) destTruckListView.getScene().getWindow();
        stage.close();
    }
}
