package tn.esprit.rechargeplus.services.LoanService;
import tn.esprit.rechargeplus.entities.Repayment;

import java.io.IOException;
import java.util.List;

public interface IRepaymentService {
    Repayment addRepayment(Repayment repayment);
    Repayment updateRepayment(Repayment repayment);
    void remouveRepayment(Long numRepayment);
    Repayment retrieveRepayment(Long numRepayment);
    List<Repayment> retriveAll();
    public  void sendSms(double amount) throws IOException;
}
