package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idTransaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTransaction;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private String source;
    private String destination;

    @Positive(message = "Amount must be a positive value")
    private double amount;

    @Enumerated(EnumType.STRING)
    private Transaction_Status status;

    // Relationships to other modules (optional)
    @ManyToOne
   // @JsonBackReference
    @JsonIgnore
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "idAccount")
    private Account account;

    @ManyToOne
    private InvestmentRequest investment_request;
    public Account getAccount() {
        return account;
    }
    private double fee;
    private boolean isReversed = false;
    private String reversalReason;
    private String ipAddress; // IP Address Tracking

    @ManyToOne
    private Transaction originalTransaction;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }

    public double getTotalAmount() {
        return this.amount + this.fee;
    }

    public boolean isReversible() {
        return !this.isReversed && this.status == Transaction_Status.COMPLETED;
    }

    // Assumes destination is stored as "ACC-{accountId}"
    public Long getDestinationAccountId() {
        return Long.parseLong(this.destination.replace("ACC-", ""));
    }

    public long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Transaction_Status getStatus() {
        return status;
    }

    public void setStatus(Transaction_Status status) {
        this.status = status;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public InvestmentRequest getInvestment_request() {
        return investment_request;
    }

    public void setInvestment_request(InvestmentRequest investment_request) {
        this.investment_request = investment_request;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void setReversed(boolean reversed) {
        isReversed = reversed;
    }

    public String getReversalReason() {
        return reversalReason;
    }

    public void setReversalReason(String reversalReason) {
        this.reversalReason = reversalReason;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Transaction getOriginalTransaction() {
        return originalTransaction;
    }

    public void setOriginalTransaction(Transaction originalTransaction) {
        this.originalTransaction = originalTransaction;
    }
}


