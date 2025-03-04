package tn.esprit.rechargeplus.entities;

public class JwtResponse {
    private String type;
    private String username;
    private String token;

    // Private constructor to force using the builder
    private JwtResponse(Builder builder) {
        this.type = builder.type;
        this.username = builder.username;
        this.token = builder.token;
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    // Static builder class
    public static class Builder {
        private String type;
        private String username;
        private String token;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(this);
        }
    }
}
