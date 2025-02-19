package tn.esprit.rechargeplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.entities.Transaction_Status;
import tn.esprit.rechargeplus.service.TransactionService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;  // Injecting the TransactionService

    // Method to create a static transaction for testing
    public Transaction createStaticTransaction() {
        Transaction transaction = new Transaction();
        transaction.setIdTransaction(1L);  // Static ID
        try {
            // Parse the string into a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse("2025/02/18");
            transaction.setCreated_at(date);  // Set the date
        } catch (Exception e) {
            e.printStackTrace();
        }
        transaction.setSource("Test Source");
        transaction.setDestination("Test Destination");
        transaction.setAmount(100.0);  // Static amount
        transaction.setStatus(Transaction_Status.PENDING);  // Example status
        return transaction;
    }

    // Post method to create a transaction
    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.saveTransaction(createStaticTransaction()));
    }

    // Get all transactions
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // Get a transaction by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get a static transaction for testing purposes
    @GetMapping("/static")
    public ResponseEntity<Transaction> getStaticTransaction() {
        Transaction staticTransaction = createStaticTransaction();
        return ResponseEntity.ok(staticTransaction);
    }

    // Update a transaction by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable long id, @RequestBody Transaction transactionDetails) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDetails));
    }

    // Delete a transaction by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
