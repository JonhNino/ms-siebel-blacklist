package com.banreservas.integration.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Excepción personalizada para errores de validación durante el proceso de agregación.
 * Esta clase se encarga de gestionar las excepciones que ocurren durante la validación
 * de datos en el proceso de agregación de múltiples servicios.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@RegisterForReflection
public class AggregationValidationException extends RuntimeException {
    /**
     * Excepción personalizada para errores de validación durante el proceso de agregación.
     * Esta clase se encarga de gestionar las excepciones que ocurren durante la validación
     * de datos en el proceso de agregación de múltiples servicios.
     *
     * @author Ing. John Niño
     * @version 1.0
     * @since 2024-12-06
     */
    public AggregationValidationException(String message) {
        super(message);
    }
}