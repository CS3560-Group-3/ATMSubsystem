package atm_sub_system;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class transferFundsScreenController {
    @FXML
    private TextField transferAmountTextField;
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
