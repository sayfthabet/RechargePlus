package tn.esprit.rechargeplus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.InvestmentRequest;
import tn.esprit.rechargeplus.services.IInvestmentRequestService;

import java.util.List;
@RestController
@RequestMapping("/investmentRequest")
@AllArgsConstructor
public class InvestmentRequestController {
    @Autowired
    private IInvestmentRequestService investmentRequestService;

    @PostMapping("/add")
    public InvestmentRequest addInvestmentRequest(@RequestBody InvestmentRequest investmentRequest) {
        return investmentRequestService.addInvestmentRequest(investmentRequest);
    }

    @GetMapping("/getAll")
    public List<InvestmentRequest> getAllInvestmentRequests() {
        return investmentRequestService.getAllInvestmentRequests();
    }

    @GetMapping("/get/{id}")
    public InvestmentRequest getInvestmentRequestById(@PathVariable Long id) {
        return investmentRequestService.getInvestmentRequestById(id);
    }

    @PutMapping("/update")
    public InvestmentRequest updateInvestmentRequest(@RequestBody InvestmentRequest investmentRequest) {
        return investmentRequestService.updateInvestmentRequest(investmentRequest);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteInvestmentRequest(@PathVariable Long id) {
        investmentRequestService.deleteInvestmentRequest(id);
    }
}
