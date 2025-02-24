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

    public long getIdRepayment() {
        return idRepayment;
    }

    public void setIdRepayment(long idRepayment) {
        this.idRepayment = idRepayment;
    }

    public Date getRepaymentdate() {
        return Repaymentdate;
    }

    public void setRepaymentdate(Date repaymentdate) {
        Repaymentdate = repaymentdate;
    }

    public double getMonthly_amount() {
        return monthly_amount;
    }

    public void setMonthly_amount(double monthly_amount) {
        this.monthly_amount = monthly_amount;
    }

    public Repayment_Status getStatus() {
        return status;
    }

    public void setStatus(Repayment_Status status) {
        this.status = status;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
