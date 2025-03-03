package tn.esprit.rechargeplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
}
