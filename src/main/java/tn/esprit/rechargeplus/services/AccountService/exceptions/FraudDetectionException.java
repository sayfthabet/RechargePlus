package tn.esprit.rechargeplus.services.AccountService.exceptions;

public class FraudDetectionException extends RuntimeException {
    public FraudDetectionException(String message) {
        super(message);
    }
}