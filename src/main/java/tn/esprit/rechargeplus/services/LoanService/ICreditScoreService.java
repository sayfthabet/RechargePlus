package tn.esprit.rechargeplus.services.LoanService;

import java.util.List;

public interface ICreditScoreService {
    double calculateCreditScore(Long accountId);
    String getLoanDecision(Long accountId);
    List<Long> detectFraudulentManipulations(Long accountId);
}
