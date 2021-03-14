module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.module.jaxb;

    opens com.handen.lab to javafx.fxml;
    opens com.handen.lab.data.developer to javafx.base;
    exports com.handen.lab.data.developer;
    exports com.handen.lab.data.managers;
    exports com.handen.lab.data.designer;
    exports com.handen.lab;
    exports com.handen.lab.controller;
    exports com.handen.lab.data;
    exports com.handen.lab.model;
    exports com.handen.lab.utils;
    exports com.handen.lab.view;
    opens com.handen.lab.controller to javafx.fxml;
    opens com.handen.lab.model.writers to com.fasterxml.jackson.databind;
}