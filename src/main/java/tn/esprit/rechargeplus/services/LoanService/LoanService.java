package tn.esprit.rechargeplus.services.LoanService;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.*;
import tn.esprit.rechargeplus.repositories.LoanRepository.IGuarantorRepository;
import tn.esprit.rechargeplus.repositories.LoanRepository.ILoanRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
//import javax.activation.DataHandler;
import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;


import com.itextpdf.layout.element.Image;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
//import javax.activation.*;


import org.slf4j.Logger;
import tn.esprit.rechargeplus.repositories.LoanRepository.IRepaymentRepository;
import tn.esprit.rechargeplus.services.AccountService.TransactionService;


@Service
@RequiredArgsConstructor

public class LoanService implements ILoanService {



    @Autowired
    ILoanRepository loanRepository;
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Autowired
    IGuarantorRepository guarantorRepository;
    @Autowired
    CreditScoreService creditScoreService;
    @Autowired
    TransactionService transactionService;


    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class); // Logger pour la classe LoanService


    @Override
    public Loan addLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Loan updateLoan(Loan loan) {
        return loanRepository.save(loan);
    }

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


    @Override
    public int calculateAnnuityDuration(double P, double Rm, double i) {
        int N = 1;
        int maxDuration = 240; // Par exemple, limiter à 20 ans (240 mois)

        while (N <= maxDuration) {
            double A = (P * i / 12 / 100) / (1 - Math.pow(1 + i / 12 / 100, -N)); // Formule de l'annuité
            if (A <= Rm) {
                return N;
            }
            N++;
        }

        log.warn("⚠️ Impossible de calculer une durée viable pour l'annuité (N > {}).", maxDuration);
        throw new IllegalArgumentException("Impossible de trouver une durée réaliste pour le remboursement par annuité.");
    }

    @Override
    public int calculateAmortizationDuration(double P, double Rm, double i) {
        int N = 1;
        int maxDuration = 240; // Par exemple, limiter à 20 ans (240 mois)

        while (N <= maxDuration) {
            double firstPayment = (P / N) + (P * i / 12 / 100); // Première mensualité (plus haute)
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
                .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.add(rate).pow(-(int) duration, new MathContext(10))
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
        double A = (P * i / 12 / 100) / (1 - Math.pow(1 + i / 12 / 100, -N)); // Annuité constante

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
            double interest = (P - (k * capitalAmortized)) * i / 12 / 100;
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
            org.jsoup.nodes.Document doc = Jsoup.connect("https://www.bct.gov.tn/bct/siteprod/stat_page.jsp?id=129").get();

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

    @Override
    public Map<String, Object> getLoanRepaymentPlan(Long accountId, double requestedAmount, double requestedDuration) {

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

    private double toDouble(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();  // Gère aussi Integer, Float, etc.
        } else {
            throw new IllegalArgumentException("Type non supporté : " + value.getClass().getName());
        }
    }
    @Override
    public Loan createLoan(Long accountId, double requestedAmount, int requestedDuration, String repaymentType,Long guarantorId) {
        log.info("➡️ Début createLoan pour accountId={} montant={} durée={}", accountId, requestedAmount, requestedDuration);

        // Appel de la fonction pour obtenir le plan de remboursement
        Map<String, Object> repaymentPlan = getLoanRepaymentPlan(accountId, requestedAmount, requestedDuration);

        // Affichage du contenu du plan de remboursement et des clés
        log.info("📊 Contenu de repaymentPlan: {}", repaymentPlan);
        log.info("🔑 Clés disponibles dans repaymentPlan: {}", repaymentPlan.keySet());

        // Extraire les informations nécessaires depuis le plan de remboursement
        Object grantedAmountObj = repaymentPlan.get("🟢 Montant accordé");
        Object interestRateObj = repaymentPlan.get("Taux d'intérêt");

        // Logs pour observer les objets et leurs types
        log.info("🔍 grantedAmountObj: {} | Type: {}", grantedAmountObj, grantedAmountObj.getClass().getName());
        log.info("🔍 interestRateObj: {} | Type: {}", interestRateObj, interestRateObj.getClass().getName());


        double grantedAmount = toDouble(repaymentPlan.get("🟢 Montant accordé"));
        double interestRate = toDouble(repaymentPlan.get("Taux d'intérêt"));

        // Traitement du type de remboursement
        List<Map<String, Object>> selectedRepaymentSchedule = new ArrayList<>();
        String repaymentKey = repaymentType.equalsIgnoreCase("annuity") ? "Plan Annuités Constantes" :
                repaymentType.equalsIgnoreCase("amortization") ? "Plan Amortissement Constant" : null;

        if (repaymentKey != null && repaymentPlan.containsKey(repaymentKey)) {
            selectedRepaymentSchedule = (List<Map<String, Object>>) repaymentPlan.get(repaymentKey);
            log.info("✅ Plan '{}' sélectionné.", repaymentKey);
        } else {
            log.error("❌ Type de remboursement invalide : {}", repaymentType);
            throw new IllegalArgumentException("❌ Type de remboursement invalide. Choisissez entre 'annuity' ou 'amortization'.");
        }
// 🔍 Récupérer le garant
        Guarantor guarantor = guarantorRepository.findGuarantorById(guarantorId)
                .orElseThrow(() -> new RuntimeException("❌ Garant non trouvé avec l'ID: " + guarantorId));


// ✅ Vérifier si le garant est approuvé
        if (!guarantor.getApprouved()) {
            log.error("❌ Le garant {} n'est pas approuvé.", guarantor.getId());
            throw new RuntimeException("Le garant n'est pas approuvé.");
        }


        BigDecimal monthlyInstallment = (BigDecimal) selectedRepaymentSchedule.get(0).get("Mensualité"); // Récupérer la 1ère mensualité

        if (Double.compare(guarantor.getMonthlyIncome(), monthlyInstallment.doubleValue()) <= 0) {
            log.error("❌ Le garant {} n'a pas un revenu suffisant pour couvrir la mensualité de {}.", guarantor.getId(), monthlyInstallment);
            throw new RuntimeException("Le garant ne peut pas couvrir la mensualité du prêt.");
        }

        log.info("✅ Garant {} validé avec un revenu mensuel de {}", guarantor.getId(), guarantor.getMonthlyIncome());

        // Création du prêt
        Loan loan = new Loan();
        loan.setAmount(grantedAmount);
        loan.setDuration(requestedDuration);
        loan.setInterestRate(interestRate);
        loan.setStatus(Loan_Status.IN_PROGRESS); // Statut initial du prêt
        loan.setRequest_date(new Date());
        loan.setGuarantor(guarantor);
        loan.setRemaining_repayment(grantedAmount);
        // Enregistrement du prêt dans la base de données
        loanRepository.save(loan);
        // Générer le document PDF après que l'ID soit généré
        try {
            // Générer le document PDF en utilisant l'ID du prêt
            loan.setLoanPdf(generateLoanDocument(loan.getIdLoan()));
        } catch (IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console
            throw new RuntimeException("Erreur lors de la génération du document PDF", e);
        }

// Mettre à jour le prêt dans la base de données avec le PDF généré
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
            repayment.setMonthly_amount(((BigDecimal) annuity.get("Mensualité")).setScale(3, RoundingMode.HALF_UP).doubleValue());
            repayment.setInterest(((BigDecimal) annuity.get("Intérêts")).setScale(3, RoundingMode.HALF_UP).doubleValue());
            remainingPrincipal -= ((BigDecimal) annuity.get("Capital Remboursé")).setScale(3, RoundingMode.HALF_UP).doubleValue();
            repayment.setRemainingPrincipal(new BigDecimal(remainingPrincipal).setScale(3, RoundingMode.HALF_UP).doubleValue());
            repayment.setRepaidPrincipal(((BigDecimal) annuity.get("Capital Remboursé")).setScale(3, RoundingMode.HALF_UP).doubleValue());
            repayment.setStatus(Repayment_Status.IN_PROGRESS); // Statut initial
            repayment.setLoan(loan); // Associer le remboursement au prêt
            // Calcul du capital restant après ce remboursement

            repayments.add(repayment);

            // Incrémenter la date de paiement pour le prochain remboursement
            nextPaymentDate = nextPaymentDate.plusMonths(1);
        }

        // Sauvegarder les remboursements associés au prêt
        repaymentRepository.saveAll(repayments);
        log.info("✅ Remboursements enregistrés avec succès pour le prêt {}", loan.getIdLoan());

        Loan loan1 = loanRepository.findById(loan.getIdLoan()).orElse(null);

        // Enregistrer la transaction du prêt
        transactionService.depositLoan(accountId, grantedAmount, "192.168.1.2", loan1);
        log.info("✅ Transaction pour le prêt enregistrée.");
        // Appel du service d'envoi d'email après création du prêt
        try {
            sendLoanEmail("rihabc184@gmail.com", loan.getIdLoan());
            System.out.println("tried sending");
        } catch (MessagingException | IOException e) {
            // Gérer l'exception ici, comme logger l'erreur
            e.printStackTrace();  // Exemple : afficher l'exception dans la console
        }

        return loan;
    }

    public byte[] generateLoanDocument(Long loanId) throws java.io.IOException {
        // Récupérer le prêt
        tn.esprit.rechargeplus.entities.Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        // Créer un document PDF avec iText 7
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

        // Charger l'image du logo (assurez-vous que le chemin est correct)
        String logoPathRight = "C:\\Users\\Rihab\\Downloads\\RechargePlus.png";  // logo à droite
        String logoPathLeft = "C:\\Users\\Rihab\\Downloads\\EspritLogo.png"; // logo à gauche

// Logo à droite
        Image logoRight = new Image(ImageDataFactory.create(logoPathRight));
        logoRight.setFixedPosition(pdfDoc.getDefaultPageSize().getWidth() - 170, pdfDoc.getDefaultPageSize().getTop() - 120); // Positionne le logo à 150px du bord droit et à 100px du haut de la page
        logoRight.scaleToFit(140, 160); // Redimensionne l'image pour qu'elle ait une largeur et une hauteur de 100px

// Ajouter le logo à droite
        document.add(logoRight);

// Logo à gauche
        Image logoLeft = new Image(ImageDataFactory.create(logoPathLeft));
        logoLeft.setFixedPosition(50, pdfDoc.getDefaultPageSize().getTop() - 120); // Positionne le logo à 50px du bord gauche et à 100px du haut de la page
        logoLeft.scaleToFit(120, 150); // Redimensionne l'image pour qu'elle ait une largeur et une hauteur de 100px

        // Ajouter le logo à gauche
        document.add(logoLeft);
        // Ajouter un Div pour créer un espace entre le logo et le contenu suivant
        com.itextpdf.layout.element.Div spacer = new com.itextpdf.layout.element.Div();
        spacer.setHeight(80); // hauteur de l'espace, ajustable selon la taille du logo
        document.add(spacer);


        // Ajouter le titre du contrat
        document.add(new com.itextpdf.layout.element.Paragraph("CONTRAT DE PRÊT")
                .setBold().setFontSize(18).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

        // Ajouter un Div pour créer un espace entre le logo et le contenu suivant
        com.itextpdf.layout.element.Div spacer1 = new com.itextpdf.layout.element.Div();
        spacer1.setHeight(80); // hauteur de l'espace, ajustable selon la taille du logo
        document.add(spacer1);

        // Ajouter les informations sur le prêteur et l'emprunteur
        document.add(new com.itextpdf.layout.element.Paragraph("1. PRÊTEUR")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Nom de l'entreprise : RechargePlus S.A."));
        document.add(new com.itextpdf.layout.element.Paragraph("Adresse : lot 13, V5XR+M37 Résidence Essalem II, Av. Fethi Zouhir, Cebalat Ben Ammar 2083"));
        document.add(new com.itextpdf.layout.element.Paragraph("Registre de commerce : R12/123456/09"));
        document.add(new com.itextpdf.layout.element.Paragraph("Matricule fiscal : 123456789/0/3"));

        document.add(new com.itextpdf.layout.element.Paragraph("2. EMPRUNTEUR")
                .setBold().setFontSize(14));
        //dans repo user add :     User findByAccountsTransactionsIdloan(long idLoan);
        // user.getName()
        document.add(new com.itextpdf.layout.element.Paragraph("Nom et prénom : Flen Fouleni"));
        document.add(new com.itextpdf.layout.element.Paragraph("CIN : [Numéro de la carte d’identité nationale]"));
        document.add(new com.itextpdf.layout.element.Paragraph("Adresse : [Adresse complète]"));

        // Ajouter l'objet du contrat
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 1 – OBJET DU CONTRAT")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Le présent contrat a pour objet d’établir les conditions générales du prêt accordé par le Prêteur à l’Emprunteur, ainsi que les modalités de remboursement et les obligations de chaque partie."));

        // Ajouter les montants et conditions du prêt
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 2 – MONTANT ET CONDITIONS DU PRÊT")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Le Prêteur accorde à l’Emprunteur un prêt d’un montant de " + loan.getAmount() + " TND, destiné à son propore motif personnel ."));
        document.add(new com.itextpdf.layout.element.Paragraph("Le taux d’intérêt appliqué est de " + loan.getInterestRate() + " % annuel."));
        document.add(new com.itextpdf.layout.element.Paragraph("Le prêt est accordé pour une durée de " + loan.getDuration() + " mois."));

        // Ajouter un tableau pour les remboursements
        com.itextpdf.layout.element.Table repaymentTable = new com.itextpdf.layout.element.Table(5); // 5 colonnes pour les détails

        // Ajouter les entêtes du tableau
        repaymentTable.addCell("Date de paiement");
        repaymentTable.addCell("Montant mensualité");
        repaymentTable.addCell("Intérêts");
        repaymentTable.addCell("Capital restant dû");
        repaymentTable.addCell("Capital remboursé");


        // Ajouter les lignes du tableau pour chaque remboursement
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.List<tn.esprit.rechargeplus.entities.Repayment> repayments = repaymentRepository.findByloanIdLoan(loanId);
        for (tn.esprit.rechargeplus.entities.Repayment repayment : repayments) {

            String formattedDate = dateFormat.format(repayment.getExpectedPaymentDate());
            // repaymentTable.addCell(repayment.getExpectedPaymentDate().toString());
            repaymentTable.addCell(formattedDate);
            repaymentTable.addCell(String.valueOf(repayment.getMonthly_amount()));
            repaymentTable.addCell(String.valueOf(repayment.getInterest()));
            repaymentTable.addCell(String.valueOf(repayment.getRemainingPrincipal()));
            repaymentTable.addCell(String.valueOf(repayment.getRepaidPrincipal()));
        }

        // Ajouter le tableau des remboursements au document
        document.add(repaymentTable);


        // Ajouter les garanties et engagements
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 4 – GARANTIES ET ENGAGEMENTS")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph(
                "L'Emprunteur s'engage à fournir une garantie sous forme de Garant pour couvrir le prêt, portant les informations suivantes :\n\n" +
                        "Nom complet : " + loan.getGuarantor().getFullName() + "\n" +
                        "Identifiant unique (CIN/Passport) : " + loan.getGuarantor().getNationalId() + "\n" +
                        "Numéro de téléphone : " + loan.getGuarantor().getPhoneNumber() + "\n" +
                        "Lien avec l'emprunteur : " + loan.getGuarantor().getRelationship() + "\n" +
                        "Email : " + loan.getGuarantor().getEmail()
        ));

        // Ajouter les pénalités en cas de retard
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 5 – RETARD DE PAIEMENT ET CONSÉQUENCES")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("En cas de retard de paiement supérieur à 7 jours, l’Emprunteur sera redevable d’une pénalité de  2% du montant dû par mois de retard."));

        // Ajouter la résiliation du contrat
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 6 – RÉSILIATION DU CONTRAT")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Le contrat pourra être résilié de plein droit en cas de fausse déclaration de l’Emprunteur, de non-paiement de 3 mensualités consécutives, ou d’utilisation frauduleuse des fonds prêtés."));

        // Ajouter la loi applicable et la juridiction compétente
        document.add(new com.itextpdf.layout.element.Paragraph("ARTICLE 7 – LOI APPLICABLE ET JURIDICTION COMPÉTENTE")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Le présent contrat est régi par les lois en vigueur en Tunisie, notamment le Code des obligations et des contrats. En cas de litige, le Tribunal de commerce de Tunis sera seul compétent."));
        LocalDate localDate = loan.getRequest_date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        // Ajouter un Div pour créer un espace entre le logo et le contenu suivant
        com.itextpdf.layout.element.Div spacer2 = new com.itextpdf.layout.element.Div();
        spacer1.setHeight(80); // hauteur de l'espace, ajustable selon la taille du logo
        document.add(spacer2);
        // Définir le format
        // Définir le format de la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDateTime.now().format(formatter); // Obtenir la date actuelle

        // Ajouter le texte à droite
        Paragraph locationParagraph = new Paragraph("Tunisie, Tunis, Ariana " )
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(locationParagraph);
        Paragraph locationParagraph1 = new Paragraph(" le " + formattedDate)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(locationParagraph1);

        Paragraph companyParagraph = new Paragraph("RechargePlus S.A.")
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(companyParagraph);

        // Charger l'image de la signature (chemin correct)
        String signaturePath = "C:\\Users\\Rihab\\Downloads\\signature.png";
        Image sign = new Image(ImageDataFactory.create(Paths.get(signaturePath).toAbsolutePath().toString()));

        // Placer la signature SOUS le texte
        sign.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        sign.scaleToFit(120, 50); // Ajuste la taille de l'image
        document.add(sign);


        // Fermer le document
        document.close();

        return baos.toByteArray(); // Retourner le PDF en tant que tableau de bytes
    }


    public void sendLoanEmail(String toEmail, Long loanId) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)
        byte[] loanPdfBytes = generateLoanDocument(loanId);

        final String username = "RechargePlus@zoho.com";  // Zoho SMTP username
        final String password = "RecharginiAman123";  // Zoho SMTP password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");  // Zoho SMTP server
        props.put("mail.smtp.port", "587");  // Port for Zoho's TLS security

        // Create the Session object with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create a MimeMessage for the email
        Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address

        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("Merci pour votre demande de prêt");

        // Set the text content of the email
        message.setText("Cher client,\n\nMerci d'avoir interagi avec notre application pour obtenir un prêt. "
                + "Nous sommes heureux de vous informer que votre demande a été traitée. "
                + "Veuillez trouver ci-joint le contrat pour votre prêt  ."
                + "Cordialement,\nL'équipe de l'application RechargePlus ");

        // Uncomment the following lines to attach the generated PDF file to the email

        ByteArrayDataSource dataSource = new ByteArrayDataSource(loanPdfBytes, "application/pdf");
        message.setDataHandler(new DataHandler(dataSource));
        message.setFileName("Contrat_Pret_" + loanId + ".pdf");


        // Send the email
        Transport.send(message);
    }
    @Scheduled(cron = "0 12 21 * * ?")
    public void checkLoanRepaymentDefault() throws MessagingException, IOException {
        List<Loan> activeLoans = loanRepository.findByStatus(Loan_Status.IN_PROGRESS);

        for (Loan loan : activeLoans) {
        // Récupérer tous les remboursements liés au prêt
        List<Repayment> repayments = repaymentRepository.findByloanIdLoan(loan.getIdLoan());

        // Vérifier si tous les remboursements sont en retard (LATE)
        boolean allRepaymentsLate = repayments.stream()
                .allMatch(repayment -> repayment.getStatus() == Repayment_Status.DEFAULT);

        // Si tous les remboursements sont LATE, on met le prêt en défaut
        if (allRepaymentsLate) {
            loan.setStatus(Loan_Status.DEFAULT);
            loanRepository.save(loan);

            log.info("Loan with ID {} is now marked as DEFAULT due to all repayments being late.", loan.getIdLoan());

            // Envoyer les emails au client et au garant
            sendLoanDefaultEmailToClient("rihabc184@gmail.com", loan.getIdLoan());
            sendLoanDefaultEmailToGuarantor(loan.getGuarantor().getEmail(), loan.getIdLoan());
        }}
    }
    @Scheduled(cron = "0 22 21 * * ?")
    public void checkLoanRepaymentPaid() throws MessagingException, IOException {
        List<Loan> activeLoans = loanRepository.findByStatus(Loan_Status.IN_PROGRESS);

        for (Loan loan : activeLoans) {
        // Récupérer tous les remboursements liés au prêt
        List<Repayment> repayments = repaymentRepository.findByloanIdLoan(loan.getIdLoan());

        // Vérifier s'il y a au moins un remboursement en retard
        boolean hasLateRepayment = repayments.stream()
                .anyMatch(repayment -> repayment.getStatus() == Repayment_Status.REPAID_LATE);

        // Vérifier si tous les remboursements sont payés (REPAID ou REPAID_LATE)
        boolean allRepaymentsPaid = repayments.stream()
                .allMatch(repayment -> repayment.getStatus() == Repayment_Status.REPAID
                        || repayment.getStatus() == Repayment_Status.REPAID_LATE);

        if (allRepaymentsPaid) {
            if (hasLateRepayment) {
                loan.setStatus(Loan_Status.REPAID_LATE);
                log.info("Loan with ID {} is now marked as REPAID_LATE.", loan.getIdLoan());
            } else {
                loan.setStatus(Loan_Status.REPAID);
                log.info("Loan with ID {} is now marked as REPAID.", loan.getIdLoan());
            }
            loanRepository.save(loan);

            // Envoyer un email de confirmation au client et au garant
            sendLoanPaidEmailToClient("rihabc184@gmail.com", loan.getIdLoan(), loan.getStatus());
        }
        }
    }

    public void  sendLoanPaidEmailToClient(String toEmail, Long loanId,Loan_Status status) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)
        byte[] loanPdfBytes = generateLoanDocument(loanId);

        final String username = "RechargePlus@zoho.com";  // Zoho SMTP username
        final String password = "RecharginiAman123";  // Zoho SMTP password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");  // Zoho SMTP server
        props.put("mail.smtp.port", "587");  // Port for Zoho's TLS security

        // Create the Session object with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create a MimeMessage for the email
        Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("🎉 Votre prêt a été remboursé !");

        String statusMessage = (status == Loan_Status.REPAID)
                ? "Félicitations ! Vous avez remboursé votre prêt à temps. ✅"
                : "Votre prêt est remboursé, mais avec du retard. Faites attention à vos délais la prochaine fois. ⏳";

        message.setText("Cher client,\n\n"
                + statusMessage + "\n\n"
                + "Votre prêt avec l'ID " + loanId + " est maintenant considéré comme " + status + ".\n\n"
                + "Merci de votre confiance.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");
        ByteArrayDataSource dataSource = new ByteArrayDataSource(loanPdfBytes, "application/pdf");
        message.setDataHandler(new DataHandler(dataSource));
        message.setFileName("Contrat_Pret_" + loanId + ".pdf");


        // Send the email
        Transport.send(message);
    }

    public void sendLoanDefaultEmailToClient(String toEmail, Long loanId) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)
        byte[] loanPdfBytes = generateLoanDocument(loanId);

        final String username = "RechargePlus@zoho.com";  // Zoho SMTP username
        final String password = "RecharginiAman123";  // Zoho SMTP password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");  // Zoho SMTP server
        props.put("mail.smtp.port", "587");  // Port for Zoho's TLS security

        // Create the Session object with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create a MimeMessage for the email
        Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("🚨 Prêt en défaut de paiement");

        message.setText("Cher client,\n\n"
                + "Nous vous informons que votre prêt avec l'ID " + loanId + " est en défaut de paiement, "
                + "en raison de retards dans les remboursements.\n\n"
                + "Il vous reste encore " + loan.getAmount() + " TND à rembourser.\n\n"
                + "Nous vous encourageons à régler cette situation dès que possible pour éviter des conséquences supplémentaires.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");
        // Uncomment the following lines to attach the generated PDF file to the email

        ByteArrayDataSource dataSource = new ByteArrayDataSource(loanPdfBytes, "application/pdf");
        message.setDataHandler(new DataHandler(dataSource));
        message.setFileName("Contrat_Pret_" + loanId + ".pdf");


        // Send the email
        Transport.send(message);
    }
    public void sendLoanDefaultEmailToGuarantor(String toEmail, Long loanId) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)
        byte[] loanPdfBytes = generateLoanDocument(loanId);

        final String username = "RechargePlus@zoho.com";  // Zoho SMTP username
        final String password = "RecharginiAman123";  // Zoho SMTP password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");  // Zoho SMTP server
        props.put("mail.smtp.port", "587");  // Port for Zoho's TLS security

        // Create the Session object with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create a MimeMessage for the email
        Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("🚨 Prêt en défaut : Intervention requise");
        message.setText("Cher garant,\n\n"
                + "Le prêt de votre client (ID " + loan.getIdLoan() + ") est **en défaut** car **tous les remboursements sont en retard**.\n\n"
                + "Montant restant à payer : **" + loan.getAmount() + " TND**.\n\n"
                + "Nous vous conseillons de prendre contact avec votre client pour résoudre cette situation.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");
        // Uncomment the following lines to attach the generated PDF file to the email

        ByteArrayDataSource dataSource = new ByteArrayDataSource(loanPdfBytes, "application/pdf");
        message.setDataHandler(new DataHandler(dataSource));
        message.setFileName("Contrat_Pret_" + loanId + ".pdf");


        // Send the email
        Transport.send(message);
    }


}


