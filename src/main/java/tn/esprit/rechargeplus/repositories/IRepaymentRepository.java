package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Repayment;

import java.util.Date;
import java.util.List;

@Repository
public interface IRepaymentRepository extends JpaRepository<Repayment, Long> {
    List<Repayment> findByloanIdLoan(long loanId);  // Trouver les remboursements d'un prêt
    List<Repayment> findByExpectedPaymentDate(Date expectedPaymentDate);

}
