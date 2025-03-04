package tn.esprit.rechargeplus.services.AccountService.exceptions;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
