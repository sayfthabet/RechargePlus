package tn.esprit.rechargeplus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AuthentificationRequest {

    private String username;
    private String password;
    // Constructeur
    public AuthentificationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


