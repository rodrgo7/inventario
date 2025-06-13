package oliveiradev.inventario.infra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Inventário API", version = "v1", description = "Documentação da API de Inventário")
)
public class OpenApiConfig {
}
