package atm_sub_system;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("startScreen");
    }

    @FXML
    private void switchToAccountBalanceScreen() throws IOException {
        App.setRoot("accountBalanceScreen");
    }

    @FXML
    private void switchToDepositScreen() throws IOException {
        App.setRoot("depositScreen");
    }

    @FXML
    private void switchToWithdrawlScreen() throws IOException {
        App.setRoot("withdrawlScreen");
    }
    @FXML
    private void switchToTransferFundsScreen() throws IOException {
        App.setRoot("transferFundsScreen");
    }
    
    

}