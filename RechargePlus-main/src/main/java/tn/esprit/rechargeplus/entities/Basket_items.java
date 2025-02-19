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
public class Basket_items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBasket_items;
    private int quantity;
    @ManyToOne
    private Basket basket;
    @ManyToOne
    private Product product;
}
