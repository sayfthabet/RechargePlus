package tn.esprit.rechargeplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
