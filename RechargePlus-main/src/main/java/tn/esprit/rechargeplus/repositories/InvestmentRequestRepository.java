package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.InvestmentRequest;

public interface InvestmentRequestRepository extends JpaRepository<InvestmentRequest, Long> {
}
