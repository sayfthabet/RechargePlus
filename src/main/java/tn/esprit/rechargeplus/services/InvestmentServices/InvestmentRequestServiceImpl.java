package tn.esprit.rechargeplus.services.InvestmentServices;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.InvestmentRequestRepository;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InvestmentRequestServiceImpl implements IInvestmentRequestService{
    @Autowired
    private InvestmentRequestRepository investmentRequestRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public InvestmentRequest addInvestmentRequest(InvestmentRequest investmentRequest) {
        if (investmentRequest.getProject() == null || investmentRequest.getProject().getIdProject() == 0) {
            throw new IllegalArgumentException("Le projet associé est requis.");
        }

        // Vérifier si le projet existe en base
        Optional<Project> projectOpt = projectRepository.findById(investmentRequest.getProject().getIdProject());

        if (projectOpt.isEmpty()) {
            throw new IllegalArgumentException("Le projet sélectionné n'existe pas.");
        }

        Project project = projectOpt.get();
        System.out.println("Projet trouvé: " + project.getTitle());

        // Associer le projet à l'investissement
        investmentRequest.setProject(project);
        if(project.getAmountCollected() + investmentRequest.getAmount()> project.getAmountRequested())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le montant total collecté dépasse le montant demandé !");
        }
        else
        {
            project.setAmountCollected(project.getAmountCollected() + investmentRequest.getAmount());
        }
        // Sauvegarder l'investissement
        return investmentRequestRepository.save(investmentRequest);
    }



    @Override
    public List<InvestmentRequest> getAllInvestmentRequests() {
        return investmentRequestRepository.findAll();
    }

    @Override
    public InvestmentRequest getInvestmentRequestById(Long id) {
        Optional<InvestmentRequest> optionalRequest = investmentRequestRepository.findById(id);
        return optionalRequest.orElse(null);
    }

    @Override
    public InvestmentRequest updateInvestmentRequest(InvestmentRequest investmentRequest) {
        return investmentRequestRepository.save(investmentRequest);
    }

    @Override
    public void deleteInvestmentRequest(Long id) {
        investmentRequestRepository.deleteById(id);
    }
}
