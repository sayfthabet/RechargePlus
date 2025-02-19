package tn.esprit.rechargeplus.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.repositories.ProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements IProjectService{

    @Autowired
    private   ProjectRepository projectRepo;
    @Override
    public Project addProject(Project project) { return projectRepo.save(project); }
    @Override
    public List<Project> getAllProjects() { return projectRepo.findAll(); }
    @Override
    public Project getProjectById(Long id) {
        Optional<Project> optionalRequest = projectRepo.findById(id);
        return optionalRequest.orElse(null);
    }
    @Override
    public Project updateProject(Project project) { return projectRepo.save(project); }
    @Override
    public void deleteProject(Long id) { projectRepo.deleteById(id); }
}
