package tn.esprit.rechargeplus.services;

import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {
    public void checkForFraud(double amount, String ipAddress) {
        // Simplified fraud detection logic; enhance as needed.
        if (amount > 10000) { // Example threshold for extra scrutiny
            throw new RuntimeException("Potential fraud detected: amount too high");
        }
        // Additional checks (e.g., IP address blacklisting) can be added here.
    }
}
