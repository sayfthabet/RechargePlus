package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Loan;
@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {
}
