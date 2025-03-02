package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.Loan;

import java.util.List;
import java.util.Map;

public interface ILoanService {
    Loan addLoan(Loan loan);
    Loan updateLoan(Loan loan);
    void remouveLoan(Long numLoan);
    Loan retrieveLoan(Long numLoan);
    List<Loan> retriveAll();
    List<Map<String, Object>> calculateConstantAnnuity(double loanAmount, double interestRate, double duration);
    List<Map<String, Object>> calculateConstantAmortization(double loanAmount, double interestRate, double duration);
    Map<String, Object> getLoanRepaymentPlan(Long accountId, double requestedAmount, int requestedDuration) ;
   int calculateAnnuityDuration(double P, double Rm, double i) ;
   int calculateAmortizationDuration(double P, double Rm, double i) ;
   Loan createLoan(Long accountId, double requestedAmount, int requestedDuration, String repaymentType) ;
   byte[] generateLoanDocument(Long loanId) throws java.io.IOException ;


    }
