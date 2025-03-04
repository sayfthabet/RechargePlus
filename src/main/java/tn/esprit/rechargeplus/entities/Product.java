package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id")
    @JsonIgnore
    private User user;
    @OneToMany (mappedBy = "product")
    @JsonIgnore
    private List<Rating> ratings;
    @OneToMany (mappedBy = "product")
    private List<Basket_items> basket_items;

    public Product() {
    }

    public Product(long idProduct, String name, double price, byte[] image, String description, String type, int quantity, Product_Status status, User user, List<Rating> ratings, List<Basket_items> basket_items) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.type = type;
        this.quantity = quantity;
        this.status = status;
        this.user = user;
        this.ratings = ratings;
        this.basket_items = basket_items;
    }

    public long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(long idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product_Status getStatus() {
        return status;
    }

    public void setStatus(Product_Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Basket_items> getBasket_items() {
        return basket_items;
    }

    public void setBasket_items(List<Basket_items> basket_items) {
        this.basket_items = basket_items;
    }
}
