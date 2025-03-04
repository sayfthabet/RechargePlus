package tn.esprit.rechargeplus.controllers.AccountController;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Account;
import tn.esprit.rechargeplus.services.AccountService.IAccountService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private  IAccountService accountService;
    /*http://localhost:8082/RechargePlus1/api/accounts*/
/*{
    "amount": 10000.50,
    "status": "ACTIVE",
    "created_at": "2025-03-04T10:30:00",
    "updated_at": "2025-03-04T11:00:00",
    "type": "SAVINGS",
    "dailyTransactionLimit": 5000.00,
    "dailyTransactionTotal": 1500.00,
    "lastTransactionDate": "2025-03-03T15:45:00",
    "user": {
        "id": 1
    }
}
*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        return accountService.updateAccount(account);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        accountService.removeAccount(id);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.retrieveAccountById(id);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.retrieveAllAccounts();
    }
}
