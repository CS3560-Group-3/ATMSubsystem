package atm_sub_system;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import java.sql.*;


public class startScreen {

    // Define FXML elements to update values / visiblity / fetch values later on
    @FXML
    private TextField cardNumberInput;

    @FXML
    private PasswordField pinInput;

    // Show login info not found alert to user
    private void showNotFoundAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Log On Failed");
        alert.setHeaderText("Log On Failed");
        alert.setContentText("Card number and PIN combination not found in our records. Please try again.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    @FXML
    private void logOn() throws IOException {

        // Get input value from card number input text field
        String cardNumber = cardNumberInput.getText();
        // Get input value from PIN input text field
        String pinCode = pinInput.getText();

        // Validate fields aren't empty
        if(cardNumber == "" || pinCode == "") {
            showNotFoundAlert();
        } else {
            // Connect to DB and search for card number and PIN code combination
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("SELECT * FROM debitcards JOIN customers ON (debitcards.customerId = customers.customerId) WHERE cardNumber=%s AND pinCode=%d", cardNumber, Integer.parseInt(pinCode));
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                    
                    // Track how many results found
                    int resultCount = 0;
                    while (rs.next()) {
                        // If found, set session customer ID to the customer ID associated with the card number and PIN code
                        App.sessionCustomerId.set(rs.getInt("customerId"));
                        // Redirecet to menu page
                        App.setRoot("secondary");
                        resultCount++;
                    }
    
                    // If no results found, display alert to user
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
