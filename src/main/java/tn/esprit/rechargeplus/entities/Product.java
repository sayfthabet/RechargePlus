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
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProduct;
    private String name;
    private double price;
    @Lob
    private byte[] image;
    private String description;
    private String type;
    private int quantity;
    @Enumerated(value = EnumType.STRING)
    private Product_Status status;
    @ManyToOne
    private User user;
    @OneToMany (mappedBy = "product")
    private List<Rating> ratings;
    @OneToMany (mappedBy = "product")
    private List<Basket_items> basket_items;

}
