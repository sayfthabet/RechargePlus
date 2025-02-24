package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.Loan;

import java.util.List;

public interface ILoanService {
    Loan addLoan(Loan loan);
    Loan updateLoan(Loan loan);
    void remouveLoan(Long numLoan);
    Loan retrieveLoan(Long numLoan);
    List<Loan> retriveAll();
}
