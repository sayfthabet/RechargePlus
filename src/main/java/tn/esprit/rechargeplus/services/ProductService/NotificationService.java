package tn.esprit.rechargeplus.services.ProductService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Product;

@Service
public class NotificationService implements INotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifyUser(Product product) {
        if (product == null || product.getUser() == null) {
            throw new IllegalArgumentException("Product or User information is missing");
        }

        String userEmail = product.getUser().getEmail();
        String subject = "⚠️ Low Stock Alert: " + product.getName();
        String message = "Hello " + product.getUser().getName() + ",\n\n" +
                "Your product '" + product.getName() + "' is running low on stock. " +
                "Only " + product.getQuantity() + " left from the initial stock.\n\n" +
                "Consider restocking soon.\n\nBest regards,\nRechargePlus";

        sendEmail(userEmail, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}