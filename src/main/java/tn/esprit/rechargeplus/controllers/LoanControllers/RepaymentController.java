package tn.esprit.rechargeplus.controllers.LoanControllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tn.esprit.rechargeplus.entities.Repayment;
import tn.esprit.rechargeplus.services.LoanService.IRepaymentService;

import java.io.IOException;
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


    @GetMapping("/send-sms/{amount}")
    public String sendSmsTest(@PathVariable double amount) {
        try {
            // Appel de la méthode sendSms pour tester l'envoi
            repaymentservice.sendSms(amount);
            return "SMS envoyé avec succès.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de l'envoi du SMS: " + e.getMessage();
        }
    }
}
