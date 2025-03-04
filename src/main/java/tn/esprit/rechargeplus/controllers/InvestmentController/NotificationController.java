package tn.esprit.rechargeplus.controllers.InvestmentController;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.rechargeplus.services.InvestmentServices.EmailService;
//import tn.esprit.rechargeplus.services.TwilioService;

@RestController
@RequestMapping("/notifications")  // Vérifie bien cette annotation
public class NotificationController {

    @Autowired
    private EmailService emailService;  // Ajout de @Autowired

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam String to, @RequestParam String message) throws MessagingException {
        emailService.sendEmail(to, "Notification", message);
        return "📧 E-mail envoyé à " + to;
    }
}


    /*@PostMapping("/sendSms")
    public String sendSms(@RequestParam String phone, @RequestParam String message) {
        twilioService.sendSms(phone, message);
        return "📱 SMS envoyé à " + phone;
    }*/
