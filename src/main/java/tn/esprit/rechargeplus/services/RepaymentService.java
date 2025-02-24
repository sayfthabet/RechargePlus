package tn.esprit.rechargeplus.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.repositories.ILoanRepository;
import tn.esprit.rechargeplus.repositories.IRepaymentRepository;
import tn.esprit.rechargeplus.entities.Repayment;

import java.util.List;
@Service
@AllArgsConstructor
public class RepaymentService  implements IRepaymentService {
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Override
    public Repayment addRepayment(Repayment repayment) {
        return repaymentRepository.save(repayment);
    }

    @Override
    public Repayment updateRepayment(Repayment repayment) {
        return repaymentRepository.save(repayment);
    }

    @Override
    public void remouveRepayment(Long numRepayment) {
        repaymentRepository.deleteById(numRepayment);
    }

    @Override
    public Repayment retrieveRepayment(Long numRepayment) {
        return repaymentRepository.findById(numRepayment).orElse(null);
    }

    @Override
    public List<Repayment> retriveAll() {
        return (List<Repayment>) repaymentRepository.findAll();
    }
}
