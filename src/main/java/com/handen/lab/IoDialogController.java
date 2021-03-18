package com.handen.lab;

import com.handen.lab.data.Employee;
import com.handen.lab.model.Repository;
import com.handen.lab.model.RepositoryProxy;
import com.handen.lab.model.writers.BinaryEmployeesMapper;
import com.handen.lab.model.writers.CsvEmployeesMapper;
import com.handen.lab.model.writers.EmployeesMapper;
import com.handen.lab.model.writers.XmlEmployeesMapper;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class IoDialogController implements Initializable {
    public Button binary_button;
    public Button xml_button;
    public Button csv_button;
    public Label title;
    private final ObservableList<String> plugins = FXCollections.observableArrayList("None");
    public ComboBox<String> plugins_combobox;
    public Label xml_plugins_label;
    private Stage stage;
    private final Repository repository = RepositoryProxy.getInstance();
    private IOMode ioMode;

    public void setIoMode(IOMode ioMode) {
        this.ioMode = ioMode;
        if(ioMode.equals(IOMode.LOAD)) {
            title.setText("LOAD");
        }
        else {
            title.setText("SAVE");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        plugins.addAll(fetchPlugins());
        plugins_combobox.setItems(plugins);
        plugins_combobox.setValue(plugins.get(0));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onBinaryClicked(MouseEvent mouseEvent) {
        EmployeesMapper mapper = new BinaryEmployeesMapper();
        File file = chooseFile("Choose txt file", mapper.getFileExtension());
        if(ioMode == IOMode.SAVE) {
            repository.saveToFile(mapper, file);
        }
        else {
            repository.loadFromFile(mapper, file);
        }
    }

    public void onXmlClicked(MouseEvent mouseEvent) {
        String pluginPath = "";
        if(!plugins_combobox.getValue().equals("None")) {
            pluginPath = "C:\\oop\\" + plugins_combobox.getValue();
        }

        EmployeesMapper mapper = new XmlEmployeesMapper(pluginPath);
        File file = chooseFile("Choose xml file", mapper.getFileExtension());
        if(ioMode == IOMode.SAVE) {
            repository.saveToFile(mapper, file);
        }
        else {
            repository.loadFromFile(mapper, file);
        }
    }

    public void onCsvClicked(MouseEvent mouseEvent) {
        EmployeesMapper mapper = new CsvEmployeesMapper();
        File file = chooseFile("Choose csv file", mapper.getFileExtension());
        if(ioMode == IOMode.SAVE) {
            repository.saveToFile(mapper, file);
        }
        else {
            repository.loadFromFile(mapper, file);
        }
    }

    private File chooseFile(String title, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension, "*." + extension));
        return fileChooser.showOpenDialog(stage);
    }

    private List<String> fetchPlugins() {
        File file = new File("C:\\oop");
        if(file.listFiles() == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().toLowerCase(Locale.ROOT).contains(".jar");
            }
        })).map(file1 -> {
            return file1.getName();
        }).collect(Collectors.toList());
    }

    public enum IOMode {
        SAVE,
        LOAD
    }
}