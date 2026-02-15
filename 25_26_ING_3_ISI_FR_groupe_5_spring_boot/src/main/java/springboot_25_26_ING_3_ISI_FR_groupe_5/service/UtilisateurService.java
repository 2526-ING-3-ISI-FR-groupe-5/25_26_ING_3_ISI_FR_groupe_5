package springboot_25_26_ING_3_ISI_FR_groupe_5.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeRole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Repository.UtilisateurRepository;

@Service
@AllArgsConstructor
public class UtilisateurService {
    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public void inscription(Utilisateur utilisateur) {
        if(utilisateur.getEmail().indexOf("@")==-1){
            throw new RuntimeException("Votre mail est invalide");
        }
        Utilisateur user = this.utilisateurRepository.findByUsername(utilisateur.getEmail());
        if(user.isPresent()){
            throw new RuntimeException("Votre mail déja utilisé");
        }

        String mdpCrypte = this.bCryptPasswordEncoder.encode(utilisateur.getPassword());
         utilisateur.setPassword(mdpCrypte);

        Role roleUtilisateur = new Role();
        roleUtilisateur.setNom(TypeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        this.utilisateurRepository.save(utilisateur);
    }
}
