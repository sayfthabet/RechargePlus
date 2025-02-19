package tn.esprit.rechargeplus.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.repositories.InvestmentRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InvestmentRequestServiceImpl implements IInvestmentRequestService{
    @Autowired
    private InvestmentRequestRepository investmentRequestRepository;

    @Override
    public InvestmentRequest addInvestmentRequest(InvestmentRequest investmentRequest) {
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
