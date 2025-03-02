package tn.esprit.rechargeplus.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Repayment_Status;
import tn.esprit.rechargeplus.entities.Transaction;
import tn.esprit.rechargeplus.repositories.ILoanRepository;
import tn.esprit.rechargeplus.repositories.IRepaymentRepository;
import tn.esprit.rechargeplus.entities.Repayment;
import tn.esprit.rechargeplus.services.exceptions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;



@Service
@AllArgsConstructor
public class RepaymentService  implements IRepaymentService {
    @Autowired
    IRepaymentRepository repaymentRepository;
    @Autowired
    TransactionService transactionService;
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

    @Scheduled(cron = "0 24 3 * * ?")
    public void UpdateRepaymentStatus(Long accountId,  String ipAddress) throws MessagingException, IOException {
        LocalDate today = LocalDate.now();
        List<Repayment> repaymentsDueToday = repaymentRepository.findByExpectedPaymentDate(java.sql.Date.valueOf(today));

        for (Repayment repayment : repaymentsDueToday) {
            try {
                // Call withdrawFunds to attempt repayment
                Transaction transaction = transactionService.withdrawFunds(
                        accountId, repayment.getMonthly_amount(), ipAddress);


                // If the transaction is successful, update repayment status to "PAID"
                repayment.setStatus(Repayment_Status.REPAID);
                repaymentRepository.save(repayment);
                sendSuccessRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount(), repayment.getRemainingPrincipal());
            } catch (InsufficientFundsException | DailyLimitExceededException e) {
                // If an exception occurs, update repayment status to "LATE"
                repayment.setStatus(Repayment_Status.DEFAULT);
                repaymentRepository.save(repayment);

                // Send email notification to the user
                sendDefaultRepaymentEmail("rihabc184@gmail.com", repayment.getMonthly_amount()); } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
