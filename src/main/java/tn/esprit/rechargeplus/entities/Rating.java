package tn.esprit.rechargeplus.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idRating;
    private int rate;
    private Date rating_date;
    private int ConnectedUser;
    @ManyToOne
    private Product product;

    public Rating() {
    }

    public Rating(long idRating, int rate, Date rating_date, int connectedUser, Product product) {
        this.idRating = idRating;
        this.rate = rate;
        this.rating_date = rating_date;
        ConnectedUser = connectedUser;
        this.product = product;
    }

    public long getIdRating() {
        return idRating;
    }

    public void setIdRating(long idRating) {
        this.idRating = idRating;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Date getRating_date() {
        return rating_date;
    }

    public void setRating_date(Date rating_date) {
        this.rating_date = rating_date;
    }

    public int getConnectedUser() {
        return ConnectedUser;
    }

    public void setConnectedUser(int connectedUser) {
        ConnectedUser = connectedUser;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
