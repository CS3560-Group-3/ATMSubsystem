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

public class withdrawScreenController {

    // Define variable to store the account which the user select to deposit into
    private int selectedAccount = -1;

    // Define FXML elements to update values / visiblity / fetch values later on
    @FXML
    private Label insertAmountWithdrawLabel;

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private Button withdrawButton;

    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    // Display withdraw success alert to user
    private void showWithdrawSuccess() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Withdraw Successful");
        alert.setHeaderText("Withdraw Successful");
        alert.setContentText("The specified amount of money was withdrawn from your account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    // Display insufficient balance alert to user
    private void showInsufficientBalance() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Insufficient Balance");
        alert.setHeaderText("Insufficient Balance");
        alert.setContentText("You do not have a sufficient account balance to fund that transaction.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    // Display withdraw fail alert to user
    private void showWithdrawFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Withdraw Failed");
        alert.setHeaderText("Withdraw Failed");
        alert.setContentText("Something went wrong when withdrawing your money. Please try again.");
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
            insertAmountWithdrawLabel.setVisible(true);
            withdrawAmountTextField.setVisible(true);
            withdrawAmountTextField.clear();
            withdrawButton.setVisible(true);
        });
    }

    @FXML
    private void withdraw() throws IOException {
        // Get input value from withdraw amount text field
        String input = withdrawAmountTextField.getText();

        try {
            // Validate input is a double
            double withdrawAmount = Double.parseDouble(input);
            // Define variable to store the current account balance
            double currentBalance = -1.0;

            // Connect to DB and fetch the account balance for the selected account
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("SELECT balance FROM accounts WHERE accountId=%d", selectedAccount);
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        // Set the current balance variable to the current balance of the selected account
                        currentBalance = rs.getDouble("balance");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Validate that the selected account has sufficent balance to fund the transaction
            if(currentBalance == -1 || withdrawAmount > currentBalance) {
                // If false, clear the input field and display error alert to user
                withdrawAmountTextField.clear();
                showInsufficientBalance();
            } else {
                // If true, withdraw the money from the selected account

                // Connect to DB and withdraw money from the selected account
                try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                    String query = String.format("UPDATE accounts SET balance = balance - %f WHERE accountId = %d", withdrawAmount, selectedAccount);
                    // Perform update and track number of rows affected
                    try (Statement stmt = conn.createStatement()) {
                        int affected = stmt.executeUpdate(query);
                        // If withdraw successful, display success alert and redirect to menu
                        if(affected > 0) {
                            showWithdrawSuccess();
                            App.setRoot("secondary");
                        }
        
                    }
                } catch (SQLException e) {
                    // Display withdraw failed alert
                    showWithdrawFail();
                    e.printStackTrace();
                }
            }

        } catch (NumberFormatException e){
            // Display invalid amount alert to user
            insertAmountWithdrawLabel.setText("Invalid amount!");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        // Redirect user home
        App.setRoot("secondary");
    }

}
