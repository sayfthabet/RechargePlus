package tn.esprit.rechargeplus.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    private String name;
    private String email;
    private String password;
    private String role;
    private String address;
    private String mobile_number;
    private Date birth_date;
    private Date registered_at;
    private String country;
    private String status; // active, inactive, banned
    @Lob
    private byte[] face_photo;
    @Lob
    private byte[] national_identity_card;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Product> Produits;
    @OneToOne
    private Role User_Role;
    @OneToMany (mappedBy = "user")
    @JsonIgnore
    private List<Account> accounts;
    @OneToMany (mappedBy = "user")
    @JsonIgnore
    private List<Project> projects;

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public Date getRegistered_at() {
        return registered_at;
    }

    public void setRegistered_at(Date registered_at) {
        this.registered_at = registered_at;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getFace_photo() {
        return face_photo;
    }

    public void setFace_photo(byte[] face_photo) {
        this.face_photo = face_photo;
    }

    public byte[] getNational_identity_card() {
        return national_identity_card;
    }

    public void setNational_identity_card(byte[] national_identity_card) {
        this.national_identity_card = national_identity_card;
    }

    public List<Product> getProduits() {
        return Produits;
    }

    public void setProduits(List<Product> produits) {
        Produits = produits;
    }

    public Role getUser_Role() {
        return User_Role;
    }

    public void setUser_Role(Role user_Role) {
        User_Role = user_Role;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
