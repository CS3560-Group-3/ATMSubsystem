package atm_sub_system;

public class AccountOption {

          /*
           * This class is used to display the list of user accounts as "Account #XXXX (Type)" while storing the account ID with a JavaFX ComboBox.
           */

          private final int id;
          private final String displayText;

          // Initialize values
          public AccountOption(int id, String displayText) {
                    this.id = id;
                    this.displayText = displayText;
          }

          // Get Account ID value
          public int getId() {
            return id;
          }

          // Get display text value
          public String getDisplayText() {
            return displayText;
          }

          @Override
          public String toString() {
            // Convert to string
            return displayText;
          }
      }
      
