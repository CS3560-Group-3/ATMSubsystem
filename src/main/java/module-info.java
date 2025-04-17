module atm_sub_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens atm_sub_system to javafx.fxml;
    exports atm_sub_system;
}
