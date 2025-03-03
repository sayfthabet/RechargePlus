package tn.esprit.rechargeplus.repositories.LoanRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Guarantor;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGuarantorRepository extends JpaRepository<Guarantor, Long> {
    List<Guarantor> findAllByApprouvedFalse();

    Optional<Guarantor> findGuarantorById(Long id);

}
