package tn.esprit.rechargeplus.repositories.LoanRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Repayment;
import tn.esprit.rechargeplus.entities.Repayment_Status;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface IRepaymentRepository extends JpaRepository<Repayment, Long> {
   List<Repayment> findByloanIdLoan(long loanId);  // Trouver les remboursements d'un prêt
    List<Repayment> findByExpectedPaymentDate(Date expectedPaymentDate);

   List<Repayment> findByLoan_IdLoanAndStatus(long loanIdLoan, Repayment_Status status);
    @Query("SELECT r FROM Repayment r WHERE r.expectedPaymentDate BETWEEN :startOfDay AND :endOfDay")
    List<Repayment> findRepaymentsDueToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

}
