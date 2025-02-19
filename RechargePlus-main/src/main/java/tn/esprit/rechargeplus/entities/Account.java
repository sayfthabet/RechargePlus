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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idAccount;
    private long amount;
    @Enumerated(value = EnumType.STRING)
    private Account_Status status;
    private Date created_at;
    private Date updated_at;
    private String type;
    @OneToMany (mappedBy = "account")
    private List<Transaction> transactions;
    @ManyToOne
    private User user;
}
