package fxControllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Manager;

public class DestinationManager {
    @FXML
    public ListView<Manager> destManagerListView;

    public Manager selectedManager;

    public void setManagerList(ObservableList<Manager> managerObservableList) {
        destManagerListView.setItems(managerObservableList);
    }

    public void selectManagerClick() {
        selectedManager = destManagerListView.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) destManagerListView.getScene().getWindow();
        stage.close();
    }
}
