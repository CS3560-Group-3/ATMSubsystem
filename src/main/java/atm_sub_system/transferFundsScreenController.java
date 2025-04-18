package atm_sub_system;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class transferFundsScreenController {

    // Define variable to store the account which the user select to deposit into
    private int selectedAccount = -1;
    
    // Define FXML elements to update values / visiblity / fetch values later on
    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    @FXML
    private Label destinationAccountLabel;

    @FXML
    private TextField destinationAccountTextField;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private Button transferButton;

    // Display invalid destination account number alert to user
    private void showInvalidDestination() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Destination Account");
        alert.setHeaderText("Invalid Destination Account");
        alert.setContentText("The destination account number you entered does not exist within our records.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    // Display transfer success alert to user
    private void showTransferSuccess() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Transfer Successful");
        alert.setHeaderText("Transfer Successful");
        alert.setContentText("Money has been successfully transferred from your selected account to the destination account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    // Display transfer fail alert to user
    private void showTransferFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Transfer Failed");
        alert.setHeaderText("Transfer Failed");
        alert.setContentText("Transfer failed. Please double check destination account and amount and try again.");
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
            destinationAccountLabel.setVisible(true);
            destinationAccountTextField.setVisible(true);
            destinationAccountTextField.clear();
            transferAmountTextField.setVisible(true);
            transferAmountTextField.clear();
            transferButton.setVisible(true);
        });
    }

    @FXML 
    private void transfer() throws IOException {
        // Get input value from destination account text field
        String destinationInput = destinationAccountTextField.getText();
        // Get input value from transfer amount text field
        String amountInput = transferAmountTextField.getText();

        // Validate inputs aren't empty
        if(destinationInput != "" && amountInput != "") {
            try {
                // Validate input is a double
                double transferAmount = Double.parseDouble(amountInput);
                // Define variable to store result count for account search
                int resultCount = 0;
                // Connect to DB and perform search for destination account
                try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                    String query = String.format("SELECT COUNT(*) FROM accounts WHERE accountNumber=%s", destinationInput);
                    try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {
                        while (rs.next()) {
                            // Set result count equal to result of COUNT(*) query
                            resultCount = rs.getInt(1);
                        }
                    }
                } catch (SQLException e) {
                    showTransferFail();
                    e.printStackTrace();
                }
                // Check if destination account exists
                if(resultCount == 0) {
                    // If not, display error alert
                    showInvalidDestination();
                } else {
                    // If destination account exists, process transfer transaction

                    // Define variables to store the amount of source & destination rows affected
                    int sourceAffected = 0;
                    int destinationAffected = 0;
                    
                    // Connect to DB and update source account to debit transfer amount from
                    try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                        String query = String.format("UPDATE accounts SET balance = balance - %f WHERE accountId = %d", transferAmount, selectedAccount);
                        try (Statement stmt = conn.createStatement()) {
                            // Store number of source rows affected
                            sourceAffected = stmt.executeUpdate(query);
                        }
                    } catch (SQLException e) {
                        // Display fail alert if SQL error occurs
                        showTransferFail();
                        e.printStackTrace();
                    }
                    
                    // Connect to DB and update destination account to credit transfer amount to
                    try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                        String query = String.format("UPDATE accounts SET balance = balance + %f WHERE accountNumber = %s", transferAmount, destinationInput);
                        try (Statement stmt = conn.createStatement()) {
                            // Store number of destination rows affected
                            destinationAffected = stmt.executeUpdate(query);
                        }
                    } catch (SQLException e) {
                        // Display fail alert if SQL error occurs
                        showTransferFail();
                        e.printStackTrace();
                    }
    
                    // Check if transfer successful by validating money was debited from source and credited to destination
                    if(sourceAffected > 0 && destinationAffected > 0)  {
                        showTransferSuccess();
                        App.setRoot("secondary");
                    }
    
                }
    
            } catch (NumberFormatException e){
                // Alert user that amount input is invalid
                destinationAccountLabel.setText("Invalid amount!");
            }
        }
     }

    @FXML
    private void switchToSecondary() throws IOException {
        // Redirect user to meun screen
        App.setRoot("secondary");
    }
}
