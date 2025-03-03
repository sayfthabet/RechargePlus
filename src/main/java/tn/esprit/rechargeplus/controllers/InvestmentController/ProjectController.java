package tn.esprit.rechargeplus.controllers.InvestmentController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.entities.Project_Status;
import tn.esprit.rechargeplus.services.InvestmentServices.IProjectService;
import tn.esprit.rechargeplus.services.InvestmentServices.PdfService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

@Autowired
    private  IProjectService projectService;
    private final PdfService pdfService;
    public ProjectController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/downloadPdf")
    public ResponseEntity<byte[]> downloadProjectPdf() throws IOException {
        byte[] pdfContent = pdfService.generateProjectPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=projects.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
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
    @GetMapping("/search/title")
    public List<Project> searchProjectsByTitle(@RequestParam String title) {
        return projectService.findByTitle(title);
    }
    @GetMapping("/search/sector/{sector}")
    public List<Project> searchProjectsBySector(@PathVariable String sector) {
        return projectService.findBySector(sector);
    }
    @GetMapping("/search/location/{location}")
    public List<Project> searchProjectsByLocation(@PathVariable String location) {
        return projectService.findByLocation(location);
    }
    @GetMapping("/filterByStatus/{status}")
    public List<Project> searchProjectsByStatus(@PathVariable Project_Status status) {
        return projectService.findByStatus(status);
    }


}
