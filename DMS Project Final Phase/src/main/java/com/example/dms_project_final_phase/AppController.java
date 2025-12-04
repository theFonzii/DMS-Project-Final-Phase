package com.example.dms_project_final_phase;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * JavaFX controller for the Runescape DMS UI.
 * Handles button clicks, validation, and calling DAO/service methods.
 */

public class AppController {

    // ---- Database / service dependencies ----
    private DatabaseManager dbManager;
    private ItemDao itemDao;
    private BackpackService backpackService;

    public void setDbManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public void setBackpackService(BackpackService backpackService) {
        this.backpackService = backpackService;
    }

    // ---- FXML controls ----

    @FXML
    private TextField dbPathField;

    @FXML
    private Button connectButton;

    @FXML
    private TableView<RunescapeItem> itemTable;

    @FXML
    private TableColumn<RunescapeItem, Number> idColumn;
    @FXML
    private TableColumn<RunescapeItem, String> nameColumn;
    @FXML
    private TableColumn<RunescapeItem, String> typeColumn;
    @FXML
    private TableColumn<RunescapeItem, Number> levelColumn;
    @FXML
    private TableColumn<RunescapeItem, Number> weightColumn;
    @FXML
    private TableColumn<RunescapeItem, Number> priceColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ItemType> typeCombo;
    @FXML
    private TextField levelField;
    @FXML
    private TextField weightField;
    @FXML
    private TextField priceField;

    @FXML
    private Label statusLabel;
    @FXML
    private Label backpackSummaryLabel;

    private final ObservableList<RunescapeItem> items =
            FXCollections.observableArrayList();

    /**
     * Called by JavaFX after FXML is loaded.
     * Wires up table columns & combo box.
     */
    @FXML
    private void initialize() {
        // table column bindings
        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()));
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        typeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().name()));
        levelColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getRequiredLevel()));
        weightColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getWeightKg()));
        priceColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getGePrice()));

        itemTable.setItems(items);

        typeCombo.setItems(FXCollections.observableArrayList(ItemType.values()));

        // when user clicks a row, populate form for Update/Delete
        itemTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> populateFormFromSelection(newItem)
        );
    }

    private void populateFormFromSelection(RunescapeItem item) {
        if (item == null) {
            idField.clear();
            nameField.clear();
            typeCombo.setValue(null);
            levelField.clear();
            weightField.clear();
            priceField.clear();
            return;
        }

        idField.setText(String.valueOf(item.getId()));
        nameField.setText(item.getName());
        typeCombo.setValue(item.getType());
        levelField.setText(String.valueOf(item.getRequiredLevel()));
        weightField.setText(String.valueOf(item.getWeightKg()));
        priceField.setText(String.valueOf(item.getGePrice()));
    }

    // ---- Button handlers ----

    @FXML
    private void onConnectClicked() {
        String path = dbPathField.getText().trim();
        if (path.isEmpty()) {
            showError("Database path is required.");
            return;
        }
        try {
            dbManager.connect(path);
            statusLabel.setText("Connected to: " + path);
            refreshTable();
        } catch (DbException ex) {
            showException("Failed to connect to database.", ex);
        }
    }

    @FXML
    private void onRefreshClicked() {
        refreshTable();
    }

    @FXML
    private void onAddClicked() {
        try {
            RunescapeItem item = readItemFromForm();
            itemDao.insert(item);
            statusLabel.setText("Item added (ID " + item.getId() + ").");
            refreshTable();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        } catch (DbException ex) {
            showException("Database error while adding item.", ex);
        }
    }

    @FXML
    private void onUpdateClicked() {
        try {
            RunescapeItem item = readItemFromForm();
            itemDao.update(item);
            statusLabel.setText("Item updated (ID " + item.getId() + ").");
            refreshTable();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        } catch (DbException ex) {
            showException("Database error while updating item.", ex);
        }
    }

    @FXML
    private void onDeleteClicked() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            showError("Enter an ID or select a row to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            itemDao.deleteById(id);
            statusLabel.setText("Item deleted (ID " + id + ").");
            refreshTable();
        } catch (NumberFormatException ex) {
            showError("ID must be an integer.");
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        } catch (DbException ex) {
            showException("Database error while deleting item.", ex);
        }
    }

    @FXML
    private void onBackpackCalcClicked() {
        try {
            double totalWeight = backpackService.calculateTotalWeight();
            int totalValue = backpackService.calculateTotalValue();
            boolean over = backpackService.isOverWeight();
            double remaining = backpackService.getRemainingCapacity();

            String summary =
                    String.format("Weight: %.2f / %.2f kg (%s), Total GE: %,d, Remaining: %.2f kg",
                            totalWeight,
                            backpackService.getWeightLimitKg(),
                            over ? "OVERWEIGHT" : "OK",
                            totalValue,
                            remaining);

            backpackSummaryLabel.setText(summary);
        } catch (DbException ex) {
            showException("Failed to calculate backpack stats.", ex);
        }
    }

    // ---- Helpers ----

    private void refreshTable() {
        try {
            if (itemDao == null) {
                return; // not connected yet
            }
            items.setAll(itemDao.findAll());
        } catch (DbException ex) {
            showException("Failed to load items.", ex);
        }
    }

    private RunescapeItem readItemFromForm() throws ValidationException {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        ItemType type = typeCombo.getValue();
        String levelText = levelField.getText().trim();
        String weightText = weightField.getText().trim();
        String priceText = priceField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || type == null ||
                levelText.isEmpty() || weightText.isEmpty() || priceText.isEmpty()) {
            throw new ValidationException("All item fields are required.");
        }

        try {
            int id = Integer.parseInt(idText);
            int level = Integer.parseInt(levelText);
            double weight = Double.parseDouble(weightText);
            int price = Integer.parseInt(priceText);

            if (id <= 0 || level < 0 || weight < 0 || price < 0) {
                throw new ValidationException("Numeric fields must be non-negative; ID must be > 0.");
            }

            return new RunescapeItem(id, name, type, level, weight, price);
        } catch (NumberFormatException ex) {
            throw new ValidationException("ID, level, weight, and price must be numeric.");
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Input Error");
        alert.showAndWait();
    }

    private void showException(String userMsg, Exception ex) {
        statusLabel.setText(userMsg);
        Alert alert = new Alert(Alert.AlertType.ERROR, userMsg + "\n\n" + ex.getMessage(), ButtonType.OK);
        alert.setHeaderText("Database Error");
        alert.showAndWait();
    }
}
