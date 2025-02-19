package tn.esprit.rechargeplus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction saveTransaction(Transaction transaction) {
        try {
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving transaction");
        }
    }


    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(long id) {
        return transactionRepository.findById(id);
    }

    public Transaction updateTransaction(long id, Transaction transactionDetails) {
        return transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setCreated_at(transactionDetails.getCreated_at());
                    transaction.setSource(transactionDetails.getSource());
                    transaction.setDestination(transactionDetails.getDestination());
                    transaction.setAmount(transactionDetails.getAmount());
                    transaction.setStatus(transactionDetails.getStatus());
                    transaction.setLoan(transactionDetails.getLoan());
                    transaction.setAccount(transactionDetails.getAccount());
                    transaction.setInvestment_request(transactionDetails.getInvestment_request());
                    return transactionRepository.save(transaction);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public void deleteTransaction(long id) {
        transactionRepository.deleteById(id);
    }
}
