package com.banreservas.integration.utils;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.UUID;

/**
 * Generador de identificadores únicos universales (UUID).
 * Esta clase se encarga de generar UUIDs únicos para el rastreo
 * y identificación de transacciones en el sistema.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@Named("uuidGenerator")
@RegisterForReflection
public class UuidGenerator {
    /**
     * Genera un nuevo UUID aleatorio.
     * Crea y retorna un identificador único universal en formato String.
     *
     * @return String El UUID generado
     */
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
