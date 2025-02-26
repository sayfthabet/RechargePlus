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
    private Date expectedPaymentDate;
    private Date actualPaymentDate;
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

    public Date getExpectedPaymentDate() {
        return expectedPaymentDate;
    }

    public void setExpectedPaymentDate(Date expectedPaymentDate) {
        this.expectedPaymentDate = expectedPaymentDate;
    }

    public Date getActualPaymentDate() {
        return actualPaymentDate;
    }

    public void setActualPaymentDate(Date actualPaymentDate) {
        this.actualPaymentDate = actualPaymentDate;
    }
}
