package tn.esprit.rechargeplus.services.ProductService;

import org.springframework.mail.javamail.JavaMailSender;
import tn.esprit.rechargeplus.entities.Product;

public interface INotificationService {
    public void notifyUser(Product product);
}
