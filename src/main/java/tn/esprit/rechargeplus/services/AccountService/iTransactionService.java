package tn.esprit.rechargeplus.services.AccountService;

import tn.esprit.rechargeplus.entities.Transaction;
import java.util.List;

public interface iTransactionService {
    Transaction saveTransaction(Transaction transaction);
    Transaction getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    Transaction updateTransaction(Long id, Transaction transactionDetails);
    void deleteTransaction(Long id);
    Transaction transferBetweenAccounts(Long sourceAccountId, Long targetAccountId, double amount, String ipAddress);
    Transaction reverseTransaction(Long transactionId, String reason);

    // New functions:
    Transaction depositFunds(Long accountId, double amount, String ipAddress);
    Transaction withdrawFunds(Long accountId, double amount, String ipAddress);
    List<Transaction> getTransactionsByAccount(Long accountId);
}
