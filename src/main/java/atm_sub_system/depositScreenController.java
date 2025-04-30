package atm_sub_system;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class depositScreenController {

    // Define variable to store the account which the user select to deposit into
    private int selectedAccount = -1;

    // Define FXML elements to update values / visiblity / fetch values later on
    @FXML
    private Label insertAmountDepositLabel;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private Button depositButton;

    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    // Display deposit success alert to user
    private void showDepositSuccess() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Deposit Successful");
        alert.setHeaderText("Deposit Successful");
        alert.setContentText("The specified amount of money was deposited into your account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    // Display deposit fail to user
    private void showDepositFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Deposit Failed");
        alert.setHeaderText("Deposit Failed");
        alert.setContentText("Something went wrong when depositing your money. Please try again.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    public void initialize() {
        // Connect to DB and fetch list of customer accounts on page load to populate account list dropdown
        ObservableList<AccountOption> accounts = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
            String query = String.format("SELECT * FROM accounts WHERE customerId=%d", App.sessionCustomerId.get());
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    // Add each account and it's ID to the account select dropdown
                    accounts.add(new AccountOption(rs.getInt("accountId"), String.format("Account #%s (%s)", rs.getString("accountNumber"), App.capitalizeFirst(rs.getString("type")))));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Set the account select dropdown items
        accountSelectDropdown.setItems(accounts);
        // Reigster callback function on account selection
        accountSelectDropdown.setOnAction(event -> {
            // Set selected account value
            selectedAccount = accountSelectDropdown.getValue().getId();
            // Reveal hidden entry fields & clear input values
            insertAmountDepositLabel.setVisible(true);
            depositAmountTextField.setVisible(true);
            depositAmountTextField.clear();
            depositButton.setVisible(true);
        });
    }

    @FXML
    private void deposit() throws IOException {
        // Get input value from deposit amount text field
        String input = depositAmountTextField.getText();

        try{
            // Verify input is a double
            double depositedAmount = Double.parseDouble(input);
            
            // Connecto to DB and deposit money into selected account
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("UPDATE accounts SET balance = balance + %f WHERE accountId = %d", depositedAmount, selectedAccount);
                // Execute statement and get number of affected rows
                try (Statement stmt = conn.createStatement()) {
                    int affected = stmt.executeUpdate(query);
                    // Verify that update was successful
                    if(affected > 0) {
                        showDepositSuccess();
                        App.setRoot("secondary");
                    }
    
                }
            } catch (SQLException e) {
                // Display failed deposit alert
                showDepositFail();
                e.printStackTrace();
            }
        }
        catch (NumberFormatException e){
            // Notify user that input is invalid
            insertAmountDepositLabel.setText("Invalid amount!");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        // Redirect user to menu screen
        App.setRoot("secondary");
    }

}
