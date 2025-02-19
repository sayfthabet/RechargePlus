package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.InvestmentRequest;

import java.util.List;

public interface IInvestmentRequestService {
    InvestmentRequest addInvestmentRequest(InvestmentRequest investmentRequest);
    List<InvestmentRequest> getAllInvestmentRequests();
    InvestmentRequest getInvestmentRequestById(Long id);
    InvestmentRequest updateInvestmentRequest(InvestmentRequest investmentRequest);
    void deleteInvestmentRequest(Long id);
}
