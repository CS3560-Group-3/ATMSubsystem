package atm_sub_system;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class accountBalanceScreenController {
    @FXML
    private Label currentBalanceLabel;

    @FXML
    private Label currentSavingsBalanceLabel;

    public void initialize() {
        // Bind label text to App.balance property
        currentBalanceLabel.textProperty().bind(App.balance.asString("$%,d"));
        currentSavingsBalanceLabel.textProperty().bind(App.savingsBalance.asString("$%,d"));
        
    }

    

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    private Label checkingsLabel;
    @FXML
    private Label savingsLabel;
}
