package me.seungeun.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vaccine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique identifier (auto-generated)

    @Column(nullable = false)
    private String vaccineName;  // Vaccine name

    // Many vaccines belong to one VaccinationInfo (ManyToOne relationship)
    // Lazy fetching strategy to delay loading until accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccination_info_id", nullable = false)  // Foreign key column
    private VaccinationInfo vaccinationInfo;
}
