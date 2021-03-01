package com.handen.lab.controller;

import com.handen.lab.App;
import com.handen.lab.data.Employee;
import com.handen.lab.data.Record;
import com.handen.lab.data.developer.Developer;
import com.handen.lab.data.developer.MobileDeveloper;
import com.handen.lab.model.Repository;
import com.handen.lab.utils.LettersTextFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

    public AnchorPane container;
    private Stage stage;

    public Label errorLabel;
    private ObservableList<Employee> items = FXCollections.observableArrayList(new MobileDeveloper(0, "Ivan", "Pletinskiy", 100500));

    public TableView<Employee> table;

    public TextArea name_text_area;
    public TextArea surname_text_area;
    public TextArea salary_text_area;
    public TextArea mentor_text_area;
    public Label mentorLabel;

    public Button add_button;
    public Button save_button;
    public Button delete_button;
    public MenuItem menu_load;
    public MenuItem menu_save;
    public MenuItem menu_help;
    public MenuItem menu_about;

    private int selectedRow;

    private Repository repository;

    private RecordChangeListener recordChangedListener = () -> {
        //TODO
//        Record item = items.get(selectedRow);
//        boolean isChanged = !surname_text_area.textProperty().getValue().equals(item.getSurname()) ||
//                !year_text_area.textProperty().getValue().equals(item.getYearString()) ||
//                !phone_text_area.textProperty().getValue().equals(item.getPhone());
//
//        save_button.setVisible(isChanged);
    };

    @FXML
    public void OnAddButtonClick(ActionEvent actionEvent) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("add_dialog.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add record");
            stage.setScene(new Scene(root, 450, 300));
            AddDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setListener(employee -> {
                if(employee != null) {
                    addItem(employee);
                }
            });
            stage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void addItem(Employee employee) {
        items.add(employee);
    }

    public void OnSaveButtonClick(ActionEvent actionEvent) {
        //TODO
//        boolean isValid = validate();
//        if(isValid) {
//            String surname = surname_text_area.textProperty().getValue();
//            String year = year_text_area.textProperty().getValue();
//            String phone = phone_text_area.textProperty().getValue();
//            Record record = new Record(surname, phone, Short.parseShort(year));
//            items.set(selectedRow, record);
//        }
    }

    private boolean validate() {
        boolean isValid = true;

        if(surname_text_area.textProperty().getValue().equals("")) {
            isValid = false;
            showError("Surname field cannot be empty.");
        }

        return isValid;
    }

    private void showError(String errorText) {
        errorLabel.setText(errorText);
        errorLabel.setVisible(true);
    }

    public void OnDeleteButtonClick(ActionEvent actionEvent) {
        if(!items.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure want to delete this record?");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                items.remove(selectedRow);
            }
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindTextFormatters();
        bindTextListeners();
        initializeTable();

        repository = new Repository();
    }

    private void initializeTable() {
        table.setEditable(false);
        table.setItems(items);
        table.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int newSelectedId = (int) newValue;
            mentorLabel.setVisible(false);
            mentor_text_area.setVisible(false);
            if((int) newValue != -1) {
                Employee employee = items.get(newSelectedId);
                updateTextAreas(employee);
                selectedRow = newSelectedId;
                save_button.setVisible(false);
                errorLabel.setVisible(false);
            }
            else {
                surname_text_area.setText("");
            }
        });

        table.getSelectionModel().clearAndSelect(0);
    }

    private void updateTextAreas(Employee employee) {
        name_text_area.setText(employee.getName());
        surname_text_area.setText(employee.getSurname());
        salary_text_area.setText(String.valueOf(employee.getSalary()));
        if(employee instanceof Developer) {
            mentorLabel.setVisible(true);
            mentor_text_area.setVisible(true);
            if(((Developer) employee).getMentor() == null) {
                mentor_text_area.setText("");
            } else {
                mentor_text_area.setText(((Developer) employee).getMentor().getSurname());
            }
        }
    }

    private void bindTextFormatters() {
        surname_text_area.setTextFormatter(new LettersTextFormatter());
    }

    private void bindTextListeners() {
        surname_text_area.textProperty().addListener((observable, oldValue, newValue) -> {
            recordChangedListener.onChanged();
        });
    }

    public void OnMenuLoadClicked(ActionEvent actionEvent) {
        File file = chooseFile("Open records file");
        if(file != null) {
            items.clear();
            items.addAll(repository.loadRecords(file));
            table.getSelectionModel().clearAndSelect(0);
        }
    }

    public void OnMenuSaveClicked(ActionEvent actionEvent) {
        File file = chooseDirecotory("Save records to directory");
        if(file != null) {
            repository.saveRecordsInDirectory(file, items);
        }
    }

    private File chooseFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        return fileChooser.showOpenDialog(stage);
    }

    private File chooseDirecotory(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);

        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        directoryChooser.setInitialDirectory(userDirectory);
        return directoryChooser.showDialog(stage);
    }

    public void OnMenuHelpClicked(ActionEvent actionEvent) {
        showAlert("Help", helpText);
    }

    public void OnMenuAboutClicked(ActionEvent actionEvent) {
        showAlert("About", aboutText);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void focusNextTextArea(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
            event.consume();
            Node node = (Node) event.getSource();
            KeyEvent newEvent
                    = new KeyEvent(event.getSource(),
                    event.getTarget(), event.getEventType(),
                    event.getCharacter(), event.getText(),
                    event.getCode(), event.isShiftDown(),
                    true, event.isAltDown(),
                    event.isMetaDown());

            node.fireEvent(newEvent);
        }
    }

    interface RecordChangeListener {
        void onChanged();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private static final String helpText =
            "This program is used for creating, editing, deleting, saving and loading records. The record consists of: owner's surname, phone number, year.\n" +
            "Use TAB and SHIFT+TAB to navigate between input fields.\n" +
            "Click on table and use arrows UP and DOWN to navigate between records\n" +
            "Save button will appear after you change text in input fields\n" +
            "Add and edit records, then click File -> Save to save them into file.";

    private static final String aboutText =
            "Made by Ivan Pletinski 951008 2020";
}