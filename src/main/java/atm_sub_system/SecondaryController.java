package atm_sub_system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SecondaryController {

    @FXML
    private Label welcomeLabel;

    public void initialize() throws IOException {
        // Validate user is authenticated
        if(App.sessionCustomerId.get() == -1) {
            App.setRoot("startScreen");
        } else {
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("SELECT * FROM customers WHERE customerId=%d", App.sessionCustomerId.get());
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
    
                    while (rs.next()) {
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
        App.sessionCustomerId.set(-1);
        App.setRoot("startScreen");
    }

    @FXML
    private void switchToAccountBalanceScreen() throws IOException {
        App.setRoot("accountBalanceScreen");
    }

    @FXML
    private void switchToDepositScreen() throws IOException {
        App.setRoot("depositScreen");
    }

    @FXML
    private void switchToWithdrawlScreen() throws IOException {
        App.setRoot("withdrawScreen");
    }
    @FXML
    private void switchToTransferFundsScreen() throws IOException {
        App.setRoot("transferFundsScreen");
    }
    
    

}