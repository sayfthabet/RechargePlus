package tn.esprit.rechargeplus.entities;

import jakarta.persistence.*;
import lombok.*;

import tn.esprit.rechargeplus.entities.role_enum;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idRole;
    @Enumerated(value = EnumType.STRING)
    private role_enum role;
    @OneToOne (mappedBy = "User_Role")
    private User user;

}
