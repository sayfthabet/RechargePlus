package tn.esprit.rechargeplus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.repositories.ILoanRepository;
import tn.esprit.rechargeplus.entities.Loan;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService  implements  ILoanService {
    @Autowired
     ILoanRepository loanRepository;
    @Override
    public Loan addLoan(Loan loan) {
        return loanRepository.save(loan);
    }
    @Override
    public Loan updateLoan(Loan loan) {
        return loanRepository.save(loan);

    }
    @Override
    public void remouveLoan(Long numLoan) {
        loanRepository.deleteById(numLoan);
    }
    @Override
    public Loan retrieveLoan(Long numLoan) {
        return loanRepository.findById(numLoan).orElse(null);
    }
    @Override
    public List<Loan> retriveAll() {
        return (List<Loan>) loanRepository.findAll();
    }
}
