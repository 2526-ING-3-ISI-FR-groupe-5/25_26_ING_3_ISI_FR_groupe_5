package springboot_25_26_ING_3_ISI_FR_groupe_5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;

@EnableJpaAuditing
@EnableScheduling

@SpringBootApplication
@ComponentScan(basePackages = {
		"springboot_25_26_ING_3_ISI_FR_groupe_5",
		"springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs"
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

