package tn.esprit.rechargeplus.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Product product) {
        notificationService.notifyUser(product);
        return ResponseEntity.ok("Notification sent successfully");
    }
}
