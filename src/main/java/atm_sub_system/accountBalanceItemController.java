package atm_sub_system;

import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class accountBalanceItemController {

    @FXML
    private Label accountInfoHeader;

    @FXML
    private Label accountBalance;

    public void setItemInfoHeader(String value) {
        accountInfoHeader.setText(value);
    }

    public void setItemBalance(double balance) {
        accountBalance.setText(String.format("$%.2f", balance));
    }
    
}
