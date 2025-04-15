package atm_sub_system;
import java.io.IOException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class accountBalanceScreenController {
    @FXML
    private Label currentBalanceLabel;

    public void initialize() {
        // Bind label text to App.balance property
        currentBalanceLabel.textProperty().bind(App.balance.asString("$%,d"));
        
    }
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
