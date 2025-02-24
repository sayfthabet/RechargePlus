package tn.esprit.rechargeplus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.services.ILoanService;
import tn.esprit.rechargeplus.entities.Loan;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    ILoanService loanservice;

    @GetMapping("/getloans")
    public List<Loan> allLoans(){
        return loanservice.retriveAll();}

    @PostMapping("/addLoan")
    public Loan addLoan(@RequestBody Loan loan){
        return loanservice.addLoan(loan);
    }

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

}
