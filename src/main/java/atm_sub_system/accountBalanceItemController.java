package atm_sub_system;

import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class accountBalanceItemController {

    /*
     * This class is used to dynamically controll account balance items listed in the Account Balance screen.
     */

    @FXML
    private Label accountInfoHeader;

    @FXML
    private Label accountBalance;

    // Set the header text on an Account Balance Item
    public void setItemInfoHeader(String value) {
        accountInfoHeader.setText(value);
    }

    // Set balance value on an Account Balance Item
    public void setItemBalance(double balance) {
        accountBalance.setText(String.format("$%.2f", balance));
    }
    
}
