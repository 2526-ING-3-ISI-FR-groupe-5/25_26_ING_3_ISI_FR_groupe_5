package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;

import java.util.List;
import java.util.Optional;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {

    Optional<UE> findByCode(String code);

    // ✅ Méthode pour récupérer TOUTES les UE (sans condition)
    @Query("SELECT u FROM UE u")
    List<UE> findAllUes();

    // Pour les UE programmées (garder si besoin)
    @Query("SELECT DISTINCT p.ue FROM ProgrammationUE p WHERE p.semestre.anneeAcademique.id = :anneeId")
    List<UE> findProgrammedUesByAnnee(@Param("anneeId") Long anneeId);

    @Query("SELECT DISTINCT p.ue FROM ProgrammationUE p WHERE p.classe.id = :classeId AND p.semestre.anneeAcademique.id = :anneeId")
    List<UE> findByClasseAndAnnee(@Param("classeId") Long classeId, @Param("anneeId") Long anneeId);

    List<UE> findByNomContainingIgnoreCase(String nom);

    List<UE> findBySpecialiteId(Long specialiteId);
}