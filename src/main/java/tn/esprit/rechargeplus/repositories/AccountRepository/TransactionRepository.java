package tn.esprit.rechargeplus.repositories.AccountRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.Loan_Status;
import tn.esprit.rechargeplus.entities.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndSourceIsLike(Long accountId, String source);
    List<Transaction> findByAccountIdAndDestinationIsLike(Long accountId, String destination);
    List<Transaction> findByAccountIdAndCreatedAtAfter(long accountId, Date date);  // Transactions récentes
    List<Transaction> findByAccountIdAndLoanStatusIn(Long accountId, List<Loan_Status> statuses);

    List<Transaction> findByLoan_IdLoan(long loanIdLoan);

}
