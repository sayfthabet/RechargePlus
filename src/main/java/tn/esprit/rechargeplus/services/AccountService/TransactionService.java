package tn.esprit.rechargeplus.services.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.rechargeplus.entities.*;
import tn.esprit.rechargeplus.repositories.AccountRepository.TransactionRepository;
import tn.esprit.rechargeplus.repositories.LoanRepository.ILoanRepository;
import tn.esprit.rechargeplus.repositories.LoanRepository.IRepaymentRepository;
import tn.esprit.rechargeplus.services.AccountService.exceptions.*;
import tn.esprit.rechargeplus.services.AccountService.exceptions.*;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service

public class TransactionService implements iTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final FraudDetectionService fraudService;
    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              FraudDetectionService fraudService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.fraudService = fraudService;
    }

    @Autowired
    ILoanRepository loanRepository;
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById( id) //"ACC-" +
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setStatus(transactionDetails.getStatus());
        transaction.setFee(transactionDetails.getFee());
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    // Helper method to check if two dates are on the same day
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }



    private double determineFeePercentage() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        // Peak hours: 9 AM to 5 PM -> 1.5% fee, otherwise 0.5%
        if (hour >= 9 && hour < 17) {
            return 0.015;
        } else {
            return 0.005;
        }}


    @Override
    @Transactional
    public Transaction transferBetweenAccounts(Long sourceAccountId, Long targetAccountId, double amount, String ipAddress) {
        Account source = accountService.retrieveAccountById(sourceAccountId);
        Account target = accountService.retrieveAccountById(targetAccountId);

        if (source == null || target == null) {
            throw new AccountNotFoundException("Source or target account not found");
        }

        // Fraud detection
        fraudService.checkForFraud(amount, ipAddress);

        // Calculate fee (1% fee)
        double fee = amount * determineFeePercentage();
        double totalAmount = amount + fee;

        // Check balance
        if (source.getAmount() < totalAmount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Check and update daily transaction limit for source account
        Date today = new Date();
        if (source.getLastTransactionDate() == null || !isSameDay(source.getLastTransactionDate(), today)) {
            source.setDailyTransactionTotal(0);
            source.setLastTransactionDate(today);
        }
        if (source.getDailyTransactionTotal() + amount > source.getDailyTransactionLimit()) {
            throw new DailyLimitExceededException("Daily transaction limit exceeded");
        }

        // Perform transfer
        source.setAmount(source.getAmount() - totalAmount);
        target.setAmount(target.getAmount() + amount);
        source.setDailyTransactionTotal(source.getDailyTransactionTotal() + amount);

        // Save updated accounts
        accountService.updateAccount(source);
        accountService.updateAccount(target);

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setSource("ACC-" + sourceAccountId);
        transaction.setDestination("ACC-" + targetAccountId);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setStatus(Transaction_Status.COMPLETED);
        transaction.setIpAddress(ipAddress);
        logger.info("Transfer completed from account {} to account {}", sourceAccountId, targetAccountId);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction reverseTransaction(Long transactionId, String reason) {
        Transaction original = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (original.isReversed()) {
            throw new AlreadyReversedException("Transaction already reversed");
        }

        // Reverse balances
        Account source = accountService.retrieveAccountById(original.getAccount().getId());
        Account target = accountService.retrieveAccountById(original.getDestinationAccountId());

        source.setAmount(source.getAmount() + original.getAmount() + original.getFee());
        target.setAmount(target.getAmount() - original.getAmount());

        // Save updated accounts
        accountService.updateAccount(source);
        accountService.updateAccount(target);

        // Mark original transaction as reversed
        original.setReversed(true);
        transactionRepository.save(original);

        // Create reversal transaction
        Transaction reversal = new Transaction();
        reversal.setOriginalTransaction(original);
        reversal.setAmount(original.getAmount());
        reversal.setFee(original.getFee());
        reversal.setStatus(Transaction_Status.REVERSED);
        reversal.setReversalReason(reason);
        reversal.setIpAddress(original.getIpAddress());
        logger.info("Transaction {} reversed", transactionId);
        return transactionRepository.save(reversal);
    }

  /*  @Override
    @Transactional
    public Transaction depositFunds(Long accountId, double amount, String ipAddress) {
        Account account = accountService.retrieveAccountById(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        // Fraud check for large deposits can be added
        fraudService.checkForFraud(amount, ipAddress);

        account.setAmount(account.getAmount() + amount);
        accountService.updateAccount(account);

        Transaction transaction = new Transaction();
        transaction.setSource("SYSTEM");
        transaction.setDestination("ACC-" + accountId);
        transaction.setAmount(amount);
        transaction.setFee(determineFeePercentage());
        transaction.setStatus(Transaction_Status.COMPLETED);
        transaction.setIpAddress(ipAddress);
        logger.info("Deposit of {} to account {}", amount, accountId);
        return transactionRepository.save(transaction);
    }
*/
    /*http://localhost:8082/RechargePlus1/api/transactions/withdraw?accountId=2&amount=1000&ipAddress=19643.1563.56
*/
    @Override
    @Transactional
    public Transaction withdrawFunds(Long accountId, double amount, String ipAddress) {
        Account account = accountService.retrieveAccountById(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        fraudService.checkForFraud(amount, ipAddress);

        if (account.getAmount() < amount) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        // Check and update daily limit
        Date today = new Date();
        if (account.getLastTransactionDate() == null || !isSameDay(account.getLastTransactionDate(), today)) {
            account.setDailyTransactionTotal(0);
            account.setLastTransactionDate(today);
        }
        if (account.getDailyTransactionTotal() + amount > account.getDailyTransactionLimit()) {
            throw new DailyLimitExceededException("Daily transaction limit exceeded");
        }

        account.setAmount(account.getAmount() - amount);
        account.setDailyTransactionTotal(account.getDailyTransactionTotal() + amount);
        accountService.updateAccount(account);

        Transaction transaction = new Transaction();
        transaction.setSource("ACC-" + accountId);
        transaction.setDestination("ACC-999");
        transaction.setAmount(amount);
        transaction.setFee(determineFeePercentage());
        transaction.setStatus(Transaction_Status.COMPLETED);
        transaction.setIpAddress(ipAddress);
        logger.info("Withdrawal of {} from account {}", amount, accountId);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByAccount(Long accountId) {
      //
         return transactionRepository.findByAccountId(accountId);
    }




    /// ////////////////AJOUT POUR loan ET REPAYMENTS!
    @Override
    @Transactional
    public  Transaction depositLoan(Long accountId, double amount, String ipAddress, Loan loan) {
        Account account = accountService.retrieveAccountById(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        // Fraud check for large deposits can be added
        fraudService.checkForFraud(amount, ipAddress);

        account.setAmount(account.getAmount() + amount);
        accountService.updateAccount(account);

        Transaction transaction = new Transaction();
        transaction.setSource("SYSTEM");
        transaction.setDestination("ACC-" + accountId);
        transaction.setAccount(accountService.retrieveAccountById(accountId));
        transaction.setAmount(amount);
        transaction.setFee(determineFeePercentage());
        transaction.setStatus(Transaction_Status.COMPLETED);
        transaction.setIpAddress(ipAddress);
        transaction.setLoan(loan);
        logger.info("Deposit of {} to account {}", amount, accountId);
        return transactionRepository.save(transaction);
    }
    @Transactional
    public Transaction depositFunds(Long accountId, double amount, String ipAddress) {
        Account account = accountService.retrieveAccountById(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        // Vérification anti-fraude
        fraudService.checkForFraud(amount, ipAddress);

        // Vérifier si le compte a des transactions liées à un prêt en cours ou en défaut
        List<Transaction> loanTransactions = transactionRepository.findByAccountIdAndLoanStatusIn(
                accountId, List.of(Loan_Status.IN_PROGRESS, Loan_Status.DEFAULT));
        double remainingAmount = amount;
        if (!loanTransactions.isEmpty()) {
            // Récupérer le prêt lié
            Loan loan = loanTransactions.get(0).getLoan(); // Supposons qu'un seul prêt actif existe par compte

            // Récupérer les remboursements en retard
            List<Repayment> lateRepayments = repaymentRepository.findByLoan_IdLoanAndStatus(loan.getIdLoan(), Repayment_Status.DEFAULT);
            // double remainingAmount = amount;
            if (!lateRepayments.isEmpty()) {
                logger.info("Processing overdue repayments for loan ID: {}", loan.getIdLoan());

                // double remainingAmount = amount;

                for (Repayment repayment : lateRepayments) {
                    if (remainingAmount <= 0) break; // Stop si plus de fonds disponibles

                    double monthlyAmount = repayment.getMonthly_amount();

                    if (remainingAmount >= monthlyAmount) {
                        // Couvrir entièrement le remboursement
                        Transaction transaction = withdrawFunds(accountId, monthlyAmount, ipAddress);

                        repayment.setStatus(Repayment_Status.REPAID_LATE);
                        repayment.setActualPaymentDate(java.sql.Date.valueOf(LocalDate.now()));
                        repaymentRepository.save(repayment);

                        remainingAmount -= monthlyAmount;
                    } else {
                        // Paiement partiel
                        Transaction transaction = withdrawFunds(accountId, remainingAmount, ipAddress);

                        repayment.setMonthly_amount(monthlyAmount - remainingAmount);
                        repayment.setStatus(Repayment_Status.DEFAULT);
                        repaymentRepository.save(repayment);

                        remainingAmount = 0; // Plus de fonds disponibles
                    }
                }

                // Mettre à jour le statut du prêt si tous les remboursements sont payés
                boolean allRepaymentsPaid = repaymentRepository.findByloanIdLoan(loan.getIdLoan())
                        .stream()
                        .allMatch(repayment -> repayment.getStatus() == Repayment_Status.REPAID || repayment.getStatus() == Repayment_Status.REPAID_LATE);

                if (allRepaymentsPaid) {
                    loan.setStatus(Loan_Status.REPAID_LATE);
                    loanRepository.save(loan);
                }
            }
        }

        // Ajouter le montant restant au compte après remboursements
        account.setAmount(account.getAmount() + remainingAmount);
        accountService.updateAccount(account);

        // Créer et sauvegarder la transaction de dépôt
        Transaction depositTransaction = new Transaction();
        depositTransaction.setSource("SYSTEM");
        depositTransaction.setDestination("ACC-" + accountId);
        depositTransaction.setAmount(amount);
        depositTransaction.setFee(determineFeePercentage());
        depositTransaction.setStatus(Transaction_Status.COMPLETED);
        depositTransaction.setIpAddress(ipAddress);
        logger.info("Deposit of {} to account {}", amount, accountId);

        return transactionRepository.save(depositTransaction);
    }

}
