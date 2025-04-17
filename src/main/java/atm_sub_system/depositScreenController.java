package atm_sub_system;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
public class depositScreenController {

    private String url = "jdbc:mysql://localhost:3306/atmsubsystem";
    private String user = "root"; 
    private String password = "root"; 

    @FXML
    private Label insertAmountDepositLabel;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private ComboBox<String> accountSelectDropdown;

    public void initialize() {
        ObservableList<String> accounts = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = String.format("SELECT * FROM accounts WHERE customerId=%d", App.sessionCustomerId.get());
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    accounts.add(String.format("Account #%d - %s", rs.getInt("accountNumber"), rs.getString("type")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        accountSelectDropdown.setItems(accounts);
    }

    @FXML
    private void deposit() throws IOException {
        String input = depositAmountTextField.getText();

        try{
            int depositedAmount = Integer.parseInt(input);
            
            int newBalance = depositedAmount + App.balance.get();
            App.balance.set(newBalance);

            //  update the screen
            insertAmountDepositLabel.setText("Deposited $" + depositedAmount);

            // Clear input field
            depositAmountTextField.clear();
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
