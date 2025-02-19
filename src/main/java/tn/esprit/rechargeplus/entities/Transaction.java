package tn.esprit.rechargeplus.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTransaction;

    private Date created_at;

    private String source;

    private String destination;

    private double amount;

    @Enumerated(EnumType.STRING)
    private Transaction_Status status;

    @ManyToOne
    private Loan loan;

    @ManyToOne
    private Account account;

    @ManyToOne
    private InvestmentRequest investment_request;

    // Getter and Setter for idTransaction
    public long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(long idTransaction) {
        this.idTransaction = idTransaction;
    }

    // Getter and Setter for created_at
    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    // Getter and Setter for source
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Getter and Setter for destination
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    // Getter and Setter for amount
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Getter and Setter for status
    public Transaction_Status getStatus() {
        return status;
    }

    public void setStatus(Transaction_Status status) {
        this.status = status;
    }

    // Getter and Setter for loan
    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    // Getter and Setter for account
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    // Getter and Setter for investment_request
    public InvestmentRequest getInvestment_request() {
        return investment_request;
    }

    public void setInvestment_request(InvestmentRequest investment_request) {
        this.investment_request = investment_request;
    }
}
