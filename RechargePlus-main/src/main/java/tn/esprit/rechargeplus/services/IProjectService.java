package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.entities.Project;

import java.util.List;

public interface IProjectService {
    Project addProject(Project project);
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    Project updateProject(Project project);
    void deleteProject(Long id);
}
