package tn.esprit.rechargeplus.services.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.*;
import tn.esprit.rechargeplus.repositories.AccountRepository;
import tn.esprit.rechargeplus.repositories.LoanRepository.ILoanRepository;
import tn.esprit.rechargeplus.repositories.TransactionRepository;


import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreditScoreService implements ICreditScoreService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ILoanRepository loanRepository;
    /**
     * Méthode pour calculer le score de crédit.
     */

    public double calculateCreditScore(Long accountId) {


        double stabilityRechargesScore = calculateStabilityRechargesScore(accountId);
        double accountAgeScore = calculateAccountAgeScore(accountId);
        double paymentPunctualityScore  = calculatePaymentPunctualityScore(accountId);
        double totalScore = 0;

            totalScore = stabilityRechargesScore + accountAgeScore + paymentPunctualityScore;
        // Retourner le score total
        return totalScore;
    }

    /**
     * Vérifie si l'utilisateur a des prêts deja remboursés (Ancien Client).
     */
    public boolean hasExistingLoan(long accountId) {
        List<Loan> loans = loanRepository.findByTransactionsAccountIdAndStatus(accountId, Loan_Status.REPAID);
        return !loans.isEmpty();
    }
    private boolean hasNoRecharges(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdAndSourceIsLike(accountId, "SYSTEM");
        return transactions.isEmpty();
    }

    public boolean hasActiveLoans(Long accountId) {
        List<Loan> activeLoans = loanRepository.findByTransactionsAccountIdAndStatusIn(accountId,
                List.of(Loan_Status.IN_PROGRESS, Loan_Status.DEFAULT));
        return !activeLoans.isEmpty();
    }

    public String getLoanDecision(Long accountId) {
        double creditScore = calculateCreditScore(accountId);

        double NbActiveMonths = getActiveMonths(accountId).size();


        // Vérifier si le client a des crédits en cours (en attente ou non remboursés)
        boolean hasActiveLoans = hasActiveLoans(accountId);

        // Calculer la moyenne mensuelle des recharges en excluant les transactions suspectes
        double averageMonthlyRecharge = calculateAverageMonthlyRecharge(accountId);

        if (NbActiveMonths<3) {
            return String.format("🔴 Score : %.2f%% ❌ Pas de crédit - Compte n'est pas actif ou n'a pas effectué de recharge pendant 3mois", creditScore);
        }

        if (hasActiveLoans) {
            return String.format("🔴 Score : %.2f%% ❌ Pas de crédit - Le client a déjà un prêt en cours ou non encore remboursé.", creditScore);
        }

        if (creditScore <= 50) {
            return String.format("🔴 Score : %.2f%% ❌ Pas de crédit - Score insuffisant.", creditScore);
        }

        double creditLimit;


        if (creditScore >= 90) {
            creditLimit = 3 * averageMonthlyRecharge;

        } else if (creditScore >= 70) {
            creditLimit = 2 * averageMonthlyRecharge;

        } else {
            creditLimit = averageMonthlyRecharge;

        }
        if (creditScore >= 90) {
            return String.format("🔵 Score : %.2f%% ✅ Plafond : 300%% de la moyenne mensuelle de recharge soit  %.2f TND  ", creditScore, creditLimit);
        } else if (creditScore >= 70) {
            return String.format("🟢 Score : %.2f%% ✅ Plafond : 200%% de la moyenne mensuelle de recharge soit  %.2f TND  ", creditScore, creditLimit);
        } else {
            return String.format("🟠 Score : %.2f%% ✅ Plafond : 100%% de la moyenne mensuelle de recharge soit  %.2f TND ", creditScore, creditLimit);
        }

    }

  public List<Long> detectFraudulentManipulations(Long accountId) {
      List<Transaction> recharges = transactionRepository.findByAccountIdAndSourceIsLike(accountId, "SYSTEM");
      List<Transaction> withdrawals = transactionRepository.findByAccountIdAndDestinationIsLike(accountId, "SYSTEM");

      List<Long> fraudulentTransactionIds = new ArrayList<>();

      for (Transaction recharge : recharges) {
          for (Transaction withdrawal : withdrawals) {
              long daysBetween = ChronoUnit.DAYS.between(
                      recharge.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                      withdrawal.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
              );

              if (daysBetween >= 0 && daysBetween < 3) {
                  if (withdrawal.getAmount() >= 0.8 * recharge.getAmount()) {
                      // Ajouter l'ID des transactions suspectes
                      fraudulentTransactionIds.add(recharge.getIdTransaction());
                      fraudulentTransactionIds.add(withdrawal.getIdTransaction());
                  }
              }
          }
      }

      return fraudulentTransactionIds;
  }

    public double calculateAverageMonthlyRecharge(Long accountId) {
        // Récupérer toutes les recharges effectuées par le client
        List<Transaction> allRecharges = transactionRepository.findByAccountIdAndSourceIsLike(accountId, "SYSTEM");

        // Récupérer les transactions suspectes
        List<Long> fraudulentTransactionIds = detectFraudulentManipulations(accountId);

        // Filtrer les transactions suspectes
        List<Transaction> validRecharges = allRecharges.stream()
                .filter(recharge -> !fraudulentTransactionIds.contains(recharge.getIdTransaction()))
                .collect(Collectors.toList());

        if (validRecharges.isEmpty()) {
            return 0;
        }

        // Grouper par mois et calculer la moyenne mensuelle
        Map<YearMonth, Double> monthlySums = validRecharges.stream()
                .collect(Collectors.groupingBy(
                        recharge -> YearMonth.from(recharge.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Retourner la moyenne mensuelle
        return monthlySums.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * Calcule l'écart-type des montants d'une série de transactions.
     */
    private double calculateStandardDeviation(double[] amounts) {
        if (amounts.length == 0) {
            return -1; // Indique que la liste est vide
        }
        double mean = Arrays.stream(amounts).average().orElse(0);
        double variance = Arrays.stream(amounts)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }
    /**
     * Calcule la stabilité des recharges pour le client.
     */
    private double calculateStabilityRechargesScore(long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdAndSourceIsLike(accountId, "SYSTEM");
        // Calculer l'écart-type des montants des transactions pour déterminer la stabilité des recharges
        double[] amounts = transactions.stream().mapToDouble(Transaction::getAmount).toArray();
        double standardDeviation = calculateStandardDeviation(amounts);
        if (hasExistingLoan(accountId)) {
            // Si l'écart-type est faible, score élevé pour les clients ayant deja pris un pret
            if (standardDeviation <= 2000) {
                return 20; // Ancien Client : 20 pts max
            } else if (standardDeviation <= 3000) {
                return 10; // Ancien Client : 10 pts max
            } else {
                return 0; // Ancien Client : 0 pts si très irrégulier
            }
        }else {// Si l'écart-type est faible, score élevé pour les clients qui n'ont jamais pris de pret
            if (standardDeviation <= 2000 && standardDeviation >= 0) {
                return 40; // AClient : 20 pts max
            } else if (standardDeviation <= 3000 && standardDeviation >= 0) {
                return 20; // Ancien Client : 10 pts max
            } else {
                return 0; // Ancien Client : 0 pts si très irrégulier
            }

        }
    }
    /**
     * Récupère les mois durant lesquels le client a été actif (ex: recharges, paiements, transactions).
     */
    public Set<String> getActiveMonths(Long accountId) {
        Set<String> activeMonths = new HashSet<>();

        for (Transaction transaction : accountRepository.getReferenceById(accountId).getTransactions()) {
            if (Transaction_Status.COMPLETED.equals(transaction.getStatus())) {
                // Récupération du mois et de l'année de la transaction (ex: "2024-02")
                String monthYear = new SimpleDateFormat("yyyy-MM").format(transaction.getCreatedAt());
                activeMonths.add(monthYear);

            }
        }
        return activeMonths;
    }

    /**
     * Calcule l'ancienneté du compte en fonction des mois où le client a été actif.
     */
    private double calculateAccountAgeScore(Long accountId) {
       // long currentTime = System.currentTimeMillis();
       // long accountCreationTime = account.getCreatedAt().getTime();

        // Liste des mois durant lesquels le client a été actif
        Set<String> activeMonths = getActiveMonths(accountId); // Méthode qui récupère les mois actifs

        // Nombre total de mois actifs
        int activeMonthCount = activeMonths.size();
        if (hasExistingLoan(accountId))
        {//les clients ayant deja pris un pret
        if (activeMonthCount > 12) {
            return 30; // Plus de 12 mois actifs
        } else if (activeMonthCount > 6) {
            return 20; // Entre 6 et 12 mois actifs
        } else if (activeMonthCount > 3) {
            return 10; // Entre 3 et 6 mois actifs
        } else {
            return 0; // Moins de 3 mois actifs
        }}else {
           // les clients qui n'ont jamais pris de pret
            if (activeMonthCount > 12) {
            return 60; // Plus de 12 mois actifs
        } else if (activeMonthCount > 6) {
            return 50; // Entre 6 et 12 mois actifs
        } else if (activeMonthCount >= 3) {
            return 40; // Entre 3 et 6 mois actifs
        } else {
            return 0; // Moins de 3 mois actifs
        }
        }
    }

    /**
     * Calcule le score de paiement ponctuel pour un ancien client.
     */
    private double calculatePaymentPunctualityScore(long accountId) {
        List<Loan> loans = loanRepository.findByTransactionsAccountId(accountId);
        int latePaymentsCount = 0;
        if ( loans.isEmpty()) {
            return 0; // Indique que la liste est vide
        }

        // Vérifier les 3 derniers paiements de chaque prêt
        for (Loan loan : loans) {
            List<Repayment> repayments = loan.getRepayments();

            // Trier les remboursements par date (du plus récent au plus ancien)
            repayments.sort(Comparator.comparing(Repayment::getExpectedPaymentDate).reversed());

            // Prendre les 3 derniers paiements
            List<Repayment> lastThreeRepayments = repayments.stream()
                    .limit(3) // On prend seulement les 3 plus récents
                    .toList();

            // Compter les paiements en retard parmi ces 3 derniers
            for (Repayment repayment : lastThreeRepayments) {
                if (repayment.getStatus() == Repayment_Status.REPAID_LATE) {
                    latePaymentsCount++;
                }
            }
        }
        // Calcul du score selon le nombre de paiements en retard sur les 3 derniers
        if (latePaymentsCount == 0) {
            return 50; // Aucun retard de paiement
        } else if (latePaymentsCount == 1) {
            return 40; // Un retard parmi les 3 derniers paiements
        } else if (latePaymentsCount == 2) {
            return 30; // deux retards parmi les 3 derniers paiements
        } else {
            return 0; // trois retards ou plus
        }
    }



}

