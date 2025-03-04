package tn.esprit.rechargeplus.repositories.InvestmentRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.entities.Project_Status;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // ✅ Projets ayant reçu le plus d'investissements
    @Query("SELECT p FROM Project p ORDER BY p.amountCollected DESC")
    List<Project> findTopProjectsByInvestment();
    List<Project> findByTitleContainingIgnoreCase(String title);
    List<Project> findBySectorContainingIgnoreCase(String sector);
    List<Project> findByLocationContainingIgnoreCase(String location);
    List<Project> findByStatus(Project_Status status);

}
