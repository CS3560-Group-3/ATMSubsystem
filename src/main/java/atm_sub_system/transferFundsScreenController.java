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

    private int selectedAccount = -1;
    
    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    @FXML
    private Label destinationAccountLabel;

    @FXML
    private TextField destinationAccountTextField;

    @FXML
    private TextField transferAmountTextField;

    private void showInvalidDestination() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Destination Account");
        alert.setHeaderText("Invalid Destination Account");
        alert.setContentText("The destination account number you entered does not exist within our records.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showTransferSuccess() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Transfer Successful");
        alert.setHeaderText("Transfer Successful");
        alert.setContentText("Money has been successfully transferred from your selected account to the destination account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showTransferFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Transfer Failed");
        alert.setHeaderText("Transfer Failed");
        alert.setContentText("Transfer failed. Please double check destination account and amount and try again.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    @FXML
    private Button transferButton;
    
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
        String destinationInput = destinationAccountTextField.getText();
        String amountInput = transferAmountTextField.getText();

        if(destinationInput != "" && amountInput != "") {
            try {

                double transferAmount = Double.parseDouble(amountInput);
                int resultCount = 0;
    
                try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                    String query = String.format("SELECT COUNT(*) FROM accounts WHERE accountNumber=%s", destinationInput);
                    try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {
                        while (rs.next()) {
                            resultCount = rs.getInt(1);
                        }
                    }
                } catch (SQLException e) {
                    showTransferFail();
                    e.printStackTrace();
                }
    
                if(resultCount == 0) {
                    showInvalidDestination();
                } else {
    
                    int sourceAffected = 0;
                    int destinationAffected = 0;
    
                    try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                        String query = String.format("UPDATE accounts SET balance = balance - %f WHERE accountId = %d", transferAmount, selectedAccount);
                        try (Statement stmt = conn.createStatement()) {
                            sourceAffected = stmt.executeUpdate(query);
                        }
                    } catch (SQLException e) {
                        showTransferFail();
                        e.printStackTrace();
                    }
    
                    try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                        String query = String.format("UPDATE accounts SET balance = balance + %f WHERE accountNumber = %s", transferAmount, destinationInput);
                        try (Statement stmt = conn.createStatement()) {
                            destinationAffected = stmt.executeUpdate(query);
                        }
                    } catch (SQLException e) {
                        showTransferFail();
                        e.printStackTrace();
                    }
    
                    if(sourceAffected > 0 && destinationAffected > 0)  {
                        showTransferSuccess();
                        App.setRoot("secondary");
                    }
    
                }
    
            } catch (NumberFormatException e){
                destinationAccountLabel.setText("Invalid amount!");
            }
        }
     }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
