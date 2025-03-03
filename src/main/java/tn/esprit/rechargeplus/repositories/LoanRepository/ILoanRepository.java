package tn.esprit.rechargeplus.repositories.LoanRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Loan;
import tn.esprit.rechargeplus.entities.Loan_Status;

import java.util.List;

@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {
   // List<Loan> findByAccount(long accountId);
    List<Loan> findByTransactionsAccountId(long accountId);// Trouver les prêts d'un compte
    List<Loan> findByTransactionsAccountIdAndStatus(long accountId, Loan_Status status);// Trouver les prets en cours d'un compte
    List<Loan> findByTransactionsAccountIdAndStatusIn(Long accountId, List<Loan_Status> statuses);

    List<Loan> findByStatus(Loan_Status status);

}
