package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;

import java.util.List;
import java.util.Optional;
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNom(String name);
    boolean existsByNom(String nom);
    List<Permission> findByActiveTrue();
    Page<Permission> findByNomContainingIgnoreCase(String name, Pageable pageable);
    Page <Permission> findByActive(Boolean active, Pageable pageable);
}