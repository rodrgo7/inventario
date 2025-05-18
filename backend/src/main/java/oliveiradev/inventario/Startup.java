package oliveiradev.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing // Habilita a auditoria do MongoDB)
public class Startup {
	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
	}

}
