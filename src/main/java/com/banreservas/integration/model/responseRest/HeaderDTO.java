package com.banreservas.integration.model.responseRest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * DTO para la estructura del encabezado de respuestas REST.
 * Esta clase representa la estructura del encabezado en las respuestas REST,
 * conteniendo códigos y mensajes de respuesta.
 *
 * @author Ing. John Niño
 * @since 2024-12-06
 * @version 1.0
 */
@RegisterForReflection
public class HeaderDTO {

    /**
     * Código de respuesta.
     * Representa el código numérico del resultado de la operación.
     */
    @JsonProperty("responseCode")
    private int responseCode;

    /**
     * Mensaje de respuesta.
     * Contiene el mensaje descriptivo asociado al código de respuesta.
     */
    @JsonProperty("responseMessage")
    private String responseMessage;

    // Getters y Setters
    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

