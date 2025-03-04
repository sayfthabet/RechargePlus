package tn.esprit.rechargeplus.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;


    private String address;

    private String mobile_number;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    private Date birth_date;

    private Date registered_at;

    private String country;

    @Lob
    private byte[] face_photo;

    @Lob
    private byte[] national_identity_card;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Product> Produits;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Project> projects;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;


    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities;

    public User() {
    }

    public User(String username, Object o, List<SimpleGrantedAuthority> grantedAuthorities) {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }
}