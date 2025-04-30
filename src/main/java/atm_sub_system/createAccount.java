package atm_sub_system;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.sql.*;

public class createAccount {

    @FXML private TextField firstNameLabel;
    @FXML private TextField lastNameLabel;
    @FXML private TextField phoneNumberLabel;
    @FXML private TextField emailAddressLabel;
    @FXML private TextField ssnLabel;
    @FXML private TextField streetAddressLabel;
    @FXML private TextField cityLabel;
    @FXML private TextField stateLabel;
    @FXML private TextField zipCodeLabel;
    @FXML private PasswordField pinLabel;
    @FXML private PasswordField confirmPinLabel;
    @FXML private CheckBox createSavingsCheckbox;

    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Account Creation Failed");
        alert.setContentText(message);
        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }
    
    // Define helper functions to check if card and account numbers exist

    private boolean cardNumberExists(Connection conn, long cardNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM debitcards WHERE cardNumber = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(query)) {
            pStatement.setLong(1, cardNumber);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    private boolean accountNumberExists(Connection conn, long accountNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM accounts WHERE accountNumber = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(query)) {
            pStatement.setLong(1, accountNumber);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // Define helper functions to generate unique account and card numbers

    private long generateUniqueAccountNumber(Connection conn) throws SQLException {
        long accountNumber;
        do {
            accountNumber = (long) (Math.random() * 9000000000L) + 1000000000L;
        } while (accountNumberExists(conn, accountNumber));
        return accountNumber;
    }
    
    private long generateUniqueCardNumber(Connection conn) throws SQLException {
        long cardNumber;
        do {
            cardNumber = (long) (Math.random() * 9000000000000000L) + 1000000000000000L;
        } while (cardNumberExists(conn, cardNumber));
        return cardNumber;
    }

    @FXML
    public void initialize() {
    }
    
    // Function to make SQL queries to update address, customer, account, and debitcard tables to create a user account
    @FXML
    public void createNewAccount() {
        String firstName = firstNameLabel.getText();
        String lastName = lastNameLabel.getText();
        String phoneNumber = phoneNumberLabel.getText();
        String emailAddress = emailAddressLabel.getText();
        String streetAddress = streetAddressLabel.getText();
        String city = cityLabel.getText();
        String state = stateLabel.getText();
        String zipCode = zipCodeLabel.getText();
        String ssn = ssnLabel.getText();
        String pin = pinLabel.getText();
        String confirmPin = confirmPinLabel.getText();

        // Make sure all fields are filled
        if (firstName.isEmpty() || lastName.isEmpty() || emailAddress.isEmpty() || phoneNumber.isEmpty() || streetAddress.isEmpty() || 
            city.isEmpty() || state.isEmpty() || zipCode.isEmpty() || ssn.isEmpty() || pin.isEmpty() || confirmPin.isEmpty()) {
            showErrorAlert("All fields are required.");
            return;
        }

        // Make sure pins match
        if (!pin.equals(confirmPin)) {
            showErrorAlert("PIN fields do not match.");
            return;
        }

        // Connect to DB
        try (Connection conn = DriverManager.getConnection(App.db_url, App.db_user, App.db_password)) {
            // Perform all instructions at once, then if any fails, we can rollback all easily
            conn.setAutoCommit(false);
            
            try {
                // Insert address into address table
                String insertAddressQuery = "INSERT INTO addresses (streetAddress1, city, state, zip) VALUES (?, ?, ?, ?)";
                PreparedStatement pStatement = conn.prepareStatement(insertAddressQuery, Statement.RETURN_GENERATED_KEYS);
                pStatement.setString(1, streetAddress);
                pStatement.setString(2, city);
                pStatement.setString(3, state);
                pStatement.setString(4, zipCode);
                pStatement.executeUpdate();
                
                // Get the address id to be used as a foreign key
                ResultSet addressKeys = pStatement.getGeneratedKeys();
                int addressId = -1;
                if (addressKeys.next()) {
                    addressId = addressKeys.getInt(1);
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
                
                // Insert customer into customer table
                String insertCustomerQuery = "INSERT INTO customers (addressId, firstName, lastName, phoneNumber, emailAddress, ssn) VALUES (?, ?, ?, ?, ?, ?)";
                pStatement = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS);
                pStatement.setInt(1, addressId);
                pStatement.setString(2, firstName);
                pStatement.setString(3, lastName);
                pStatement.setString(4, phoneNumber);
                pStatement.setString(5, emailAddress);
                pStatement.setString(6, ssn);
                pStatement.executeUpdate();
                
                // Get the customer id to be used as a foreign key
                ResultSet customerKeys = pStatement.getGeneratedKeys();
                int customerId = -1;
                if (customerKeys.next()) {
                    customerId = customerKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
                
                // Insert checking account into account table
                long checkingAccountNumber = generateUniqueAccountNumber(conn);
                String insertAccountQuery = "INSERT INTO accounts (customerId, accountNumber, balance, type, openDate) VALUES (?, ?, 0.00, 'checking', CURRENT_DATE)";
                pStatement = conn.prepareStatement(insertAccountQuery);
                pStatement.setInt(1, customerId);
                pStatement.setLong(2, checkingAccountNumber);
                pStatement.executeUpdate();
                
                // Insert savings account into account table if checkbox is selected
                if (createSavingsCheckbox.isSelected()) {
                    long savingsAccountNumber = generateUniqueAccountNumber(conn);
                    
                    String insertSavingsQuery = "INSERT INTO accounts (customerId, accountNumber, balance, type, openDate) VALUES (?, ?, 0.00, 'savings', CURRENT_DATE)";
                    pStatement = conn.prepareStatement(insertSavingsQuery);
                    pStatement.setInt(1, customerId);
                    pStatement.setLong(2, savingsAccountNumber);
                    pStatement.executeUpdate();
                }
                
                // Insert debit card into debitcard table
                long cardNumber = generateUniqueCardNumber(conn);
                String insertCardQuery = "INSERT INTO debitcards (customerId, cardNumber, pinCode, expiryDate, CCV, status) VALUES (?, ?, ?, ?, ?, 'active')";
                pStatement = conn.prepareStatement(insertCardQuery);
                pStatement.setInt(1, customerId);
                pStatement.setLong(2, cardNumber);
                pStatement.setInt(3, Integer.parseInt(pin));
                pStatement.setString(4, "2029-04-29");
                pStatement.setInt(5, (int) (Math.random() * 900) + 100);
                pStatement.executeUpdate();
                
                // Commit transaction together
                conn.commit();
                
                // Display success message with account information
                String accountsCreated = "Checking Account: $0.00 initial balance";
                if (createSavingsCheckbox.isSelected()) {
                    accountsCreated += "\nSavings Account: $0.00 initial balance";
                }
                
                // Show success alert with card number and PIN displayed so the user can login
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Account Created");
                alert.setHeaderText("Account Created Successfully");
                alert.setContentText("Your account has been created successfully.\n\n" +
                                    "Card Number: " + cardNumber + "\n" +
                                    "PIN: " + pin + "\n\n" +
                                    accountsCreated + "\n\n" +
                                    "Please remember these details for logging in.");
                alert.getDialogPane().setPrefSize(500, 300);
                alert.showAndWait();
                
                // Go back to the start screen
                App.setRoot("startScreen");
                
            } catch (SQLException e) {
                // Undo all commits
                conn.rollback();
                showErrorAlert("Error creating account: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (SQLException e) {
            showErrorAlert("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showErrorAlert("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to return to login screen when the Back button is clicked on
    @FXML
    private void goBack() throws IOException {
        App.setRoot("startScreen");
    }
}
