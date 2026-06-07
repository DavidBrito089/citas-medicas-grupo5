package com.grupo5.citasmedicas.repository;

import com.grupo5.citasmedicas.enums.AppointmentStatus;
import com.grupo5.citasmedicas.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctorIdAndDeletedAtIsNull(UUID doctorId);

    List<Appointment> findByStatusAndDeletedAtIsNull(AppointmentStatus status);

    /** Historial de citas de un paciente por cedula (RQ-CIT-02). */
    @Query("select a from Appointment a where a.patient.nationalId = :nationalId and a.deletedAt is null order by a.startsAt desc")
    List<Appointment> findHistoryByPatientNationalId(@Param("nationalId") String nationalId);

    /**
     * Detecta solapamientos para un mismo medico (DDS - bloqueo de solapamientos).
     * Devuelve las citas activas del medico que se cruzan con el rango dado.
     */
    @Query("""
            select a from Appointment a
            where a.doctor.id = :doctorId
              and a.deletedAt is null
              and a.status in (com.grupo5.citasmedicas.enums.AppointmentStatus.PENDIENTE,
                               com.grupo5.citasmedicas.enums.AppointmentStatus.CONFIRMADA,
                               com.grupo5.citasmedicas.enums.AppointmentStatus.ATENDIDA)
              and a.startsAt < :ends
              and a.endsAt > :starts
            """)
    List<Appointment> findOverlapping(@Param("doctorId") UUID doctorId,
                                      @Param("starts") OffsetDateTime starts,
                                      @Param("ends") OffsetDateTime ends);
}
