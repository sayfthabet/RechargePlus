package tn.esprit.rechargeplus.services.exceptions;

public class AlreadyReversedException extends RuntimeException {
    public AlreadyReversedException(String message) {
        super(message);
    }
}