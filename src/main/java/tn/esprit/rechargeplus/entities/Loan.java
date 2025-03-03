package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idLoan")
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
    @Lob // Annonce que cet attribut contient de grandes données (comme un fichier)
    private byte[] loanPdf;
    @OneToMany (mappedBy = "loan")
    //@JsonManagedReference
    private List<Transaction> transactions;
    @OneToMany (mappedBy = "loan")
    private List<Repayment> repayments;
    @OneToOne
    private Guarantor guarantor;

    public long getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(long idLoan) {
        this.idLoan = idLoan;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getCaution() {
        return caution;
    }

    public void setCaution(double caution) {
        this.caution = caution;
    }

    public Loan_Status getStatus() {
        return status;
    }

    public void setStatus(Loan_Status status) {
        this.status = status;
    }

    public Date getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Date request_date) {
        this.request_date = request_date;
    }

    public double getTotal_repayment_amount() {
        return total_repayment_amount;
    }

    public void setTotal_repayment_amount(double total_repayment_amount) {
        this.total_repayment_amount = total_repayment_amount;
    }

    public double getRemaining_repayment() {
        return remaining_repayment;
    }

    public void setRemaining_repayment(double remaining_repayment) {
        this.remaining_repayment = remaining_repayment;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Repayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<Repayment> repayments) {
        this.repayments = repayments;
    }

    public byte[] getLoanPdf() {
        return loanPdf;
    }

    public void setLoanPdf(byte[] loanPdf) {
        this.loanPdf = loanPdf;
    }
}
