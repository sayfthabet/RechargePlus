package tn.esprit.rechargeplus.services.LoanService;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Loan;
import tn.esprit.rechargeplus.entities.Repayment_Status;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.repositories.LoanRepository.ILoanRepository;
import tn.esprit.rechargeplus.repositories.LoanRepository.IRepaymentRepository;
import tn.esprit.rechargeplus.entities.Repayment;
import tn.esprit.rechargeplus.repositories.TransactionRepository;
import tn.esprit.rechargeplus.services.TransactionService;
import tn.esprit.rechargeplus.services.exceptions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
@AllArgsConstructor
public class RepaymentService  implements IRepaymentService {
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    ILoanRepository loanRepository;
    private static final Logger log = LoggerFactory.getLogger(RepaymentService.class);

    @Override
    public Repayment addRepayment(Repayment repayment) {
        return repaymentRepository.save(repayment);
    }

    @Override
    public Repayment updateRepayment(Repayment repayment) {
        return repaymentRepository.save(repayment);
    }

    @Override
    public void remouveRepayment(Long numRepayment) {
        repaymentRepository.deleteById(numRepayment);
    }

    @Override
    public Repayment retrieveRepayment(Long numRepayment) {
        return repaymentRepository.findById(numRepayment).orElse(null);
    }

    @Override
    public List<Repayment> retriveAll() {
        return (List<Repayment>) repaymentRepository.findAll();
    }

    @Scheduled(cron = "0 24 3 * * ?")
    public void sendRepaymentReminders() throws IOException, MessagingException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Repayment> repaymentsDueTomorrow = repaymentRepository.findByExpectedPaymentDate(
                java.sql.Date.valueOf(tomorrow));

        for (Repayment repayment : repaymentsDueTomorrow) {
          //  sendSms( repayment.getMonthly_amount());
            sendLoanEmail("rihabc184@gmail.com", repayment.getMonthly_amount());
        }
    }
    public void sendLoanEmail(String toEmail, double amount) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)


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
        jakarta.mail.Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address

        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("🔔 Rappel de Paiement ");

        // Set the text content of the email
        message.setText("Cher client,\n\n"
                + "Nous vous rappelons que vous avez un paiement de **" + amount + " TND** prévu pour demain. "
                + "Veuillez vous assurer d'avoir le montant nécessaire sur votre compte afin d'éviter tout retard.\n\n"
                + "Pour toute question, n'hésitez pas à nous contacter.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");





        // Send the email
        Transport.send(message);
    }


    public final String ACCOUNT_SID = "AC9256cac011b4f52d6204c2440471fbc7";
    public  final  String AUTH_TOKEN = "5364156662303c3c19fe7c3112af814a";
    public  void sendSms(double amount) throws IOException {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+21621391325"),
                        "MGc8326dae8d9e145f7e342c9b4ae540e0",
                " [Rappel de Paiement]\n\n"
                        + "Cher client,\n\n"
                        + "Nous vous rappelons que vous avez un paiement de **" + amount + " TND** prévu pour demain. "
                        + "Veuillez vous assurer d'avoir le montant nécessaire sur votre compte afin d'éviter tout retard.\n\n"
                        + "Pour toute question, n'hésitez pas à nous contacter.\n\n"
                        + "Cordialement,\n"
                        + "Équipe RechargePlus")
                .create();
        System.out.println(message.getSid());

    }

  /*  @Scheduled(cron = "0 32 17 * * ?")
    public void UpdateRepaymentStatus( ) throws MessagingException, IOException {
     //   LocalDate today = LocalDate.now();
     //   List<Repayment> repaymentsDueToday = repaymentRepository.findByExpectedPaymentDate(java.sql.Date.valueOf(today));
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 2025-03-03T00:00:00
        LocalDateTime endOfDay = today.atTime(23, 59, 59); // 2025-03-03T23:59:59

        List<Repayment> repaymentsDueToday = repaymentRepository.findRepaymentsDueToday(startOfDay, endOfDay);


        for (Repayment repayment : repaymentsDueToday) {
            Loan loan = repayment.getLoan();

            // Trouver une des transactions liées au prêt pour obtenir le accountId
            Transaction transaction = transactionRepository.findByLoan_IdLoan(loan.getIdLoan())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No transaction found for loan ID: " + loan.getIdLoan()));

            Long accountId = transaction.getAccount().getId(); // Récupérer accountId depuis la transaction associée

            try {
                // Tentative de retrait des fonds pour effectuer le remboursement
                Transaction transactionResult = transactionService.withdrawFunds(accountId, repayment.getMonthly_amount(), "192.168.1.1");

                // Si la transaction est réussie, mettre à jour le statut du remboursement à "PAID"
                repayment.setStatus(Repayment_Status.REPAID);
                repayment.setActualPaymentDate(java.sql.Date.valueOf(today));
                repaymentRepository.save(repayment);
                loan.setRemaining_repayment(loan.getTotal_repayment_amount()-repayment.getMonthly_amount());
                loan.setTotal_repayment_amount(loan.getTotal_repayment_amount()+repayment.getMonthly_amount());
                loanRepository.save(loan);
                // Envoyer un email de succès de remboursement
                sendSuccessRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount(), repayment.getRemainingPrincipal());

            } catch (InsufficientFundsException | DailyLimitExceededException e) {
                // Si une exception liée aux fonds insuffisants ou au plafond quotidien se produit
                repayment.setStatus(Repayment_Status.DEFAULT);
                repaymentRepository.save(repayment);

                // Envoyer un email de notification de défaut de remboursement
                sendDefaultRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount());

            } catch (IOException e) {
                // Gérer l'exception d'entrée/sortie
                throw new RuntimeException("Error processing repayment email", e);
            }
        }
    }*/
  @Scheduled(cron = "0 25 19 * * ?")
  public void UpdateRepaymentStatus() throws MessagingException, IOException {
      log.info("Début de la mise à jour des statuts de remboursement...");

      LocalDate today = LocalDate.now();
      LocalDateTime startOfDay = today.atStartOfDay(); // 00:00:00
      LocalDateTime endOfDay = today.atTime(23, 59, 59); // 23:59:59

      log.info("Recherche des remboursements dus entre {} et {}", startOfDay, endOfDay);
      List<Repayment> repaymentsDueToday = repaymentRepository.findRepaymentsDueToday(startOfDay, endOfDay);
      log.info("Nombre de remboursements trouvés pour aujourd'hui : {}", repaymentsDueToday.size());

      for (Repayment repayment : repaymentsDueToday) {
          Loan loan = repayment.getLoan();
          log.info("Traitement du remboursement ID: {} pour le prêt ID: {}", repayment.getIdRepayment(), loan.getIdLoan());

          try {
              // Trouver une transaction associée au prêt
              Optional<Transaction> transactionOpt = transactionRepository.findByLoan_IdLoan(loan.getIdLoan()).stream().findFirst();

              if (transactionOpt.isEmpty()) {
                  log.warn("Aucune transaction trouvée pour le prêt ID: {}", loan.getIdLoan());
                  continue; // Passer au remboursement suivant
              }

              Transaction transaction = transactionOpt.get();
              Long accountId = transaction.getAccount().getId();
              log.info("Compte ID {} trouvé pour le prêt ID {}", accountId, loan.getIdLoan());

              // Tentative de retrait des fonds
              log.info("Tentative de retrait de {} du compte ID: {}", repayment.getMonthly_amount(), accountId);
              Transaction transactionResult = transactionService.withdrawFunds(accountId, repayment.getMonthly_amount(), "192.168.1.1");

              // Mise à jour du remboursement à "PAID"
              repayment.setStatus(Repayment_Status.REPAID);
              repayment.setActualPaymentDate(java.sql.Date.valueOf(today));
              repaymentRepository.save(repayment);
              log.info("Remboursement ID: {} marqué comme REPAID", repayment.getIdRepayment());

              // Mise à jour des montants du prêt
              loan.setRemaining_repayment(loan.getRemaining_repayment() - repayment.getMonthly_amount());
              loan.setTotal_repayment_amount(loan.getTotal_repayment_amount() + repayment.getMonthly_amount());
              loanRepository.save(loan);
              log.info("Montants du prêt ID {} mis à jour : Restant à rembourser = {}, Total remboursé = {}", loan.getIdLoan(), loan.getRemaining_repayment(), loan.getTotal_repayment_amount());

              // Envoyer un email de succès
              sendSuccessRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount(), loan.getRemaining_repayment());
              log.info("Email de succès envoyé pour le remboursement ID: {}", repayment.getIdRepayment());

          } catch (InsufficientFundsException | DailyLimitExceededException e) {
              log.warn("Échec du remboursement ID: {} - Cause: {}", repayment.getIdRepayment(), e.getMessage());

              // Mise à jour du remboursement à "DEFAULT"
              repayment.setStatus(Repayment_Status.DEFAULT);
              repaymentRepository.save(repayment);
              log.info("Remboursement ID: {} marqué comme DEFAULT", repayment.getIdRepayment());

              // Envoyer un email d'échec
              sendDefaultRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount());
              log.info("Email de défaut envoyé pour le remboursement ID: {}", repayment.getIdRepayment());

          } catch (IOException e) {
              log.error("Erreur d'envoi d'email pour le remboursement ID: {}", repayment.getIdRepayment(), e);
              throw new RuntimeException("Erreur lors de l'envoi des emails", e);
          }
      }

      log.info("Fin de la mise à jour des statuts de remboursement.");
  }

    public void sendDefaultRepaymentEmail(String toEmail, double amount) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)


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
        jakarta.mail.Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address

        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("⚠️ Alerte de Retard de Paiement");

        message.setText("Cher client,\n\n"
                + "Nous vous informons que la date limite de votre paiement de **" + amount + " TND** est passée et votre compte ne contient pas le montant nécessaire pour effectuer le règlement.\n\n"
                + "Veuillez régulariser votre situation dès que possible pour éviter toute conséquence supplémentaire. Si vous avez des questions ou si vous souhaitez discuter de votre paiement, n'hésitez pas à nous contacter.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");



        // Send the email
        Transport.send(message);
    }
    public void sendSuccessRepaymentEmail(String toEmail, double amount,double remainingLoanAmount) throws MessagingException, java.io.IOException {
        // Générer le PDF en tableau de bytes (this is where you generate the PDF as a byte array)


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
        jakarta.mail.Message message = new MimeMessage(session);
        String from = "RechargePlus@zohomail.com";  // Sender's email address

        message.setFrom(new InternetAddress(from));
        // Set recipient email field
        message.setRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));  // Dynamic email from parameter
        // Set email subject field
        message.setSubject("🎉 Félicitations ! Paiement Effectué avec Succès");

        message.setText("Cher client,\n\n"
                + "Nous avons le plaisir de vous informer que votre paiement de **" + amount + " TND** a été effectué avec succès. Félicitations pour avoir respecté vos engagements de paiement !\n\n"
                + "Actuellement, il vous reste **" + remainingLoanAmount + " TND** à rembourser sur votre prêt.\n\n"
                + "Merci de faire partie de la communauté RechargePlus. Nous apprécions votre fiabilité et restons à votre disposition pour toute question ou assistance supplémentaire.\n\n"
                + "Cordialement,\n"
                + "💳 Équipe RechargePlus");


        // Send the email
        Transport.send(message);
    }

}
