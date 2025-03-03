package tn.esprit.rechargeplus.entities;

import jakarta.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private role_enum name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public role_enum getName() { return name; }
    public void setName(role_enum name) { this.name = name; }
}
