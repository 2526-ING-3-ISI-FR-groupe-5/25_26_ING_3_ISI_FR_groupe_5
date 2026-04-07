package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<Annee_academique, Long> {

    // Trouver l'année en cours
    Optional<Annee_academique> findByActiveTrue();

    // Vérifier si une année existe déjà
    boolean existsByNom(String nom);
    Optional<Annee_academique> findByNom(String nom);

    // Toutes les années triées
    List<Annee_academique> findAllByOrderByNomDesc();
}
