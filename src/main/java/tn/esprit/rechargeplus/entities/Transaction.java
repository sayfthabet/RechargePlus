package tn.esprit.rechargeplus.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
}
