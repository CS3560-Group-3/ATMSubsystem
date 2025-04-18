package atm_sub_system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class secondaryController {

    // Define FXML elements to update values / visiblity / fetch values later on
    @FXML
    private Label welcomeLabel;

    public void initialize() throws IOException {
        // Validate user is authenticated
        if(App.sessionCustomerId.get() == -1) {
            App.setRoot("startScreen");
        } else {
            // Connect to DB and fetch customer information for current session customer
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("SELECT * FROM customers WHERE customerId=%d", App.sessionCustomerId.get());
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                        
                    while (rs.next()) {
                        // Set welcome label to welcome the first and last name of the user
                        welcomeLabel.textProperty().set(String.format("Welcome, %s %s!", rs.getString("firstName"), rs.getString("lastName")));
                    }
    
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void logOut() throws IOException {
        // Reset session customer ID and redirect to log on page
        App.sessionCustomerId.set(-1);
        App.setRoot("startScreen");
    }

    @FXML
    private void switchToAccountBalanceScreen() throws IOException {
        // Redirect to account balance page
        App.setRoot("accountBalanceScreen");
    }

    @FXML
    private void switchToDepositScreen() throws IOException {
        // Redirect to deposit page
        App.setRoot("depositScreen");
    }

    @FXML
    private void switchToWithdrawlScreen() throws IOException {
        // Redirect to withdraw page
        App.setRoot("withdrawScreen");
    }
    @FXML
    private void switchToTransferFundsScreen() throws IOException {
        // Redirect to transfer page
        App.setRoot("transferFundsScreen");
    }
    
    

}
