package fxControllers;

import hibernateControllers.UserHib;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import model.*;
import utils.GlobalUtils;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static utils.GlobalUtils.createError;

public class ManagerPage implements Initializable {
    @FXML public TextField makeField, destinationStartCityField, destinationEndCityField, modelField, yearField, odometerField, truckCapacityField, titleCargoField, weightCargoField, descriptionCargoField;
    @FXML public ComboBox<TyreType> tyreTypeField;
    @FXML public ComboBox<CargoType> comboBoxCargoType;
    @FXML public ComboBox<String> destinationComboBox;
    @FXML public Button forumButton, addTruckButton, updateTruckButton, deleteTruckButton, logOutButton, destinationChoseDriverButton, destinationChoseManagerButton, destinationAddCheckButton, destinationDeleteCheckButton, destinationAddDestinationButton, destinationDeleteDestButton, destinationUpdateButton, addCargoButton, deleteCargoButton, updateCargoButton, helpCargoButton, helpDestinationButton, destinationChoseTruckButton, cargoManagementAssignDestinationButton, cargoManagementUnassignDestinationButton;
    @FXML public ListView<Truck> listViewTruck;
    @FXML public ListView<Checkpoint> destinationCheckpointListView;
    @FXML public ListView<Destination> destinationListView;
    @FXML public ListView<Cargo> cargoManagementCargoListView;
    @FXML public ListView<Destination> cargoManagementDestinationListView;
    @FXML public Label creationDateLabel, currentDestinationCargoLabel, updateDateLabel, userNameLabel, destinationSelectedDriverLabel, destinationSelectedManagerLabel, destinationSelectedTruckLabel;
    @FXML public Tab userManagementTab, cargoManagementTab, truckManagementTab, destinationManagementTab;
    @FXML public DatePicker destinationManagementDepDate, destinationManagementArriDate;
    @FXML public TableView<Manager> managerTableView;
    @FXML public TableView<Driver> driverTableView;
    @FXML public TableColumn<Manager, String> managerTableLogin, managerTableName, managerTableSurname, managerTablePhoneNum, managerTableEmail;
    @FXML public TableColumn<Manager, LocalDate> managerTableBirthDate;
    @FXML public TableColumn<Driver, String> driverTableLogin, driverTableName, driverTableSurname, driverTablePhoneNum, driverTableMedNum, driverTableLicense;
    @FXML public TableColumn<Driver, LocalDate> driverTableBirthDate, driverTableMedDate;
    @FXML public TableColumn<Manager, Boolean> managerTableIsAdmin;

    public Truck destTruck;
    public Manager destManager;
    private Driver destDriver;
    public TabPane tabPane;

    private Manager loggedUser;
    private UserHib userHib;
    private String currentSort;

    private List<Manager> managerList;
    private List<Driver> driverList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargoManagementDestinationListView.itemsProperty().bind(destinationListView.itemsProperty());

        tyreTypeField.getItems().removeAll(tyreTypeField.getItems());
        tyreTypeField.getItems().addAll(TyreType.BUDGET_TYRE, TyreType.SUMMER_TYRE, TyreType.WINTER_TYRE, TyreType.ENERGY_SAVING_TYRE);
        tyreTypeField.setValue(TyreType.BUDGET_TYRE);

        comboBoxCargoType.getItems().removeAll(comboBoxCargoType.getItems());
        comboBoxCargoType.getItems().addAll(CargoType.ALCOHOL, CargoType.MIX, CargoType.FOOD, CargoType.PAPER, CargoType.CONSUMER_ELECTRONICS, CargoType.ELECTRONICS);
        comboBoxCargoType.setValue(CargoType.ALCOHOL);

        destinationComboBox.getItems().setAll("Date", "Start city", "End city");
        destinationComboBox.setValue("Date");
        currentSort = "Date";

        managerList = null;
        driverList = null;

        destinationSelectedDriverLabel.setText(" ");
        destinationSelectedManagerLabel.setText(" ");
        destinationSelectedTruckLabel.setText(" ");

    }

    private void fillCargoTruckDestinationFromDB() {
        List<Truck> trucks = userHib.getAllRecords(Truck.class);
        List<Destination> destinations = userHib.getAllRecords(Destination.class);
        List<Cargo> cargos = userHib.getAllRecords(Cargo.class);
        listViewTruck.setItems(FXCollections.observableArrayList(trucks));
        destinationListView.setItems(FXCollections.observableArrayList(destinations));
        cargoManagementCargoListView.setItems(FXCollections.observableArrayList(cargos));

        managerTableLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        managerTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        managerTableSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        managerTableBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        managerTablePhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        managerTableEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        managerTableIsAdmin.setCellValueFactory(cellData -> {
            Manager manager = userHib.getObjectById(Manager.class, cellData.getValue().getId());
            SimpleBooleanProperty property = new SimpleBooleanProperty(manager.isAdmin());
            property.addListener((observable, oldValue, newValue) -> {
                manager.setAdmin(newValue);
                userHib.updateObject(manager);
                managerList = userHib.getAllRecords(Manager.class);
            });
            return property;
        });

        driverTableLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        driverTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        driverTableSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        driverTableBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        driverTablePhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        driverTableMedDate.setCellValueFactory(new PropertyValueFactory<>("medCertificateDate"));
        driverTableMedNum.setCellValueFactory(new PropertyValueFactory<>("medCertificateNumber"));
        driverTableLicense.setCellValueFactory(new PropertyValueFactory<>("driverLicense"));

        managerTableView.setItems(FXCollections.observableArrayList(managerList));
        driverTableView.setItems(FXCollections.observableArrayList(driverList));
    }

    public void setInfo(Manager manager, EntityManagerFactory entityManagerFactory) {
        this.loggedUser = manager;
        this.userHib = new UserHib(entityManagerFactory);

        userNameLabel.setText(manager.getLogin());
        userManagementTab.setDisable(!manager.isAdmin());
        tabPane.getSelectionModel().select(manager.isAdmin() ? 0 : 1);
        driverList = userHib.getAllRecords(Driver.class);
        managerList = userHib.getAllRecords(Manager.class);

        fillCargoTruckDestinationFromDB();
        editableCols();
    }

    public void createTruck() {
        if (ensureAllFieldTrucks() && ensureFieldIntegerTruck()) {
            Truck truck = new Truck(makeField.getText(), modelField.getText(), Integer.parseInt(yearField.getText()), Double.parseDouble(odometerField.getText()), Double.parseDouble(truckCapacityField.getText()), tyreTypeField.getValue());
            userHib.createObject(truck, Truck.class);
            clearAllTrucksFields();
        } else {
            createError("Input error", "Fill all the fields or make sure that you put integer");
            System.out.println("Fill all the fields or make sure that you put integer");
        }
        listViewTruck.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Truck.class)));
    }

    public void createCargo() {
        if (ensureAllFieldCargo() && ensureFieldIntegerCargo()) {
            Cargo cargo = new Cargo(titleCargoField.getText(), Double.parseDouble(weightCargoField.getText()), comboBoxCargoType.getValue(), descriptionCargoField.getText());
            userHib.createObject(cargo, Cargo.class);
            clearAllCargoFields();
        } else {
            createError("Input error", "Fill all the fields or make sure that you put integer");
            System.out.println("Fill all the cargo fields or make sure that you put integers");
        }
        cargoManagementCargoListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Cargo.class)));
    }

    private boolean alertCreateDestination() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("Are you sure you don't want to assign a Driver now ?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void createDestination() {
        if (!ensureAllFieldDestination()) {
            createError("Input error", "Make sure to select a manager, a truck, a driver and fill all the fields");
            return;
        }

        if (!ensureDateOrder()) {
            createError("Input error", "End date must be after start date");
            return;
        }

        boolean wantDriver = true;
        if (destDriver == null)
            wantDriver = !alertCreateDestination();

        Manager manager = userHib.getObjectById(Manager.class, destManager.getId());
        Truck truck = userHib.getObjectById(Truck.class, destTruck.getId());
        Driver driver = wantDriver ? userHib.getObjectById(Driver.class, destDriver.getId()) : null;

        Destination newDestination = new Destination(destinationStartCityField.getText(), destinationEndCityField.getText(), manager, truck, driver, destinationManagementDepDate.getValue(), destinationManagementArriDate.getValue());

        manager.getDestinationList().add(newDestination);
        if (wantDriver)
            driver.getMyDestinations().add(newDestination);

        newDestination.setCheckpointList(destinationCheckpointListView.getItems());

        for (Checkpoint c: destinationCheckpointListView.getItems()) {
            c.setAssignedDestination(newDestination);
        }

        userHib.createObject(newDestination, Destination.class);

        destinationListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Destination.class)));
        clearAllDestinationFields();
        System.out.println("Destination Created");
    }

    public void mouseClickedCargo() {
        if (cargoManagementCargoListView.getItems().size() == 0 || cargoManagementCargoListView.getSelectionModel().getSelectedItem() == null)
            return;

        clearAllCargoFields();
        Cargo cargo = userHib.getObjectById(Cargo.class, cargoManagementCargoListView.getSelectionModel().getSelectedItem().getId());

        if (cargo == null)
            return;

        titleCargoField.setText(cargo.getTitle());
        weightCargoField.setText(String.valueOf(cargo.getWeight()));
        comboBoxCargoType.setValue(cargo.getCargoType());
        descriptionCargoField.setText(cargo.getDescription());
        updateDateLabel.setText(cargo.getDateUpdated() == null ? " " : cargo.getDateUpdated().toString());
        creationDateLabel.setText(cargo.getDateCreated().toString());
        currentDestinationCargoLabel.setText(cargo.getAssignedDestination() == null ? " " : cargo.getAssignedDestination().toString());
    }

    public void mouseClickedTrucks() {
        if (listViewTruck.getItems().size() == 0 || listViewTruck.getSelectionModel().getSelectedItem() == null)
            return;

        clearAllTrucksFields();
        Truck selectedTruck = userHib.getObjectById(Truck.class, listViewTruck.getSelectionModel().getSelectedItem().getId());

        if (selectedTruck == null)
            return;

        makeField.setText(selectedTruck.getMake());
        modelField.setText(selectedTruck.getModel());
        yearField.setText(String.valueOf(selectedTruck.getYear()));
        odometerField.setText(String.valueOf(selectedTruck.getOdometer()));
        truckCapacityField.setText(String.valueOf(selectedTruck.getFuelTankCapacity()));
        tyreTypeField.setValue(selectedTruck.getTyreType());
    }

    public void destinationClickListView() {
        if (destinationListView.getItems().size() == 0 || destinationListView.getSelectionModel().getSelectedItem() == null)
            return;

        clearAllDestinationFields();
        Destination currentDestination = userHib.getObjectById(Destination.class, destinationListView.getSelectionModel().getSelectedItem().getId());
        if (currentDestination == null)
            return;

        destinationStartCityField.setText(currentDestination.getStartCity());
        destinationEndCityField.setText(currentDestination.getEndCity());
        destinationCheckpointListView.setItems(FXCollections.observableArrayList(currentDestination.getCheckpointList()));
        destManager = currentDestination.getResponsibleManager();
        destDriver = currentDestination.getDriver();
        String destTruckString = currentDestination.getTruck() == null ? " " : currentDestination.getTruck().toString();
        destinationManagementArriDate.setValue(currentDestination.getArrivalDate());
        destinationManagementDepDate.setValue(currentDestination.getDepartureDate());
        destinationSelectedManagerLabel.setText(currentDestination.getResponsibleManager() == null ? " " : currentDestination.getResponsibleManager().toString());
        destinationSelectedDriverLabel.setText(currentDestination.getDriver() == null ? " " : currentDestination.getDriver().toString());
        destinationSelectedTruckLabel.setText(destTruckString);
    }

    public void deleteSelectedTruck() {
        if (listViewTruck.getItems().size() == 0 || listViewTruck.getSelectionModel().getSelectedItem() == null) {
            createError("Input error", "No truck selected");
            return;
        }

        Truck currentTruck = userHib.getObjectById(Truck.class, listViewTruck.getSelectionModel().getSelectedItem().getId());

        userHib.deleteObject(currentTruck);
        listViewTruck.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Truck.class)));
        clearAllTrucksFields();
        System.out.println("Truck deleted");
    }

    public void deleteSelectedCargo() {
        if (cargoManagementCargoListView.getItems().size() == 0 || cargoManagementCargoListView.getSelectionModel().getSelectedItem() == null) {
            createError("Input error", "No cargo selected");
            return;
        }

        Cargo currentCargo = userHib.getObjectById(Cargo.class, cargoManagementCargoListView.getSelectionModel().getSelectedItem().getId());

        if (currentCargo.getAssignedDestination() != null) {
            Destination currentDestination = userHib.getObjectById(Destination.class, currentCargo.getAssignedDestination().getId());
            if (currentDestination != null) {
                currentDestination.getCargoList().remove(currentCargo);
                userHib.updateObject(currentDestination);
            }
        }

        userHib.deleteObject(userHib.getObjectById(Cargo.class, currentCargo.getId()));
        cargoManagementCargoListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Cargo.class)));
        clearAllCargoFields();
        System.out.println("Cargo deleted");
    }

    public void deleteDestination() {
        if (destinationListView.getItems().size() == 0 || destinationListView.getSelectionModel().getSelectedItem() == null) {
            createError("Input error", "No destination selected");
            return;
        }

        Destination currentDestination = userHib.getObjectById(Destination.class, destinationListView.getSelectionModel().getSelectedItem().getId());

        for (Cargo c: currentDestination.getCargoList()) {
            c = userHib.getObjectById(Cargo.class, c.getId());
            c.setAssignedDestination(null);
            userHib.updateObject(c);
        }

        if (currentDestination.getDriver() != null) {
            Driver currentDriver = userHib.getObjectById(Driver.class, currentDestination.getDriver().getId());
            currentDriver.getMyDestinations().remove(currentDestination);
            userHib.updateObject(userHib.getObjectById(Driver.class, currentDriver.getId()));
        }

        if (currentDestination.getTruck() != null) {
            Truck currentTruck = userHib.getObjectById(Truck.class, currentDestination.getTruck().getId());
            currentTruck.setCurrentDestination(null);
            userHib.updateObject(userHib.getObjectById(Truck.class, currentTruck.getId()));
        }

        if (currentDestination.getResponsibleManager() != null) {
            Manager currentManager = userHib.getObjectById(Manager.class, currentDestination.getResponsibleManager().getId());
            currentManager.getDestinationList().remove(currentDestination);
            userHib.updateObject(userHib.getObjectById(Manager.class, currentManager.getId()));
        }

        userHib.deleteObject(userHib.getObjectById(Destination.class, currentDestination.getId()));
        destinationListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Destination.class)));
        clearAllDestinationFields();
        System.out.println("Destination deleted");
    }

    public void deleteCheckpointPressed() {
        Checkpoint currentCheck = destinationCheckpointListView.getSelectionModel().getSelectedItem();
        destinationCheckpointListView.getItems().remove(currentCheck);
    }

    public void updateSelectedTruck() {
        if (ensureAllFieldTrucks() && ensureFieldIntegerTruck()) {
            Truck currentTruck = userHib.getObjectById(Truck.class, listViewTruck.getSelectionModel().getSelectedItem().getId());
            currentTruck.setMake(makeField.getText());
            currentTruck.setModel(modelField.getText());
            currentTruck.setYear(Integer.parseInt(yearField.getText()));
            currentTruck.setOdometer(Double.parseDouble(odometerField.getText()));
            currentTruck.setFuelTankCapacity(Double.parseDouble(truckCapacityField.getText()));
            currentTruck.setTyreType(tyreTypeField.getValue());
            userHib.updateObject(currentTruck);
            System.out.println("Truck updated");
            listViewTruck.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Truck.class)));
            clearAllTrucksFields();
        } else {
            createError("Input error", "Fill all the fields or make sure that you put integer");
            System.out.println("Fill all the fields or make sure you put integer");
        }
    }

    public void updateCargo() {
        if (ensureAllFieldCargo() && ensureFieldIntegerCargo()) {
            Cargo currentCargo = userHib.getObjectById(Cargo.class, cargoManagementCargoListView.getSelectionModel().getSelectedItem().getId());
            currentCargo.setTitle(titleCargoField.getText());
            currentCargo.setWeight(Double.parseDouble(weightCargoField.getText()));
            currentCargo.setCargoType(comboBoxCargoType.getValue());
            currentCargo.setDescription(descriptionCargoField.getText());
            currentCargo.setDateUpdated(LocalDate.now());
            userHib.updateObject(currentCargo);
            System.out.println("Cargo updated");
            cargoManagementCargoListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Cargo.class)));
            clearAllCargoFields();
        } else {
            createError("Input error", "Fill all the fields or make sure that you put integer");
            System.out.println("Fill all the cargo fields or make sure you put integer");
        }
    }

    public void updateDestination() {
        if (destinationListView.getSelectionModel().getSelectedItem() != null && ensureAllFieldDestination()) {
            if (!ensureDateOrder()) {
                createError("Input error", "End date must be after start date");
                return;
            }
            Destination currentDestination = userHib.getObjectById(Destination.class, destinationListView.getSelectionModel().getSelectedItem().getId());
            currentDestination.setDateUpdated(LocalDate.now());
            currentDestination.setStartCity(destinationStartCityField.getText());
            currentDestination.setEndCity(destinationEndCityField.getText());
            currentDestination.setTruck(destTruck != null ? destTruck : currentDestination.getTruck());
            currentDestination.setResponsibleManager(destManager);
            currentDestination.setDriver(destDriver);
            currentDestination.setDateUpdated(LocalDate.now());

            for (Checkpoint c: currentDestination.getCheckpointList()) {
                userHib.deleteObject(userHib.getObjectById(Checkpoint.class, c.getId()));
            }

            currentDestination.setCheckpointList(destinationCheckpointListView.getItems());

            if (destinationCheckpointListView.getItems().size() > 0) {
                for (Checkpoint c : destinationCheckpointListView.getItems()) {
                    c.setAssignedDestination(currentDestination);
                }
            } else {
                currentDestination.setCheckpointList(null);
            }

            userHib.updateObject(currentDestination);

            clearAllDestinationFields();
            destinationListView.setItems(FXCollections.observableArrayList(userHib.getAllRecords(Destination.class)));
        } else {
            createError("Input error", "No destination selected or fill all the fields");
        }
    }

    public void destChoseDriver() {
        driverList = userHib.getAllRecords(Driver.class);
        if (driverList == null || driverList.size() == 0) {
            createError("Data error", "No driver created");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/destinationDriver-page.fxml"));
            Parent parent = fxmlLoader.load();

            DestinationDriver destinationPage = fxmlLoader.getController();
            destinationPage.setDriverList(FXCollections.observableArrayList(driverList));

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("DestinationDeezNuts");
            stage.setScene(scene);
            stage.showAndWait();

            if (destinationPage.selectedDriver == null) {
                createError("Error message", "No driver selected");
                return;
            }

            destDriver = userHib.getObjectById(Driver.class, destinationPage.selectedDriver.getId());

            if (destDriver == null) {
                createError("Error message", "No driver selected or pulled from database");
            } else {
                destinationSelectedDriverLabel.setText(destinationPage.selectedDriver.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destChoseManager() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/destinationManager-page.fxml"));
            Parent parent = fxmlLoader.load();

            DestinationManager destinationPage = fxmlLoader.getController();
            destinationPage.setManagerList(FXCollections.observableArrayList(managerList));

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("DestinationDeezNuts");
            stage.setScene(scene);
            stage.showAndWait();

            if (destinationPage.selectedManager == null) {
                createError("Error message", "No manager selected");
                return;
            }

            destManager = userHib.getObjectById(Manager.class, destinationPage.selectedManager.getId());

            if (destManager == null) {
                createError("Error message", "No manager selected or pulled from database");
            } else {
                destinationSelectedManagerLabel.setText(destinationPage.selectedManager.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destChoseTruck() {
        if (listViewTruck.getItems() == null || listViewTruck.getItems().size() == 0) {
            GlobalUtils.createError("Data errors", "No trucks created. Create at least one truck");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/destinationTruck-page.fxml"));
            Parent parent = fxmlLoader.load();

            DestinationTruck destinationPage = fxmlLoader.getController();
            destinationPage.setTruckList(userHib);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("DestinationDeezNuts");
            stage.setScene(scene);
            stage.showAndWait();

            if (destinationPage.selectedTruck == null) {
                createError("Error message", "No truck selected or pulled from database");
                return;
            }

            destTruck = userHib.getObjectById(Truck.class, destinationPage.selectedTruck.getId());

            if (destTruck == null) {
                createError("Error message", "No truck selected");
            } else {
                destinationSelectedTruckLabel.setText(destinationPage.selectedTruck.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCheckpointPressed() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/checkpoint-page.fxml"));
            Parent parent = fxmlLoader.load();

            CheckpointPage destinationPage = fxmlLoader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("DestinationDeezNuts");
            stage.setScene(scene);
            stage.showAndWait();

            Checkpoint newCheckpoint = destinationPage.checkpoint;
            destinationCheckpointListView.getItems().add(newCheckpoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearAllCargoFields() {
        titleCargoField.clear();
        weightCargoField.clear();
        comboBoxCargoType.setValue(CargoType.ALCOHOL);
        descriptionCargoField.clear();
        currentDestinationCargoLabel.setText(" ");
        creationDateLabel.setText(" ");
        updateDateLabel.setText(" ");
    }

    public void clearAllTrucksFields() {
        makeField.clear();
        modelField.clear();
        yearField.clear();
        odometerField.clear();
        truckCapacityField.clear();
        tyreTypeField.setValue(TyreType.BUDGET_TYRE);
    }

    public void clearAllDestinationFields() {
        destinationEndCityField.clear();
        destinationStartCityField.clear();
        destinationCheckpointListView.getItems().clear();
        destManager = null;
        destTruck = null;
        destDriver = null;
        destinationSelectedDriverLabel.setText(" ");
        destinationSelectedManagerLabel.setText(" ");
        destinationSelectedTruckLabel.setText(" ");
        destinationManagementArriDate.setValue(null);
        destinationManagementDepDate.setValue(null);
    }

    private boolean ensureFieldIntegerTruck() {
        try {
            Integer.parseInt(yearField.getText());
            Double.parseDouble(odometerField.getText());
            Double.parseDouble(truckCapacityField.getText());
        } catch (NumberFormatException n) {
            return false;
        }

        return true;
    }

    private boolean ensureFieldIntegerCargo() {
        try {
            Double.parseDouble(weightCargoField.getText());
        } catch (NumberFormatException n) {
            return false;
        }

        return true;
    }

    private boolean ensureAllFieldCargo() {
        return titleCargoField.getText() != null && !titleCargoField.getText().trim().isEmpty() && weightCargoField.getText() != null && !weightCargoField.getText().trim().isEmpty() && descriptionCargoField.getText() != null && !descriptionCargoField.getText().trim().isEmpty();
    }

    public boolean ensureAllFieldTrucks() {
        return makeField.getText() != null && !makeField.getText().trim().isEmpty() && modelField.getText() != null && !modelField.getText().trim().isEmpty() && yearField.getText() != null && !yearField.getText().trim().isEmpty() && odometerField.getText() != null && !odometerField.getText().trim().isEmpty() && truckCapacityField.getText() != null && !truckCapacityField.getText().trim().isEmpty();
    }

    public boolean ensureAllFieldDestination() {
        return destinationEndCityField.getText() != null && !destinationEndCityField.getText().trim().isEmpty() && destinationStartCityField.getText() != null && !destinationStartCityField.getText().trim().isEmpty() && destManager != null && destinationManagementArriDate.getValue() != null && destinationManagementDepDate.getValue() != null;
    }

    public boolean ensureDateOrder() {
        return destinationManagementArriDate.getValue().isAfter(destinationManagementDepDate.getValue());
    }

    public void logOut() {
        GlobalUtils.logOut(loggedUser, logOutButton);
    }

    public void closedCargoManagementTab() {
        clearAllCargoFields();
    }

    public void closedTruckManagementTab() {
        clearAllTrucksFields();
    }

    public void closedDestinationManagementTab() {
        clearAllDestinationFields();
    }

    public void showHelpCargo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("When creating a cargo, you have the option to directly assign it to a truck. To do this, first create the cargo, then select the cargo and the desired truck from their respective lists. Finally, click the 'Assign Truck' button. If you decide not to assign a cargo to a truck, simply select the cargo and click the 'Unassign Truck' button.");

        alert.showAndWait();
    }

    public void showDestinationHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("To create a destination, make sure you have at least one truck created. Then, fill in all the fields with the destination's information. If you want to add a checkpoint to the destination, simply click the 'Add Checkpoint' button and fill in the fields accordingly");

        alert.showAndWait();
    }

    public void assignCargoDestination() {
        if (cargoManagementCargoListView.getItems().size() == 0 || cargoManagementDestinationListView.getItems().size() == 0) {
            createError("Input error", "No cargo nor destination has been created");
            return;
        }

        if (cargoManagementDestinationListView.getSelectionModel().getSelectedItem() == null || cargoManagementCargoListView.getSelectionModel().getSelectedItem() == null) {
            createError("Input error", "No cargo nor destination selected");
            return;
        }

        Cargo currentCargo = userHib.getObjectById(Cargo.class, cargoManagementCargoListView.getSelectionModel().getSelectedItem().getId());
        if (currentCargo.getAssignedDestination() != null) {
            createError("Input error", "You can't assign a cargo to 2 different destinations. \nFirst, unassign the destination and then assign the new one");
        }
        Destination currentDestination = userHib.getObjectById(Destination.class, cargoManagementDestinationListView.getSelectionModel().getSelectedItem().getId());

        currentCargo.setAssignedDestination(currentDestination);
        userHib.updateObject(currentCargo);
        currentDestinationCargoLabel.setText(currentDestination.toString());
    }

    public void removeCargoDestination() {
        if (cargoManagementCargoListView.getItems().size() == 0) {
            createError("Input error", "No cargo nor destination has been created");
            return;
        }

        if (cargoManagementDestinationListView.getSelectionModel().getSelectedItem() == null || cargoManagementCargoListView.getSelectionModel().getSelectedItem() == null) {
            createError("Input error", "No cargo nor destination selected");
            return;
        }

        Cargo currentCargo = userHib.getObjectById(Cargo.class, cargoManagementCargoListView.getSelectionModel().getSelectedItem().getId());
        if (currentCargo.getAssignedDestination() == null)
            return;

        Destination currentDestination = userHib.getObjectById(Destination.class, currentCargo.getAssignedDestination().getId());

        currentDestination.getCargoList().remove(currentCargo);
        currentCargo.setAssignedDestination(null);

        userHib.updateObject(currentCargo);
        userHib.updateObject(currentDestination);
        currentDestinationCargoLabel.setText(" ");
    }

    private void editableCols() {
        try {
            managerTableLogin.setCellFactory(TextFieldTableCell.forTableColumn());
            managerTableLogin.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setLogin(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setLogin(e.getNewValue());
                managerTableView.refresh();
            });

            managerTableName.setCellFactory(TextFieldTableCell.forTableColumn());
            managerTableName.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setName(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
                managerTableView.refresh();
            });

            managerTableSurname.setCellFactory(TextFieldTableCell.forTableColumn());
            managerTableSurname.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setSurname(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setSurname(e.getNewValue());
                managerTableView.refresh();
            });

            managerTableBirthDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
            managerTableBirthDate.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setBirthDate(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setBirthDate(e.getNewValue());
                managerTableView.refresh();
            });

            managerTablePhoneNum.setCellFactory(TextFieldTableCell.forTableColumn());
            managerTablePhoneNum.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setPhoneNum(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setPhoneNum(e.getNewValue());
                managerTableView.refresh();
            });

            managerTableEmail.setCellFactory(TextFieldTableCell.forTableColumn());
            managerTableEmail.setOnEditCommit(e -> {
                Manager manager = userHib.getObjectById(Manager.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                manager.setEmail(e.getNewValue());
                userHib.updateObject(manager);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setEmail(e.getNewValue());
                managerTableView.refresh();
            });

            managerTableIsAdmin.setCellFactory(CheckBoxTableCell.forTableColumn(managerTableIsAdmin));

            driverTableLogin.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTableLogin.setOnEditCommit(e -> {
                Driver driver = userHib.getObjectById(Driver.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                driver.setLogin(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setLogin(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableName.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTableName.setOnEditCommit(e -> {
                Driver driver = userHib.getObjectById(Driver.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                driver.setName(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableSurname.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTableSurname.setOnEditCommit(e -> {
                Driver driver = userHib.getObjectById(Driver.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                driver.setSurname(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setSurname(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableBirthDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
            driverTableBirthDate.setOnEditCommit(e -> {
                Driver driver = userHib.getObjectById(Driver.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                driver.setBirthDate(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setBirthDate(e.getNewValue());
                driverTableView.refresh();
            });

            driverTablePhoneNum.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTablePhoneNum.setOnEditCommit(e -> {
                Driver driver = userHib.getObjectById(Driver.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
                driver.setPhoneNum(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setPhoneNum(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableMedDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
            driverTableMedDate.setOnEditCommit(e -> {
                Driver driver = e.getTableView().getItems().get(e.getTablePosition().getRow());
                driver.setMedCertificateDate(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setMedCertificateDate(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableMedNum.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTableMedNum.setOnEditCommit(e -> {
                Driver driver = e.getTableView().getItems().get(e.getTablePosition().getRow());
                driver.setMedCertificateNumber(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setMedCertificateNumber(e.getNewValue());
                driverTableView.refresh();
            });

            driverTableLicense.setCellFactory(TextFieldTableCell.forTableColumn());
            driverTableLicense.setOnEditCommit(e -> {
                Driver driver = e.getTableView().getItems().get(e.getTablePosition().getRow());
                driver.setDriverLicense(e.getNewValue());
                userHib.updateObject(driver);
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setDriverLicense(e.getNewValue());
                driverTableView.refresh();
            });

            managerTableView.setEditable(true);
            driverTableView.setEditable(true);
        } catch (Exception e) {
            GlobalUtils.createError("Error", "Error while editing table, enter a valid value");
        }
    }

    public void openForum() {
        Scene currentScene = forumButton.getScene();
        Stage stage = (Stage) forumButton.getScene().getWindow();
        GlobalUtils.openForum(loggedUser, currentScene, stage, userHib);
    }

    public void onSortDestinationClicked() {
        List<Destination> destinationList = userHib.getAllRecords(Destination.class);

        switch (destinationComboBox.getSelectionModel().getSelectedItem()) {
            case "Date" -> destinationList.sort(Comparator.comparing(Destination::getDepartureDate));
            case "Start city" -> destinationList.sort(Comparator.comparing(Destination::getStartCity));
            case "End city" -> destinationList.sort(Comparator.comparing(Destination::getEndCity));
            default -> System.out.println("Error");
        }

        destinationListView.setItems(FXCollections.observableArrayList(destinationList));
    }
}
