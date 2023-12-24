package fxControllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Driver;

public class DestinationDriver {
    @FXML
    public ListView<Driver> destDriverListView;
    public Driver selectedDriver;

    public void setDriverList(ObservableList<Driver> truckObservableList) {
        destDriverListView.setItems(truckObservableList);
    }

    public void selectDriverClick() {
        selectedDriver = destDriverListView.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) destDriverListView.getScene().getWindow();
        stage.close();
    }


}
