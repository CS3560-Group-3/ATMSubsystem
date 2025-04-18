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

    private int selectedAccount = -1;

    @FXML
    private Label insertAmountWithdrawLabel;

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private Button withdrawButton;

    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    private void showWithdrawSuccess() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Withdraw Successful");
        alert.setHeaderText("Withdraw Successful");
        alert.setContentText("The specified amount of money was withdrawn from your account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showInsufficientBalance() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Insufficient Balance");
        alert.setHeaderText("Insufficient Balance");
        alert.setContentText("You do not have a sufficient account balance to fund that transaction.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showWithdrawFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Withdraw Failed");
        alert.setHeaderText("Withdraw Failed");
        alert.setContentText("Something went wrong when withdrawing your money. Please try again.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    public void initialize() {
        ObservableList<AccountOption> accounts = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
            String query = String.format("SELECT * FROM accounts WHERE customerId=%d", App.sessionCustomerId.get());
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    accounts.add(new AccountOption(rs.getInt("accountId"), String.format("Account #%s (%s)", rs.getString("accountNumber"), App.capitalizeFirst(rs.getString("type")))));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        accountSelectDropdown.setItems(accounts);
        accountSelectDropdown.setOnAction(event -> {
            selectedAccount = accountSelectDropdown.getValue().getId();
            insertAmountWithdrawLabel.setVisible(true);
            withdrawAmountTextField.setVisible(true);
            withdrawAmountTextField.clear();
            withdrawButton.setVisible(true);
        });
    }

    @FXML
    private void withdraw() throws IOException {
        String input = withdrawAmountTextField.getText();

        try {
            double withdrawAmount = Double.parseDouble(input);
            double currentBalance = -1.0;

            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("SELECT balance FROM accounts WHERE accountId=%d", selectedAccount);
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        currentBalance = rs.getDouble("balance");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(currentBalance == -1 || withdrawAmount > currentBalance) {
                withdrawAmountTextField.clear();
                showInsufficientBalance();
            } else {
                try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                    String query = String.format("UPDATE accounts SET balance = balance - %f WHERE accountId = %d", withdrawAmount, selectedAccount);
                    try (Statement stmt = conn.createStatement()) {
                        int affected = stmt.executeUpdate(query);
                        if(affected > 0) {
                            showWithdrawSuccess();
                            App.setRoot("secondary");
                        }
        
                    }
                } catch (SQLException e) {
                    showWithdrawFail();
                    e.printStackTrace();
                }
            }

        } catch (NumberFormatException e){
            insertAmountWithdrawLabel.setText("Invalid amount!");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

}
