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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProject;
    private String title;
    private String description;
    private String sector;
    private String location;
    private double amountRequested;
    private double amountCollected;
    private String riskCategory;
    private double InvestmentAmount ;
    private String InvestmentTrends;
    @Enumerated(value = EnumType.STRING)
    private Project_Status status;
    @ManyToOne
    private User user;
    @OneToMany (mappedBy = "project")
    private List<InvestmentRequest> investment_requests;

}
