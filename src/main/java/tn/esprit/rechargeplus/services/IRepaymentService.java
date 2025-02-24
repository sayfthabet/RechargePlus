package tn.esprit.rechargeplus.services;
import tn.esprit.rechargeplus.entities.Repayment;

import java.util.List;

public interface IRepaymentService {
    Repayment addRepayment(Repayment repayment);
    Repayment updateRepayment(Repayment repayment);
    void remouveRepayment(Long numRepayment);
    Repayment retrieveRepayment(Long numRepayment);
    List<Repayment> retriveAll();
}
