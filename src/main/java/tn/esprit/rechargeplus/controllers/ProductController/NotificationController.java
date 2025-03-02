package tn.esprit.rechargeplus.controllers.ProductController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.services.ProductService.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notify")
    public ResponseEntity<String> sendNotification(@RequestBody Product product) {
        notificationService.notifyUser(product);
        return ResponseEntity.ok("Notification sent successfully");
    }
}
