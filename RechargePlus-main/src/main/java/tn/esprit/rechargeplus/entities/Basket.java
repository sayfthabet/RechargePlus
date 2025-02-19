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
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBasket;
    private Date date;
    private int ConnectedUser;
    private double total_price;
    @Enumerated(value = EnumType.STRING)
    private Basket_Status status;
    @OneToMany(mappedBy = "basket")
    private List<Basket_items> basket_items;
}
