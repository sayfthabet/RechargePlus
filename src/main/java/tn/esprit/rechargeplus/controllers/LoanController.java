package tn.esprit.rechargeplus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.services.ICreditScoreService;
import tn.esprit.rechargeplus.services.ILoanService;
import tn.esprit.rechargeplus.entities.Loan;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    ILoanService loanservice;
    @Autowired
    ICreditScoreService creditScoreService;


    //CRUD
    @GetMapping("/getloans")
    public List<Loan> allLoans(){return loanservice.retriveAll();}
    @PostMapping("/addLoan")
    public Loan addLoan(@RequestBody Loan loan){return loanservice.addLoan(loan);}
    @DeleteMapping("deleteLoan/{id}")
    public void deleteLoan(@PathVariable long id){
        loanservice.remouveLoan(id);
    }
    @PutMapping("/updateLoan")
    public Loan updateLoan(@RequestBody Loan Loan) {
        return  loanservice.updateLoan(Loan);
    }
    @GetMapping("/get/{id}")
    public Loan getLoan(@PathVariable Long id) {
        return loanservice.retrieveLoan(id);
    }
    @PostMapping("/createLoanWithType/{accountId}/{requestedAmount}/{requestedDuration}/{repaymentType}")
    public Loan createLoan(@PathVariable Long accountId,
                           @PathVariable double requestedAmount,
                           @PathVariable int requestedDuration,
                           @PathVariable String repaymentType) {
        return loanservice.createLoan(accountId, requestedAmount, requestedDuration, repaymentType);
    }


    @GetMapping("/fraudTransaction/{accountId}")
    public List<Long> getFraudulentManipulations(@PathVariable long accountId){
        return creditScoreService.detectFraudulentManipulations(accountId);
    }

    @GetMapping("/getScore/{accountId}")
    public double getScore(@PathVariable long accountId){
        return creditScoreService.calculateCreditScore(accountId);
    }
    @GetMapping("/getDecision/{accountId}")
    public String getLoanDecision(@PathVariable long accountId){
        return creditScoreService.getLoanDecision(accountId);
    }


    @GetMapping("/getLoanScheduleConstantAnnuity/{loanAmount}/{interestRate}/{duration}")
    public List<Map<String, Object>> calculateConstantAnnuity(@PathVariable double loanAmount,@PathVariable double interestRate,@PathVariable double duration )
    {return loanservice.calculateConstantAnnuity(loanAmount,interestRate, duration);}

    @GetMapping("/getLoanScheduleConstantAmortization/{loanAmount}/{interestRate}/{duration}")
    public List<Map<String, Object>> calculateConstantAmortization(@PathVariable double loanAmount,@PathVariable double interestRate,@PathVariable double duration )
    {return loanservice.calculateConstantAmortization(loanAmount,interestRate, duration);}

    @GetMapping("/loanSchedule/{accountId}/{amount}/{duration}")
    public ResponseEntity<Map<String, Object>> getLoanSchedule(
            @PathVariable Long accountId,
            @PathVariable double amount,
            @PathVariable int duration) {

        try {
            Map<String, Object> response = loanservice.getLoanRepaymentPlan(accountId, amount, duration);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @GetMapping("/calculateAnnuityDuration/{P}/{Rm}/{i}")
    public int calculateAnnuityDuration(
            @PathVariable("P") double P,
            @PathVariable("Rm") double Rm,
            @PathVariable("i") double i
    ) {
        return loanservice.calculateAnnuityDuration(P, Rm, i);
    }

    @GetMapping("/calculateAmortizationDuration/{P}/{Rm}/{i}")
    public int calculateAmortizationDuration(  @PathVariable("P") double P,
                                               @PathVariable("Rm") double Rm,
                                               @PathVariable("i") double i)
    {return loanservice.calculateAmortizationDuration(P,Rm,i);}


}
