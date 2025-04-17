package atm_sub_system;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import java.sql.*;


public class startScreen {

    private String url = "jdbc:mysql://localhost:3306/atmsubsystem";
    private String user = "root"; 
    private String password = "root"; 

    @FXML
    private TextField accountNumberInput;

    @FXML
    private PasswordField pinInput;

    private void showNotFoundAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Log On Failed");
        alert.setContentText("Account number and pin combination not found. Please try again.");
        alert.showAndWait();
    }

    @FXML
    private void logOn() throws IOException {

        String accountNumber = accountNumberInput.getText();
        String pinCode = pinInput.getText();

        if(accountNumber == "" || pinCode == "") {
            showNotFoundAlert();
        } else {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String query = String.format("SELECT * FROM accounts JOIN customers ON (accounts.customerId = customers.customerId) WHERE accountNumber=%d AND pinCode=%d", Integer.parseInt(accountNumber), Integer.parseInt(pinCode));
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
    
                    int resultCount = 0;
                    while (rs.next()) {
                        App.sessionAccountId.set(rs.getInt("accountId"));
                        App.sessionCustomerId.set(rs.getInt("customerId"));
                        App.setRoot("secondary");
                        resultCount++;
                    }
    
                    if(resultCount == 0)
                        showNotFoundAlert();
    
                }
            } catch (SQLException e) {
                showNotFoundAlert();
                e.printStackTrace();
            }
        }

    }

}
