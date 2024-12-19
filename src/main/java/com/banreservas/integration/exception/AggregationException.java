package com.banreservas.integration.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Excepción personalizada para errores durante el proceso de agregación.
 * Esta clase se encarga de gestionar las excepciones que ocurren durante la agregación de respuestas
 * de múltiples servicios, proporcionando información detallada sobre el error.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@RegisterForReflection
public class AggregationException extends RuntimeException {
    /**
     * Constructor para crear una nueva excepción de agregación.
     *
     * @param message El mensaje que describe el error ocurrido
     * @param cause   La causa raíz de la excepción
     */
    public AggregationException(String message, Throwable cause) {
        super(message, cause);
    }
}