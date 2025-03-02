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
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idLoan;
    private double amount;
    private double interestRate;
    private int duration; // in months
    private double caution;
    @Enumerated(value = EnumType.STRING)
    private Loan_Status status;
    private Date request_date;
    private double total_repayment_amount;
    private double remaining_repayment;
    @OneToMany (mappedBy = "loan")
    private List<Transaction> transactions;
    @OneToMany (mappedBy = "loan")
    private List<Repayment> repayments;
}
