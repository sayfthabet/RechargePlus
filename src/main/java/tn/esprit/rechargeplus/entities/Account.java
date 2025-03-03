package tn.esprit.rechargeplus.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public long getId() {
        return this.id;
    }

}
