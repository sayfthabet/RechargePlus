package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Basket_items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBasket_items;
    private int quantity;
    @ManyToOne
    @JsonIgnore
    private Basket basket;
    @ManyToOne
    @JsonIgnore
    private Product product;

    public Basket_items() {
    }

    public Basket_items(long idBasket_items, int quantity, Basket basket, Product product) {
        this.idBasket_items = idBasket_items;
        this.quantity = quantity;
        this.basket = basket;
        this.product = product;
    }

    public long getIdBasket_items() {
        return idBasket_items;
    }

    public void setIdBasket_items(long idBasket_items) {
        this.idBasket_items = idBasket_items;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
