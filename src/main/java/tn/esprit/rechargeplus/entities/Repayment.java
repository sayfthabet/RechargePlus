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
public class Repayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idRepayment;
    private Date Repaymentdate;
    private double monthly_amount;
    @Enumerated(value = EnumType.STRING)
    private Repayment_Status status;
    @ManyToOne
    private Loan loan;
}
