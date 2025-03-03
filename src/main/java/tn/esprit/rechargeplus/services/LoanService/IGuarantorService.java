package tn.esprit.rechargeplus.services.LoanService;

import tn.esprit.rechargeplus.entities.Guarantor;

import java.io.IOException;
import java.util.List;

public interface IGuarantorService {
    Guarantor addGuarantor(Guarantor guarantor) throws IOException;
    Guarantor updateGuarantor(Guarantor guarantor);
    void remouveGuarantor(Long numGuarantor);
    Guarantor retrieveGuarantor(Long numGuarantor);
    List<Guarantor> retriveAll();
    Guarantor approveGuarantor(Long guarantorId);
    List<Guarantor> NonApprouvedGuarantors();
}
