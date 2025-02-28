package tn.esprit.rechargeplus.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Loan_Status;
import tn.esprit.rechargeplus.entities.Transaction_Status;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.entities.Repayment_Status;
import tn.esprit.rechargeplus.entities.Repayment;
import tn.esprit.rechargeplus.repositories.ILoanRepository;
import tn.esprit.rechargeplus.entities.Loan;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.rechargeplus.repositories.IRepaymentRepository;


@Service
@RequiredArgsConstructor
public class LoanService  implements  ILoanService {
    @Autowired
     ILoanRepository loanRepository;
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Autowired
    CreditScoreService creditScoreService;
    @Autowired
    TransactionService transactionService;
    @Override
    public Loan addLoan(Loan loan) {
        return loanRepository.save(loan);
    }
    @Override
    public Loan updateLoan(Loan loan) {return loanRepository.save(loan);}
    @Override
    public void remouveLoan(Long numLoan) {
        loanRepository.deleteById(numLoan);
    }
    @Override
    public Loan retrieveLoan(Long numLoan) {
        return loanRepository.findById(numLoan).orElse(null);
    }
    @Override
    public List<Loan> retriveAll() {
        return (List<Loan>) loanRepository.findAll();
    }

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class); // Logger pour la classe LoanService



        @Override
        public  int calculateAnnuityDuration(double P, double Rm, double i) {
            int N = 1;
            int maxDuration = 240; // Par exemple, limiter à 20 ans (240 mois)

            while (N <= maxDuration) {
                double A = (P * i /12/ 100) / (1 - Math.pow(1 + i /12/ 100, -N)); // Formule de l'annuité
                if (A <= Rm) {
                    return N;
                }
                N++;
            }

            log.warn("⚠️ Impossible de calculer une durée viable pour l'annuité (N > {}).", maxDuration);
            throw new IllegalArgumentException("Impossible de trouver une durée réaliste pour le remboursement par annuité.");
        }
    @Override
    public  int calculateAmortizationDuration(double P, double Rm, double i) {
        int N = 1;
        int maxDuration = 240; // Par exemple, limiter à 20 ans (240 mois)

        while (N <= maxDuration) {
            double firstPayment = (P / N) + (P * i/12 / 100); // Première mensualité (plus haute)
            if (firstPayment <= Rm) {
                return N;
            }
            N++;
        }

        log.warn("⚠️ Impossible de calculer une durée viable pour l'amortissement (N > {}).", maxDuration);
        throw new IllegalArgumentException("Impossible de trouver une durée réaliste pour le remboursement par amortissement.");
    }


    @Override
    public List<Map<String, Object>> calculateConstantAnnuity(double loanAmount, double interestRate, double duration) {
        List<Map<String, Object>> schedule = new ArrayList<>();

        BigDecimal loan = BigDecimal.valueOf(loanAmount);
        BigDecimal rate = BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_EVEN);
        BigDecimal annuity = loan.multiply(rate)
                .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.add(rate).pow(- (int) duration, new MathContext(10))
                ), 2, RoundingMode.HALF_EVEN);

        BigDecimal remainingBalance = loan;

        for (int month = 1; month <= duration; month++) {
            BigDecimal interest = remainingBalance.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal principal = annuity.subtract(interest).setScale(2, RoundingMode.HALF_EVEN);

            // Vérification du dernier paiement pour éviter un solde négatif
            if (month == duration) {
                principal = remainingBalance; // Ajuste le dernier paiement
                annuity = interest.add(principal); // Ajuste l'annuité finale
                remainingBalance = BigDecimal.ZERO; // Le prêt doit être soldé exactement
            } else {
                remainingBalance = remainingBalance.subtract(principal).setScale(2, RoundingMode.HALF_EVEN);
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("Mois", month);
            entry.put("Mensualité", annuity);
            entry.put("Capital Remboursé", principal);
            entry.put("Intérêts", interest);
            entry.put("Capital Restant", remainingBalance);
            schedule.add(entry);
        }
        return schedule;
    }


    @Override
    public List<Map<String, Object>> calculateConstantAmortization(double loanAmount, double interestRate, double duration) {
        List<Map<String, Object>> schedule = new ArrayList<>();

        BigDecimal loan = BigDecimal.valueOf(loanAmount);
        BigDecimal rate = BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_EVEN);
        BigDecimal principalPayment = loan.divide(BigDecimal.valueOf(duration), 2, RoundingMode.HALF_EVEN);
        BigDecimal remainingBalance = loan;

        for (int month = 1; month <= duration; month++) {
            BigDecimal interest = remainingBalance.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal monthlyPayment = principalPayment.add(interest).setScale(2, RoundingMode.HALF_EVEN);

            // Vérification du dernier paiement pour éviter un solde négatif
            if (month == duration) {
                principalPayment = remainingBalance; // Ajuste le dernier paiement
                monthlyPayment = principalPayment.add(interest); // Ajuste la mensualité finale
                remainingBalance = BigDecimal.ZERO; // Le prêt doit être soldé exactement
            } else {
                remainingBalance = remainingBalance.subtract(principalPayment).setScale(2, RoundingMode.HALF_EVEN);
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("Mois", month);
            entry.put("Mensualité", monthlyPayment);
            entry.put("Capital Remboursé", principalPayment);
            entry.put("Intérêts", interest);
            entry.put("Capital Restant", remainingBalance);
            schedule.add(entry);
        }
        return schedule;
    }

    // Génération du planning de remboursement avec annuités constantes
    public static List<Double> generateAnnuitySchedule(double P, int N, double i) {
        List<Double> schedule = new ArrayList<>();
        double A = (P * i/12/ 100) / (1 - Math.pow(1 + i/12/ 100, -N)); // Annuité constante

        for (int k = 0; k < N; k++) {
            schedule.add(A);
        }
        return schedule;
    }

    // Génération du planning de remboursement avec amortissement constant
    public static List<Double> generateAmortizationSchedule(double P, int N, double i) {
        List<Double> schedule = new ArrayList<>();
        double capitalAmortized = P / N;

        for (int k = 0; k < N; k++) {
            double interest = (P - (k * capitalAmortized)) * i/12/ 100;
            double monthlyPayment = capitalAmortized + interest;
            schedule.add(monthlyPayment);
        }
        return schedule;
    }


public double determineInterestRate(Long accountId, double creditScore) {
    boolean isNewClient = !creditScoreService.hasExistingLoan(accountId);
    double interestRate;

    // Scraper la TMM depuis le lien
    double tmm = getTMMFromBCT();

    if (isNewClient) {
        // Client qui n'a jamais pris de prêt
        if (creditScore >= 90) {
            interestRate = 6.0 + tmm;
        } else if (creditScore >= 70) {
            interestRate = 7.0 + tmm;
        } else {
            interestRate = 8.0 + tmm;
        }
    } else {
        // Client ayant déjà pris un prêt
        if (creditScore >= 90) {
            interestRate = 2.0 + tmm;
        } else if (creditScore >= 70) {
            interestRate = 3.0 + tmm;
        } else {
            interestRate = 5.0 + tmm;
        }
    }

    return interestRate;
}
//Scraping de la TMM dynamique
    private double getTMMFromBCT() {
        try {
            // Connexion à la page et extraction du contenu
            Document doc = Jsoup.connect("https://www.bct.gov.tn/bct/siteprod/stat_page.jsp?id=129").get();

            // Utilisation du sélecteur CSS spécifique pour récupérer la TMM
            Element tmmElement = doc.select("#PSR_print2 > table > tbody > tr:nth-child(10) > td:nth-child(2) > p").first();

            if (tmmElement != null) {
                String tmmText = tmmElement.text().trim().replace(",", ".");
                double tmm = Double.parseDouble(tmmText);

                // Log la TMM récupérée pour vérification
                logger.info("TMM récupéré : " + tmm);

                return tmm; // Retourner la TMM
            } else {
                logger.warn("L'élément TMM n'a pas été trouvé sur la page.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du scraping de la TMM", e);
        }

        return 7.99; // Retourner 7.99 en cas d'erreur ou si l'élément n'est pas trouvé
    }

  private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    public Map<String, Object> getLoanRepaymentPlan(Long accountId, double requestedAmount, int requestedDuration) {
        log.info("➡️ Début getLoanRepaymentPlan pour accountId={} montant={} durée={}", accountId, requestedAmount, requestedDuration);

        double creditScore = creditScoreService.calculateCreditScore(accountId);
        log.info("✅ Credit Score récupéré : {}", creditScore);

        double averageMonthlyRecharge = creditScoreService.calculateAverageMonthlyRecharge(accountId);
        log.info("✅ Recharge moyenne récupérée : {}", averageMonthlyRecharge);

        boolean hasActiveLoans = creditScoreService.hasActiveLoans(accountId);
        log.info("✅ Le client a-t-il des prêts actifs ? {}", hasActiveLoans);

        double interestRate = determineInterestRate(accountId, creditScore);
        log.info("✅ Taux d'intérêt déterminé : {}", interestRate);

        // Vérification des conditions d'éligibilité
        if (hasActiveLoans) {
            log.warn("❌ Le client a déjà un prêt en cours.");
            throw new IllegalArgumentException("❌ Pas de crédit - Le client a déjà un prêt en cours.");
        }

        if (creditScore < 50) {
            log.warn("❌ Score insuffisant : {}", creditScore);
            throw new IllegalArgumentException("❌ Pas de crédit - Score insuffisant.");
        }

        int activeMonths = creditScoreService.getActiveMonths(accountId).size();
        log.info("✅ Nombre de mois actifs : {}", activeMonths);

        if (activeMonths < 3) {
            log.warn("❌ Compte non actif pendant 3 mois.");
            throw new IllegalArgumentException("❌ Pas de crédit - Compte pas actif depuis 3 mois.");
        }

        // Déterminer le plafond du prêt
        double creditLimit;
        if (creditScore >= 90) {
            creditLimit = 3 * averageMonthlyRecharge;
        } else if (creditScore >= 70) {
            creditLimit = 2 * averageMonthlyRecharge;
        } else {
            creditLimit = averageMonthlyRecharge;
        }
        log.info("✅ Plafond du prêt calculé : {} TND", creditLimit);

        if (requestedAmount > creditLimit) {
            log.warn("🔴 Montant demandé dépasse le plafond : {}", creditLimit);
            throw new IllegalArgumentException("🔴 Montant demandé dépasse le plafond autorisé selon votre score  : " + creditLimit + " TND");
        }

        // Calcul des durées minimales
        int minAnnuityDuration = calculateAnnuityDuration(requestedAmount, averageMonthlyRecharge, interestRate);
        int minAmortizationDuration = calculateAmortizationDuration(requestedAmount, averageMonthlyRecharge, interestRate);
        int minRequiredDuration = Math.max(minAnnuityDuration, minAmortizationDuration);
        log.info("✅ Durée minimale requise calculée : {} mois", minRequiredDuration);

        if (requestedDuration < minRequiredDuration) {
            log.warn("🔴 Durée demandée inférieure à la durée minimale requise : {}", minRequiredDuration);
            throw new IllegalArgumentException("🔴 Durée minimale requise : " + minRequiredDuration + " mois");
        }

        // Générer le planning de remboursement
        log.info("⏳ Génération du plan de remboursement...");
        List<Map<String, Object>> annuitySchedule = calculateConstantAnnuity(requestedAmount, interestRate, requestedDuration);
        log.info("✅ Plan Annuités Constantes généré.");
        List<Map<String, Object>> amortizationSchedule = calculateConstantAmortization(requestedAmount, interestRate, requestedDuration);
        log.info("✅ Plan Amortissement Constant généré.");

        // Retourner les résultats
        Map<String, Object> result = new HashMap<>();
        // result.put("Plafond maximum", creditLimit);
        // result.put("Durée minimale requise", minRequiredDuration);
        result.put("🟢 Montant accordé", requestedAmount);
        result.put("Durée", requestedDuration);
        result.put("Taux d'intérêt", interestRate);
        result.put("Plan Annuités Constantes", annuitySchedule);
        result.put("Plan Amortissement Constant", amortizationSchedule);

        log.info("✅ Réponse générée avec succès pour accountId={}", accountId);
        return result;
    }

    public Loan createLoan(Long accountId, double requestedAmount, int requestedDuration, String repaymentType) {
        log.info("➡️ Début createLoan pour accountId={} montant={} durée={}", accountId, requestedAmount, requestedDuration);

        // Appel de la fonction pour obtenir le plan de remboursement
        Map<String, Object> repaymentPlan = getLoanRepaymentPlan(accountId, requestedAmount, requestedDuration);

        // Extraire les informations nécessaires depuis le plan de remboursement
        double grantedAmount = ((BigDecimal) repaymentPlan.get("🟢 Montant accordé")).doubleValue();
        double interestRate = ((BigDecimal) repaymentPlan.get("Taux d'intérêt")).doubleValue();
        // List<Map<String, Object>> annuitySchedule = (List<Map<String, Object>>) repaymentPlan.get("Plan Annuités Constantes");
       // List<Map<String, Object>> amortizationSchedule = (List<Map<String, Object>>) repaymentPlan.get("Plan Amortissement Constant");
        List<Map<String, Object>> selectedRepaymentSchedule = new ArrayList<>();
        log.info("📊 Contenu de repaymentPlan: {}", repaymentPlan);
        log.info("🔑 Clés disponibles dans repaymentPlan: {}", repaymentPlan.keySet());

        String repaymentKey = repaymentType.equalsIgnoreCase("annuity") ? "Plan Annuités Constantes" :
                repaymentType.equalsIgnoreCase("amortization") ? "Plan Amortissement Constant" : null;

        if (repaymentKey != null && repaymentPlan.containsKey(repaymentKey)) {
            selectedRepaymentSchedule = (List<Map<String, Object>>) repaymentPlan.get(repaymentKey);
            log.info("✅ Plan '{}' sélectionné.", repaymentKey);
        } else {
            log.error("❌ Type de remboursement invalide : {}", repaymentType);
            throw new IllegalArgumentException("❌ Type de remboursement invalide. Choisissez entre 'annuity' ou 'amortization'.");
        }

        // Création du prêt
        Loan loan = new Loan();
        loan.setAmount(grantedAmount);
        loan.setDuration(requestedDuration);
        loan.setInterestRate(interestRate);
       // loan.setAccountId(accountId); // L'ID du compte de l'utilisateur
        loan.setStatus(Loan_Status.IN_PROGRESS); // Statut initial du prêt
        loan.setRequest_date(new Date());

        // Enregistrement du prêt dans la base de données
        loanRepository.save(loan);
        log.info("✅ Prêt créé avec succès pour accountId={}", accountId);
        LocalDate loanStartDate = LocalDate.now(); // Ou utilisez loan.getRequest_date() si la date est déjà définie
        LocalDate nextPaymentDate = loanStartDate.plusMonths(1); // Premier paiement le mois suivant

        // Traitement des remboursements
        List<Repayment> repayments = new ArrayList<>();
        double remainingPrincipal = grantedAmount;

        // Parcours du plan d'annuité constante pour créer les objets Repayment
        for (Map<String, Object> annuity : selectedRepaymentSchedule) {
            Repayment repayment = new Repayment();

            // Convertir LocalDate en Date avant de l'affecter à expectedPaymentDate
            Date expectedDate = Date.from(nextPaymentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            repayment.setExpectedPaymentDate(expectedDate);

            repayment.setMonthly_amount((double) annuity.get("Mensualité"));
            repayment.setInterest((double) annuity.get("Intérêts"));
            repayment.setRemainingPrincipal(remainingPrincipal);
            repayment.setRepaidPrincipal((double) annuity.get("Capital Remboursé"));
            repayment.setStatus(Repayment_Status.IN_PROGRESS); // Statut initial
            repayment.setLoan(loan); // Associer le remboursement au prêt

            // Calcul du capital restant après ce remboursement
            remainingPrincipal -= (double) annuity.get("Capital Remboursé");

            repayments.add(repayment);

            // Incrémenter la date de paiement pour le prochain remboursement
            nextPaymentDate = nextPaymentDate.plusMonths(1);
        }

        // Sauvegarder les remboursements associés au prêt
        repaymentRepository.saveAll(repayments);
        log.info("✅ Remboursements enregistrés avec succès pour le prêt {}", loan.getIdLoan());
        Loan loan1 = loanRepository.findById(loan.getIdLoan()).orElse(null);

        // Enregistrer la transaction du prêt
      //  ITransactionService iTransactionService = new ITransactionServiceImpl();  // ou autre implémentation concrète

        transactionService.depositLoan(accountId, grantedAmount, "192.168.1.1", loan1);
        log.info("✅ Transaction pour le prêt enregistrée.");

        return loan;
    }





}


