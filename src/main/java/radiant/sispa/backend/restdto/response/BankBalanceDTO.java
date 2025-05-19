package radiant.sispa.backend.restdto.response;

public class BankBalanceDTO {
    private String bankName;
    private double totalBalance;

    public BankBalanceDTO() {}

    public BankBalanceDTO(String bankName, double totalBalance) {
        this.bankName = bankName;
        this.totalBalance = totalBalance;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }
}
