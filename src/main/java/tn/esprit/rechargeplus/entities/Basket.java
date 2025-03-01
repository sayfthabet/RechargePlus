package tn.esprit.rechargeplus.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
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
    private List<Basket_items> basket_items ;

    public Basket() {
    }

    public Basket(long idBasket, Date date, int connectedUser, double total_price, Basket_Status status, List<Basket_items> basket_items) {
        this.idBasket = idBasket;
        this.date = date;
        ConnectedUser = connectedUser;
        this.total_price = total_price;
        this.status = status;
        this.basket_items = basket_items;
    }
    public long getIdBasket() {
        return idBasket;
    }

    public void setIdBasket(long idBasket) {
        this.idBasket = idBasket;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getConnectedUser() {
        return ConnectedUser;
    }

    public void setConnectedUser(int connectedUser) {
        ConnectedUser = connectedUser;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public Basket_Status getStatus() {
        return status;
    }

    public void setStatus(Basket_Status status) {
        this.status = status;
    }

    public List<Basket_items> getBasket_items() {
        return basket_items;
    }

    public void setBasket_items(List<Basket_items> basket_items) {
        this.basket_items = basket_items;
    }
}
