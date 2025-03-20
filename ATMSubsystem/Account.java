package ATMSubsystem;

import java.util.Date;

public class Account {
    private int accountId;
    private double balance;
    private Date createDate;
    private int status;

    public Account(int id, double balance, Date createDate, int status) {
        this.accountId = id;
        this.balance = balance;
        this.createDate = createDate;
        this.status = status;
    }

    public int getId() {
        return this.accountId;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public double getBalance() {
        return this.balance;
    }

    public double checkBalance() {
        return this.balance;
    }

    public double deposit(double amount) {
        this.balance += amount; // Update the balance locally
        // Make call to the central bank database to update the balance in the database
        return this.balance;
    }

    public double withdraw(double amount) {
        this.balance -= amount; // Update the balance locally
        // Make call to the central bank database to update the balance in the database
        return this.balance;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void depositAmount(double amount) {
        // Deposit the amount into the account
    }

    public void withdrawAmount(double amount) {
        // Withdraw the amount from the account
    }

    public void transferAmount(double amount, int accountNumber) {
        // Transfer the amount to the account number
    }

    
}
