package tn.esprit.rechargeplus.services.AccountService.exceptions;

public class AlreadyReversedException extends RuntimeException {
    public AlreadyReversedException(String message) {
        super(message);
    }
}