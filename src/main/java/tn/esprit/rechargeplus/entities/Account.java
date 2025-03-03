package tn.esprit.rechargeplus.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity


@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private Account_Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    private String type;

    // Daily transaction limit features
    private double dailyTransactionLimit = 5000.00;
    private double dailyTransactionTotal = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTransactionDate;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Transaction> transactions;

    @ManyToOne
    private User user;

    public Account() {
    }

    public Account(long id, double amount, Account_Status status, Date created_at, Date updated_at, String type, double dailyTransactionLimit, double dailyTransactionTotal, Date lastTransactionDate, List<Transaction> transactions, User user) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.type = type;
        this.dailyTransactionLimit = dailyTransactionLimit;
        this.dailyTransactionTotal = dailyTransactionTotal;
        this.lastTransactionDate = lastTransactionDate;
        this.transactions = transactions;
        this.user = user;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Account_Status getStatus() {
        return status;
    }

    public void setStatus(Account_Status status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDailyTransactionLimit() {
        return dailyTransactionLimit;
    }

    public void setDailyTransactionLimit(double dailyTransactionLimit) {
        this.dailyTransactionLimit = dailyTransactionLimit;
    }

    public double getDailyTransactionTotal() {
        return dailyTransactionTotal;
    }

    public void setDailyTransactionTotal(double dailyTransactionTotal) {
        this.dailyTransactionTotal = dailyTransactionTotal;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
