package ATMSubsystem;

import java.util.Date;

public class Card {
    public int cardNumber;
    public int ccv;
    public int status;
    public Date expiryDate;

    public Card(int cardNumber, int ccv, int status, Date expiryDate) {
        this.cardNumber = cardNumber;
        this.ccv = ccv;
        this.status = status;
        this.expiryDate = expiryDate;
    }

    public int getCardNumber() {
        return this.cardNumber;
    }

    public int getCCV() {
        return this.ccv;
    }   

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public int checkPin() {
        return this.pin;
    }
}
