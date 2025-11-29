// MapCycleBuilder
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final ObservableList<String> availableMaps = FXCollections.observableArrayList();
    private final ObservableList<String> availableGametypes = FXCollections.observableArrayList();
    private final ObservableList<MapEntry> rotationEntries = FXCollections.observableArrayList();
    @FXML
    private ListView<String> availableMapsList;
    @FXML
    private ListView<String> availableGametypesList;
    @FXML
    private ListView<MapEntry> rotationList;
    @FXML
    private TextField mapsDirectoryField;
    @FXML
    private TextField gametypesDirectoryField;
    @FXML
    private TextField serverDirectoryField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Spinner<Integer> minPlayersSpinner;
    @FXML
    private Spinner<Integer> maxPlayersSpinner;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSpinners();
        setupDragAndDrop();
        availableMapsList.setItems(availableMaps);
        availableGametypesList.setItems(availableGametypes);
        rotationList.setItems(rotationEntries);
        rotationList.setCellFactory(param -> new MapEntryCell());
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void setupSpinners() {
        minPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32, 0));
        maxPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 32, 16));
    }

    private void setupDragAndDrop() {
        availableMapsList.setOnDragDetected(this::handleMapDragDetected);
        availableGametypesList.setOnDragDetected(this::handleGametypeDragDetected);
        rotationList.setOnDragOver(this::handleRotationDragOver);
        rotationList.setOnDragDropped(this::handleRotationDragDropped);
    }

    private void handleMapDragDetected(MouseEvent event) {
        String selected = availableMapsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dragboard db = availableMapsList.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("MAP:" + selected);
            db.setContent(content);
        }
        event.consume();
    }

    private void handleGametypeDragDetected(MouseEvent event) {
        String selected = availableGametypesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dragboard db = availableGametypesList.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("GAMETYPE:" + selected);
            db.setContent(content);
        }
        event.consume();
    }

    private void handleRotationDragOver(DragEvent event) {
        if (event.getGestureSource() != rotationList && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleRotationDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            String data = db.getString();
            if (data.startsWith("MAP:")) {
                String mapName = data.substring(4);
                addMapToRotation(mapName);
                success = true;
            } else if (data.startsWith("GAMETYPE:")) {
                // TODO: Handle gametype drop
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void addMapToRotation(String mapName) {
        MapEntry entry = new MapEntry(mapName, "", "", 0, 16);
        rotationEntries.add(entry);
        updateStatus("Added " + mapName + " to rotation");
    }

    @FXML
    private void browseMapsDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Maps Directory");
        File selected = chooser.showDialog(null);
        if (selected != null) {
            mapsDirectoryField.setText(selected.getAbsolutePath());
            loadMaps(selected.getAbsolutePath());
        }
    }

    @FXML
    private void browseGametypesDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Gametypes Directory");
        File selected = chooser.showDialog(null);
        if (selected != null) {
            gametypesDirectoryField.setText(selected.getAbsolutePath());
            loadGametypes(selected.getAbsolutePath());
        }
    }

    @FXML
    private void browseServerDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Server Directory");
        File selected = chooser.showDialog(null);
        if (selected != null) {
            serverDirectoryField.setText(selected.getAbsolutePath());
        }
    }

    private void loadMaps(String directoryPath) {
        availableMaps.clear();
        File mapsDir = new File(directoryPath);
        File[] mapFiles = mapsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".map"));

        if (mapFiles != null) {
            for (File mapFile : mapFiles) {
                String name = mapFile.getName().replace(".map", "");
                availableMaps.add(name);
            }
        }
        updateStatus("Loaded " + availableMaps.size() + " maps");
    }

    private void loadGametypes(String directoryPath) {
        availableGametypes.clear();
        File gametypesDir = new File(directoryPath);
        File[] gametypeDirs = gametypesDir.listFiles(File::isDirectory);

        if (gametypeDirs != null) {
            for (File gametypeDir : gametypeDirs) {
                availableGametypes.add(gametypeDir.getName());
            }
        }
        updateStatus("Loaded " + availableGametypes.size() + " gametypes");
    }

    @FXML
    private void addToRotation() {
        String selectedMap = availableMapsList.getSelectionModel().getSelectedItem();
        String selectedGametype = availableGametypesList.getSelectionModel().getSelectedItem();

        if (selectedMap != null) {
            MapEntry entry = new MapEntry(
                    selectedMap,
                    selectedGametype != null ? selectedGametype : "",
                    descriptionField.getText(),
                    minPlayersSpinner.getValue(),
                    maxPlayersSpinner.getValue()
            );
            rotationEntries.add(entry);
            updateStatus("Added " + selectedMap + " to rotation");
        }
    }

    @FXML
    private void removeFromRotation() {
        MapEntry selected = rotationList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rotationEntries.remove(selected);
            updateStatus("Removed " + selected.mapName() + " from rotation");
        }
    }

    @FXML
    private void moveUp() {
        int selectedIndex = rotationList.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            Collections.swap(rotationEntries, selectedIndex, selectedIndex - 1);
            rotationList.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    private void moveDown() {
        int selectedIndex = rotationList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < rotationEntries.size() - 1 && selectedIndex != -1) {
            Collections.swap(rotationEntries, selectedIndex, selectedIndex + 1);
            rotationList.getSelectionModel().select(selectedIndex + 1);
        }
    }

    @FXML
    private void validateSettings() {
        List<String> errors = new ArrayList<>();

        if (rotationEntries.isEmpty()) {
            errors.add("Rotation list is empty");
        }

        for (int i = 0; i < rotationEntries.size(); i++) {
            MapEntry entry = rotationEntries.get(i);
            if (entry.gametype().isEmpty()) {
                errors.add("Entry " + (i + 1) + " (" + entry.mapName() + ") has no gametype");
            }
            if (entry.minPlayers() > entry.maxPlayers()) {
                errors.add("Entry " + (i + 1) + " has min players greater than max players");
            }
        }

        if (serverDirectoryField.getText().isEmpty()) {
            errors.add("Server directory not set");
        }

        if (errors.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Validation Successful", "All settings are valid!");
        } else {
            String errorMessage = String.join("\n", errors);
            showAlert(Alert.AlertType.ERROR, "Validation Failed", errorMessage);
        }
    }

    @FXML
    private void generateFiles() {
        if (rotationEntries.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Entries", "Rotation list is empty");
            return;
        }

        if (serverDirectoryField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No Server Directory", "Please set the server directory");
            return;
        }

        try {
            generateMapCycle();
            generateMapVotes();
            updateStatus("Files generated successfully!");
            showAlert(Alert.AlertType.INFORMATION, "Success", "mapcycle.txt and mapvotes.txt generated successfully!");
        } catch (IOException e) {
            updateStatus("Error generating files: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Generation Failed", e.getMessage());
        }
    }

    private void generateMapCycle() throws IOException {
        Path outputPath = Paths.get(serverDirectoryField.getText(), "mapcycle.txt");
        List<String> lines = new ArrayList<>();

        for (MapEntry entry : rotationEntries) {
            String line = String.format("%s:%s:%d:%d",
                    entry.mapName(),
                    entry.gametype(),
                    entry.minPlayers(),
                    entry.maxPlayers()
            );
            lines.add(line);
        }

        Files.write(outputPath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void generateMapVotes() throws IOException {
        Path outputPath = Paths.get(serverDirectoryField.getText(), "mapvotes.txt");
        List<String> lines = new ArrayList<>();

        for (MapEntry entry : rotationEntries) {
            String line = String.format("%s:%s:%s",
                    entry.mapName(),
                    entry.gametype(),
                    entry.description()
            );
            lines.add(line);
        }

        // Write with UTF-16 LE BOM and Windows line endings
        try (OutputStream os = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_16LE);
             BufferedWriter writer = new BufferedWriter(osw)) {

            // Write BOM
            os.write(0xFF);
            os.write(0xFE);

            for (String line : lines) {
                writer.write(line);
                writer.write("\r\n");
            }
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Cell for displaying MapEntry in the rotation list
    private static class MapEntryCell extends ListCell<MapEntry> {
        @Override
        protected void updateItem(MapEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox(10);
                Label mapLabel = new Label(item.mapName());
                Label gametypeLabel = new Label("[" + item.gametype() + "]");
                Label descLabel = new Label(item.description());
                Label playersLabel = new Label("(" + item.minPlayers() + "-" + item.maxPlayers() + " players)");

                mapLabel.setStyle("-fx-font-weight: bold;");
                gametypeLabel.setStyle("-fx-text-fill: #0066cc;");
                playersLabel.setStyle("-fx-text-fill: #666666;");

                hbox.getChildren().addAll(mapLabel, gametypeLabel, descLabel, playersLabel);
                setGraphic(hbox);
            }
        }
    }

    public record MapEntry(String mapName, String gametype, String description, int minPlayers, int maxPlayers) {

    }
}