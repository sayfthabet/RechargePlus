package tn.esprit.rechargeplus.services.InvestmentServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.entities.Project_Status;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.ProjectRepository;

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
    @Override
    public List<Project> findByTitle(String title) {
        return projectRepo.findByTitleContainingIgnoreCase(title);
    }
    @Override
    public List<Project> findBySector(String sector) {
        return projectRepo.findBySectorContainingIgnoreCase(sector);
    }
    @Override
    public List<Project> findByLocation(String location) {
        return projectRepo.findByLocationContainingIgnoreCase(location);
    }
    @Override
    public List<Project> findByStatus(Project_Status status) {
        return projectRepo.findByStatus(status);
    }

}
