package tn.esprit.rechargeplus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.rechargeplus.entities.Guarantor;
import tn.esprit.rechargeplus.entities.Guarantor;
import tn.esprit.rechargeplus.repositories.IGuarantorRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuarantorService  implements  IGuarantorService {

    @Autowired
    IGuarantorRepository guarantorRepository;

    @Override
    public Guarantor addGuarantor(Guarantor guarantor) throws IOException {
guarantor.setApprouved(false);
        return guarantorRepository.save(guarantor);
    }


    @Override
    public Guarantor updateGuarantor(Guarantor guarantor) {
        return guarantorRepository.save(guarantor);
    }

    @Override
    public void remouveGuarantor(Long numGuarantor) {
        guarantorRepository.deleteById(numGuarantor);
    }

    @Override
    public Guarantor retrieveGuarantor(Long numGuarantor) {
        return guarantorRepository.findById(numGuarantor).orElse(null);
    }
    @Override
    public List<Guarantor> retriveAll() {
        return (List<Guarantor>) guarantorRepository.findAll();
    }

    @Override
    public Guarantor approveGuarantor(Long guarantorId) {
        Guarantor guarantor = guarantorRepository.findById(guarantorId)
                .orElseThrow(() -> new RuntimeException("Guarantor not found"));
        guarantor.setApprouved(true); // Mettre à jour l'attribut approved
        return guarantorRepository.save(guarantor); // Sauvegarder les modifications
    }
    @Override
    public List<Guarantor> NonApprouvedGuarantors()
    {return (List<Guarantor>) guarantorRepository.findAllByApprouvedFalse(); }

}
