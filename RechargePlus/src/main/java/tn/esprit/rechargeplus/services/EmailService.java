package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.User;

public interface EmailService {
    void sendVerificationEmail(User user);
}
