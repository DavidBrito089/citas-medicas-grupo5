package com.grupo5.citasmedicas.bootstrap;

import com.grupo5.citasmedicas.enums.Role;
import com.grupo5.citasmedicas.model.AccountingAccount;
import com.grupo5.citasmedicas.model.Collaborator;
import com.grupo5.citasmedicas.model.InstitutionConfig;
import com.grupo5.citasmedicas.model.Patient;
import com.grupo5.citasmedicas.model.User;
import com.grupo5.citasmedicas.repository.AccountingAccountRepository;
import com.grupo5.citasmedicas.repository.CollaboratorRepository;
import com.grupo5.citasmedicas.repository.InstitutionConfigRepository;
import com.grupo5.citasmedicas.repository.PatientRepository;
import com.grupo5.citasmedicas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/**
 * Carga datos iniciales para que la API sea funcional al primer arranque:
 * un usuario admin, un medico, un paciente de ejemplo, el plan de cuentas
 * basico y la configuracion de la institucion.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final PatientRepository patientRepository;
    private final AccountingAccountRepository accountRepository;
    private final InstitutionConfigRepository configRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CollaboratorRepository collaboratorRepository,
                      PatientRepository patientRepository, AccountingAccountRepository accountRepository,
                      InstitutionConfigRepository configRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.patientRepository = patientRepository;
        this.accountRepository = accountRepository;
        this.configRepository = configRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("DataSeeder: ya existen datos, se omite la carga inicial.");
            return;
        }

        // Usuarios por cada rol (RQ-CONF-01).
        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@clinicagrupo5.ec")
                .passwordHash(passwordEncoder.encode("admin123"))
                .fullName("Administrador del Sistema")
                .roles(Set.of(Role.ADMIN))
                .build());

        User medicoUser = userRepository.save(User.builder()
                .username("medico")
                .email("medico@clinicagrupo5.ec")
                .passwordHash(passwordEncoder.encode("medico123"))
                .fullName("Dra. Ana Salazar")
                .roles(Set.of(Role.MEDICO))
                .build());

        userRepository.save(User.builder()
                .username("recepcion")
                .email("recepcion@clinicagrupo5.ec")
                .passwordHash(passwordEncoder.encode("recepcion123"))
                .fullName("Recepcion Principal")
                .roles(Set.of(Role.RECEPCIONISTA))
                .build());

        // Colaborador medico vinculado al usuario medico (RQ-COL-01).
        collaboratorRepository.save(Collaborator.builder()
                .fullName("Dra. Ana Salazar")
                .nationalId("0102030405")
                .specialty("Medicina General")
                .position("MEDICO")
                .email("medico@clinicagrupo5.ec")
                .user(medicoUser)
                .build());

        // Paciente de ejemplo (RQ-PAC-01).
        patientRepository.save(Patient.builder()
                .nationalId("0107654321")
                .fullName("Juan Perez")
                .birthDate(LocalDate.of(1990, 5, 20))
                .gender("M")
                .phone("0991234567")
                .email("juan.perez@example.com")
                .address("Cuenca, Ecuador")
                .build());

        // Plan de cuentas basico (RQ-CONF-02).
        seedAccount("1.1.01", "Caja", "ACTIVO");
        seedAccount("1.1.02", "Bancos", "ACTIVO");
        seedAccount("4.1.01", "Ingresos por servicios medicos", "INGRESO");
        seedAccount("5.1.01", "Gastos operativos", "GASTO");

        // Configuracion institucional (RQ-CONF-03).
        configRepository.save(InstitutionConfig.builder()
                .nombre("Clinica Grupo 5")
                .ruc("0190000000001")
                .direccion("Cuenca, Ecuador")
                .telefono("072000000")
                .email("info@clinicagrupo5.ec")
                .moneda("USD")
                .build());

        log.info("DataSeeder: datos iniciales cargados. Usuario admin/admin123, medico/medico123, recepcion/recepcion123");
        log.info("DataSeeder: admin id = {}", admin.getId());
    }

    private void seedAccount(String codigo, String nombre, String tipo) {
        accountRepository.save(AccountingAccount.builder()
                .codigo(codigo).nombre(nombre).tipo(tipo).activa(true).build());
    }
}
