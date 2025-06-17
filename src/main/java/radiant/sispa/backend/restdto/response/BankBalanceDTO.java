package radiant.sispa.backend.restdto.response;

public class BankBalanceDTO {
    private String bankName;
    private double totalBalance;
    private String accountNumber;
    private Long accountId;

    public BankBalanceDTO() {}

    public BankBalanceDTO(String bankName, double totalBalance, String accountNumber, Long accountId) {
        this.bankName = bankName;
        this.totalBalance = totalBalance;
        this.accountNumber = accountNumber;
        this.accountId = accountId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }
}
