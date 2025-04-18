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

    private int selectedAccount = -1;

    @FXML
    private Label insertAmountDepositLabel;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private Button depositButton;

    @FXML
    private ComboBox<AccountOption> accountSelectDropdown;

    private void showDepositSuccess() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Deposit Successful");
        alert.setHeaderText("Deposit Successful");
        alert.setContentText("The specified amount of money was deposited into your account.");
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showDepositFail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Deposit Failed");
        alert.setHeaderText("Deposit Failed");
        alert.setContentText("Something went wrong when depositing your money. Please try again.");
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
            insertAmountDepositLabel.setVisible(true);
            depositAmountTextField.setVisible(true);
            depositAmountTextField.clear();
            depositButton.setVisible(true);
        });
    }

    @FXML
    private void deposit() throws IOException {
        String input = depositAmountTextField.getText();

        try{
            double depositedAmount = Double.parseDouble(input);
            
            try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
                String query = String.format("UPDATE accounts SET balance = balance + %f WHERE accountId = %d", depositedAmount, selectedAccount);
                try (Statement stmt = conn.createStatement()) {
                    int affected = stmt.executeUpdate(query);
                    if(affected > 0) {
                        showDepositSuccess();
                        App.setRoot("secondary");
                    }
    
                }
            } catch (SQLException e) {
                showDepositFail();
                e.printStackTrace();
            }
        }
        catch (NumberFormatException e){
            insertAmountDepositLabel.setText("Invalid amount!");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

}
