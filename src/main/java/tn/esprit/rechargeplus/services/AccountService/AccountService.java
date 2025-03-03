package tn.esprit.rechargeplus.services.AccountService;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Account;
import tn.esprit.rechargeplus.repositories.AccountRepository.AccountRepository;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountService implements IAccountService {
    @Autowired
    private  AccountRepository accountRepository;

    @Override
    public Account addAccount(Account account) {
        account.setCreated_at(new Date());
        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(Account account) {
        account.setUpdated_at(new Date());
        return accountRepository.save(account);
    }

    @Override
    public void removeAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public Account retrieveAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public List<Account> retrieveAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account updateAccountBalance(Long accountId, double amountDelta) {
        Account account = retrieveAccountById(accountId);
        account.setAmount(account.getAmount() + amountDelta);
        return accountRepository.save(account);
    }
}
