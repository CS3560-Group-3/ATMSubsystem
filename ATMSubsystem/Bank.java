package ATMSubsystem;

public class Bank {

    private static final String BANK_NAME = "Cal Poly Pomona Credit Union";
    private static final String BANK_ADDRESS = "3801 W Temple Ave, Pomona, CA 91768";
    private static final String BANK_PHONE = "(909) 869-7659";

    private int branchId;
    private String branchLocation;

    public Bank(int branchId, String branchLocation) {
        this.branchId = branchId;
        this.branchLocation = branchLocation;
    }

    public String getBankName() {
        return BANK_NAME;
    }

    public String getBankAddress() {
        return BANK_ADDRESS;
    }

    public String getBankPhone() {
        return BANK_PHONE;
    }

    public int getBranchId() {
        return this.branchId;
    }

    public String getBranchLocation() {
        return this.branchLocation;
    }

    public void addATM(ATM atm) {
        this.atms.add(atm);
    }

    public void removeATM(ATM atm) {
        this.atms.remove(atm);
    }

    public List<ATM> getATMs() {
        return this.atms;
    }

    public void addBank(Bank bank) {
        this.banks.add(bank);
    }

    public void removeBank(Bank bank) {
        this.banks.remove(bank);
    }

    public List<Bank> getBanks() {
        return this.banks;
    }
}
