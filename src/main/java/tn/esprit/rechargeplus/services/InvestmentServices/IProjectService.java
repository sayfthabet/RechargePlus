package tn.esprit.rechargeplus.services.InvestmentServices;

import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.entities.Project_Status;

import java.util.List;

public interface IProjectService {
    Project addProject(Project project);
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    Project updateProject(Project project);
    void deleteProject(Long id);
    List<Project> findByTitle(String title);
    List<Project> findBySector(String sector);
    List<Project> findByLocation(String location);
    List<Project> findByStatus(Project_Status status);

}
