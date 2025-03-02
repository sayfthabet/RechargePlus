package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.Account;
import tn.esprit.rechargeplus.entities.Loan;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findById(long accountId);
}
