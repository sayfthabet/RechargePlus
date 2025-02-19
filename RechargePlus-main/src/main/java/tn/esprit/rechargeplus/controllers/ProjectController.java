package tn.esprit.rechargeplus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.services.IProjectService;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

@Autowired
    private  IProjectService projectService;

    @GetMapping("/getProjects")
    public List<Project> allProjects() {
        return projectService.getAllProjects();
    }
    @GetMapping("/get/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping("/addProject")
    public Project addProject(@RequestBody Project project) {
        return projectService.addProject(project);
    }

    @PutMapping("/updateProject")
    public Project updateProject(@RequestBody Project project) {
        return projectService.updateProject(project);
    }

    @DeleteMapping("/deleteProject/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
