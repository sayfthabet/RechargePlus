package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Repayment;
@Repository
public interface IRepaymentRepository extends JpaRepository<Repayment, Long> {
}
