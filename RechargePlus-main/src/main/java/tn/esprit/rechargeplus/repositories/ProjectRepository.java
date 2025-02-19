package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
