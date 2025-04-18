package atm_sub_system;

public class AccountOption {
          private final int id;
          private final String displayText;

          public AccountOption(int id, String displayText) {
                    this.id = id;
                    this.displayText = displayText;
          }

          public int getId() {
                    return id;
          }

          public String getDisplayText() {
                    return displayText;
          }

          @Override
          public String toString() {
                    return displayText;
          }
      }
      
