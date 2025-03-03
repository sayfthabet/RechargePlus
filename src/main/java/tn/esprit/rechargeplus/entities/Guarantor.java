package tn.esprit.rechargeplus.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Guarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;  // Nom complet du garant
    private String nationalId; // CIN ou passeport (identifiant unique)
    private String phoneNumber; // Numéro de téléphone
    private String relationship; // Lien avec l'emprunteur (parent, ami, collègue...)
    private double monthlyIncome; // Revenu mensuel du garant
    private String bankCardNumber; // Numéro de carte bancaire (sécurisé dans un vrai système)
    private String email;
    private Boolean approuved;
    private int userId;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] incomeProof; // Justificatif de revenu (PDF, image...)

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] commitmentLetter; // Lettre d'engagement de paiement

    @OneToOne(mappedBy = "guarantor")
    private Loan loan; // Association avec le prêt garanti

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public Boolean getApprouved() {
        return approuved;
    }

    public void setApprouved(Boolean approuved) {
        this.approuved = approuved;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getIncomeProof() {
        return incomeProof;
    }

    public void setIncomeProof(byte[] incomeProof) {
        this.incomeProof = incomeProof;
    }

    public byte[] getCommitmentLetter() {
        return commitmentLetter;
    }

    public void setCommitmentLetter(byte[] commitmentLetter) {
        this.commitmentLetter = commitmentLetter;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


