package com.banreservas.integration.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Resultado de validación para verificaciones en listas negras.
 * Esta clase almacena los resultados de las validaciones realizadas contra
 * las listas de defraudadores, restringidos y listas externas, incluyendo estados y mensajes.
 *
 * @author Ing. John Niño
 * @version 1.1
 * @since 2024-12-19
 */
@RegisterForReflection
public class ValidationResult {
    /**
     * Indica si la validación fue exitosa.
     */
    private final boolean isValid;
    /**
     * Indica si se encontró coincidencia en la lista de defraudadores.
     */
    private final boolean isDefraudador;
    /**
     * Indica si se encontró coincidencia en la lista de restringidos.
     */
    private final boolean isRestringido;
    /**
     * Indica si se encontró coincidencia en las listas externas.
     */
    private final boolean isExternas;
    /**
     * Mensaje descriptivo del resultado de la validación.
     */
    private final String message;

    // Constructor para validación de listas negras
    public ValidationResult(boolean isDefraudador, boolean isRestringido, boolean isExternas) {
        this.isDefraudador = isDefraudador;
        this.isRestringido = isRestringido;
        this.isExternas = isExternas;
        this.isValid = true;
        this.message = "OK";
    }

    // Constructor para validación de respuestas
    public ValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
        this.isDefraudador = false;
        this.isRestringido = false;
        this.isExternas = false;
    }

    // Constructor completo para casos especiales
    public ValidationResult(boolean isValid, boolean isDefraudador, boolean isRestringido,
                            boolean isExternas, String message) {
        this.isValid = isValid;
        this.isDefraudador = isDefraudador;
        this.isRestringido = isRestringido;
        this.isExternas = isExternas;
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isDefraudador() {
        return isDefraudador;
    }

    public boolean isRestringido() {
        return isRestringido;
    }

    public boolean isExternas() {
        return isExternas;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasCoincidence() {
        return isDefraudador || isRestringido || isExternas;
    }

    public boolean hasAnyMatch() {
        return isDefraudador || isRestringido || isExternas;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "isValid=" + isValid +
                ", isDefraudador=" + isDefraudador +
                ", isRestringido=" + isRestringido +
                ", isExternas=" + isExternas +
                ", message='" + message + '\'' +
                '}';
    }
}