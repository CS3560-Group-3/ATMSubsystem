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
    private TextField cardNumberInput;

    @FXML
    private PasswordField pinInput;

    private void showNotFoundAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Log On Failed");
        alert.setHeaderText("Log On Failed");
        alert.setContentText("Card number and PIN combination not found in our records. Please try again.");
        alert.showAndWait();
    }

    @FXML
    private void logOn() throws IOException {

        String cardNumber = cardNumberInput.getText();
        String pinCode = pinInput.getText();

        if(cardNumber == "" || pinCode == "") {
            showNotFoundAlert();
        } else {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String query = String.format("SELECT * FROM debitcards JOIN customers ON (debitcards.customerId = customers.customerId) WHERE cardNumber=%s AND pinCode=%d", cardNumber, Integer.parseInt(pinCode));
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
    
                    int resultCount = 0;
                    while (rs.next()) {
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
