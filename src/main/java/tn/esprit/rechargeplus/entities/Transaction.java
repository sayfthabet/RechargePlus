package tn.esprit.rechargeplus.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTransaction;
    private Date created_at;
    private String source;
    private String destination;
    private double amount;
    @Enumerated(value = EnumType.STRING)
    private Transaction_Status status;
    @ManyToOne
    private Loan loan;
    @ManyToOne
    private Account account;
    @ManyToOne
    private InvestmentRequest investment_request;


}
