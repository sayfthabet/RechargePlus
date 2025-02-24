package tn.esprit.rechargeplus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.services.ILoanService;
import tn.esprit.rechargeplus.services.IRepaymentService;
import tn.esprit.rechargeplus.entities.Repayment;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/repayment")
public class RepaymentController {
    @Autowired
    IRepaymentService repaymentservice;

    @GetMapping("/getRepayments")
    public List<Repayment> allRepayments(){
        return repaymentservice.retriveAll();}

    @PostMapping("/addRepayment")
    public Repayment addRepayment(@RequestBody Repayment repayment){
        return repaymentservice.addRepayment(repayment);
    }

    @DeleteMapping("deleteRepayment/{id}")
    public void deleteRepayment(@PathVariable long id){
        repaymentservice.remouveRepayment(id);
    }
    @PutMapping("/updateRepayment")
    public Repayment updateRepayment(@RequestBody Repayment repayment) {
        return  repaymentservice.updateRepayment(repayment);
    }

    @GetMapping("/get/{id}")
    public Repayment getRepayment(@PathVariable Long id) {
        return repaymentservice.retrieveRepayment(id);
    }

}
