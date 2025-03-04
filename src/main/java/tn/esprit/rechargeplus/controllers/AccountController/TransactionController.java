package tn.esprit.rechargeplus.controllers.AccountController;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.services.AccountService.iTransactionService;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
@Validated // Activation of parameter validation
public class TransactionController {
@Autowired
    private  iTransactionService transactionService;

    /**
     * Save a new transaction.
     */
    @PostMapping
    public ResponseEntity<Transaction> saveTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionService.saveTransaction(transaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    /**
     * Get a transaction by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    /**
     * Get all transactions.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Update an existing transaction.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transactionDetails) {

        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        if (updatedTransaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTransaction);
    }

    /**
     * Delete a transaction by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Transfer funds between two accounts.
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(
            @RequestParam Long sourceAccountId,
            @RequestParam Long targetAccountId,
            @RequestParam double amount,
            @RequestParam String ipAddress) {

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(null); // Validation for positive amount
        }

        Transaction transaction = transactionService.transferBetweenAccounts(sourceAccountId, targetAccountId, amount, ipAddress);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    /**
     * Reverse a transaction.
     */
    @PostMapping("/reverse/{transactionId}")
    public ResponseEntity<Transaction> reverseTransaction(
            @PathVariable Long transactionId,
            @RequestParam String reason) {

        if (reason == null || reason.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Validation for non-empty reason
        }

        Transaction reversal = transactionService.reverseTransaction(transactionId, reason);
        if (reversal == null) {
            return ResponseEntity.notFound().build(); // If the transaction to reverse is not found
        }
        return new ResponseEntity<>(reversal, HttpStatus.OK);
    }

    /**
     * Deposit funds into an account.
     */
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> depositFunds(
            @RequestParam Long accountId,
            @RequestParam double amount,
            @RequestParam String ipAddress) {

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(null); // Validation for positive amount
        }

        Transaction transaction = transactionService.depositFunds(accountId, amount, ipAddress);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    /**
     * Withdraw funds from an account.
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdrawFunds(
            @RequestParam Long accountId,
            @RequestParam double amount,
            @RequestParam String ipAddress) {

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(null); // Validation for positive amount
        }

        Transaction transaction = transactionService.withdrawFunds(accountId, amount, ipAddress);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    /**
     * Get transactions by account ID.
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }
}