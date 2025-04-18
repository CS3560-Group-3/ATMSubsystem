package atm_sub_system;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class accountBalanceScreenController {

    @FXML
    private VBox accountsList;

    public void initialize() throws IOException {
        // Connect to DB and fetch list of customer accounts on page load
        try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
            String query = String.format("SELECT * FROM accounts WHERE customerId=%d", App.sessionCustomerId.get());
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    // Define an Account Balance Item to store and display accout info on main page
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("accountBalanceItem.fxml"));
                    VBox itemBox = loader.load();

                    // Set the controller
                    accountBalanceItemController controller = loader.getController();

                    // Set the header text to display the Account Number and Account Type
                    controller.setItemInfoHeader(String.format("%s Account - #%s", App.capitalizeFirst(rs.getString("type")), rs.getString("accountNumber")));
                    // Set the balance to display current balance value
                    controller.setItemBalance(rs.getDouble("balance"));

                    // Add Account Balance Item to page display
                    accountsList.getChildren().add(itemBox);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        // Redirect user to menu page
        App.setRoot("secondary");
    }
    
}
