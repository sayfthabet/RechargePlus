package tn.esprit.rechargeplus.entities;

import lombok.Builder;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private role_enum role;

    private String address;

    private String mobileNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private Date registeredAt;

    private String country;

    @Enumerated(EnumType.STRING)
    private Status status; // active, inactive, banned

    @Lob
    private byte[] facePhoto;

    @Lob
    private byte[] nationalIdentityCard;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Product> produits;

    @OneToOne
    private Role userRole;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Project> projects;

    // Constructeurs
    @Builder
    public User() {
    }

    public User(long idUser, String name, String email, String password, role_enum role, String address,
                String mobileNumber, Date birthDate, Date registeredAt, String country, Status status,
                byte[] facePhoto, byte[] nationalIdentityCard, List<Product> produits, Role userRole,
                List<Account> accounts, List<Project> projects) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.birthDate = birthDate;
        this.registeredAt = registeredAt;
        this.country = country;
        this.status = status;
        this.facePhoto = facePhoto;
        this.nationalIdentityCard = nationalIdentityCard;
        this.produits = produits;
        this.userRole = userRole;
        this.accounts = accounts;
        this.projects = projects;
    }

    // Implémentation des méthodes UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Utilisation de l'email comme username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Peut être personnalisé selon la logique métier
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Peut être modifié pour gérer les utilisateurs bloqués
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Peut être utilisé pour forcer le changement de mot de passe après un certain temps
    }

    @Override
    public boolean isEnabled() {
        return this.status == Status.ACTIVE; // L'utilisateur est actif s'il a le statut ACTIVE
    }

    // Getters et Setters
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

    public role_enum getRole() {
        return role;
    }

    public void setRole(role_enum role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public byte[] getFacePhoto() {
        return facePhoto;
    }

    public void setFacePhoto(byte[] facePhoto) {
        this.facePhoto = facePhoto;
    }

    public byte[] getNationalIdentityCard() {
        return nationalIdentityCard;
    }

    public void setNationalIdentityCard(byte[] nationalIdentityCard) {
        this.nationalIdentityCard = nationalIdentityCard;
    }

    public List<Product> getProduits() {
        return produits;
    }

    public void setProduits(List<Product> produits) {
        this.produits = produits;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
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
