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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProject;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("sector")
    private String sector;
    @JsonProperty("location")
    private String location;
    @JsonProperty("amountRequested")
    private double amountRequested;
    @JsonProperty("amountCollected")
    private double amountCollected;
    @JsonProperty("riskCategory")
    private String riskCategory;
    @JsonProperty("investmentAmount")
   @Column(name = "investment_amount")
    private double investmentAmount;
    @JsonProperty("investmentTrends")
    @Column(name = "investment_trends")
    private String investmentTrends;

    @Enumerated(value = EnumType.STRING)
    @JsonProperty("status")
    private Project_Status status;
    @ManyToOne
    @JsonIgnore
    private User user;
    @OneToMany (mappedBy = "project")
    @JsonIgnore
    private List<InvestmentRequest> investment_requests;

}
