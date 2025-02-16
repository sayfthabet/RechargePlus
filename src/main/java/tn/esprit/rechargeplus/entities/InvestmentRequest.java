package tn.esprit.rechargeplus.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class InvestmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idInvestmentRequest;
    private double amount;
    private Date request_date;
    private double expectedROI;
    private double actualROI;
    private int ConnectedUser;
    private double riskScore;
    private int investmentDuration; // in months
    @ManyToOne
    private Project project;
    @OneToMany(mappedBy = "investment_request")
    private List<Transaction> transactions;
}
