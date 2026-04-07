package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Annee_academique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String nom;                    // ex: "2025-2026"

    private boolean active = false;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "anneeAcademique", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Semestre> semestres = new HashSet<>();

    // Méthodes utilitaires pour synchronisation
    public void addSemestre(Semestre semestre) {
        semestres.add(semestre);
        semestre.setAnneeAcademique(this);
    }

    public void removeSemestre(Semestre semestre) {
        semestres.remove(semestre);
        semestre.setAnneeAcademique(null);
    }
}