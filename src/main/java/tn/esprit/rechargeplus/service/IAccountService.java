package tn.esprit.rechargeplus.service;

import tn.esprit.rechargeplus.entities.Account;
import java.util.List;

public interface IAccountService {
    Account addAccount(Account account);
    Account updateAccount(Account account);
    void removeAccount(Long id);
    Account retrieveAccountById(Long id);
    List<Account> retrieveAllAccounts();
    Account updateAccountBalance(Long accountId, double amountDelta);
}
