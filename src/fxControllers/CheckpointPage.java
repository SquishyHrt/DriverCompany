package fxControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Checkpoint;
import utils.GlobalUtils;

import java.time.LocalDate;

public class CheckpointPage {
    @FXML
    public TextField checkpointAddressField;
    @FXML
    public DatePicker checkpointArrivalDate;
    @FXML
    public CheckBox checkpointLongStopCheck;
    @FXML
    public Button checkpointAddButton;

    public Checkpoint checkpoint;

    public void createCheckpoint() {
        if (checkpointAddressField.getText().isEmpty() || checkpointArrivalDate.getValue() == null) {
            GlobalUtils.createError("Input error","Please fill all fields");
            return;
        }

        checkpoint = new Checkpoint(checkpointAddressField.getText(), checkpointLongStopCheck.isSelected(), checkpointArrivalDate.getValue());
        Stage stage = (Stage) checkpointAddButton.getScene().getWindow();
        stage.close();
    }
}
