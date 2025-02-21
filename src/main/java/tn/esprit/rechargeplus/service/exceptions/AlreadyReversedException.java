package tn.esprit.rechargeplus.service.exceptions;

public class AlreadyReversedException extends RuntimeException {
    public AlreadyReversedException(String message) {
        super(message);
    }
}