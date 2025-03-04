package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("request_date")
    private Date request_date;
    @JsonProperty("expectedROI")
    private double expectedROI;
    @JsonProperty("actualROI")
    private double actualROI;
    @JsonProperty("ConnectedUser")
    private int ConnectedUser;
    @JsonProperty("riskScore")
    private double riskScore;
    @JsonProperty("investmentDuration")
    private int investmentDuration; // in months
    @Enumerated(value = EnumType.STRING)
    @JsonProperty("status")
    private Investment_Request_Status status;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    @OneToMany(mappedBy = "investment_request")
    @JsonIgnore
    private List<Transaction> transactions;

    public Project getProject() {
        return project;
    }

    public double getAmount() {
        return amount;
    }

    public void setProject(Project project) {
        this.project = project;
    }

}
