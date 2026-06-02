package com.grupo5.citasmedicas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Citas Medicas - API",
                version = "1.0.0",
                description = "Backend del Sistema de Citas Medicas (Tarea T02.03 - Grupo 5). "
                        + "Implementa los requerimientos del SRS (T02.01) y el diseno del DDS (T02.02): "
                        + "autenticacion JWT/RBAC, gestion de pacientes y colaboradores, agenda de citas, "
                        + "historia clinica, prescripciones, certificados, facturacion y contabilidad.",
                contact = @Contact(name = "Grupo 5 - Ingenieria de Software", email = "grupo5@est.ups.edu.ec"),
                license = @License(name = "Uso academico")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class CitasMedicasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitasMedicasApplication.class, args);
    }
}
