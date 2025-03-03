package tn.esprit.rechargeplus.controllers.LoanControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.rechargeplus.entities.Guarantor;
import tn.esprit.rechargeplus.services.LoanService.IGuarantorService;


import java.io.IOException;
import java.util.List;
@RestController
@AllArgsConstructor
@RequestMapping("/guarantor")
public class GuarantorController {
    @Autowired
    IGuarantorService guarantorservice;

    @GetMapping("/getGuarantors")
    public List<Guarantor> allGuarantors(){
        return guarantorservice.retriveAll();}


    @PostMapping(value = "/addGuarantor", consumes = {"multipart/form-data"})
    public Guarantor addGuarantor(@RequestPart("guarantor") String guarantorJson,
                                  @RequestPart(value = "incomeProof", required = false) MultipartFile incomeProof,
                                  @RequestPart(value = "commitmentLetter", required = false) MultipartFile commitmentLetter) throws IOException {

        // Convertir JSON en objet Java
        ObjectMapper objectMapper = new ObjectMapper();
        Guarantor guarantor = objectMapper.readValue(guarantorJson, Guarantor.class);

        // Convertir MultipartFile en byte[]
        if (incomeProof != null) {
            guarantor.setIncomeProof(incomeProof.getBytes());
        }
        if (commitmentLetter != null) {
            guarantor.setCommitmentLetter(commitmentLetter.getBytes());
        }

        // Sauvegarde en base
        return guarantorservice.addGuarantor(guarantor);
    }

    @DeleteMapping("deleteGuarantor/{id}")
    public void deleteGuarantor(@PathVariable long id){
        guarantorservice.remouveGuarantor(id);
    }
    @PutMapping("/updateGuarantor")
    public Guarantor updateGuarantor(@RequestBody Guarantor guarantor) {
        return  guarantorservice.updateGuarantor(guarantor);
    }

    @GetMapping("/get/{id}")
    public Guarantor getGuarantor(@PathVariable Long id) {
        return guarantorservice.retrieveGuarantor(id);
    }

    @GetMapping("/getNonApprouvedGuarantors")
    public List<Guarantor> NonApprouvedGuarantors(){
        return guarantorservice.NonApprouvedGuarantors();}

    @PutMapping("/approve/{id}")
    public ResponseEntity<Guarantor> approveGuarantor(@PathVariable Long id) {
        Guarantor updatedGuarantor = guarantorservice.approveGuarantor(id);
        return ResponseEntity.ok(updatedGuarantor);
    }

}


