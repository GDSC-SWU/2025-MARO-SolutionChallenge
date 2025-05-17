package me.seungeun.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vaccination_info")
public class VaccinationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique identifier (auto-generated)

    @Column(nullable = false)
    private String placeId;  // Unique place or institution ID

    @Column(nullable = false)
    private String phone;  // Contact phone number

    // One-to-many relationship with Vaccine entities
    // Cascade operations propagate and orphanRemoval deletes disconnected child entities
    @OneToMany(mappedBy = "vaccinationInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vaccine> vaccines;

    // Helper method to add a Vaccine and set the bidirectional relationship
    public void addVaccine(Vaccine vaccine) {
        this.vaccines.add(vaccine);
        vaccine.setVaccinationInfo(this);
    }
}
