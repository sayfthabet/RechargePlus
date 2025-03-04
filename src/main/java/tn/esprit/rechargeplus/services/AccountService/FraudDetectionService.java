package tn.esprit.rechargeplus.services.AccountService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FraudDetectionService {
    private static final Logger log = LoggerFactory.getLogger(FraudDetectionService.class);

    @Value("${fraud.detection.amount.threshold:10000}")
    private double amountThreshold;

    @Value("${fraud.detection.blocked.ips:}")
    private String blockedIpsString;

    public void checkForFraud(double amount, String ipAddress) {
        // Initialisation des IPs bloquées à chaque appel
        Set<String> blockedIps = new HashSet<>();

        // Vérification si la chaîne n'est pas vide
        if (!blockedIpsString.isEmpty()) {
            // Split des adresses IP par la virgule et ajout dans le Set
            String[] ips = blockedIpsString.split(",");
            for (String ip : ips) {
                String trimmedIp = ip.trim();
                if (!trimmedIp.isEmpty()) {
                    blockedIps.add(trimmedIp);
                }
            }
            log.info("Loaded blocked IPs: {}", blockedIps);
        } else {
            log.warn("No blocked IPs configured.");
        }

        // Vérification du montant et des adresses IP
        if (amount > amountThreshold) {
            String message = String.format("Potential fraud detected: amount %.2f exceeds threshold of %.2f", amount, amountThreshold);
            log.warn(message);
            throw new RuntimeException(message);
        }

        if (blockedIps.contains(ipAddress)) {
            String message = String.format("Potential fraud detected: IP address %s is blocked", ipAddress);
            log.warn(message);
            throw new RuntimeException(message);
        }
    }
}