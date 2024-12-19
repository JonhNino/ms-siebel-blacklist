package com.banreservas.integration.model.responseRest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * DTO para la estructura completa de respuestas REST.
 * Esta clase representa la estructura general de las respuestas REST,
 * encapsulando tanto el encabezado como el cuerpo de la respuesta.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class ResponseDTO {

    /**
     * Encabezado de la respuesta.
     * Contiene la información de control y estado de la respuesta.
     */
    @JsonProperty("header")
    private HeaderDTO header;
    /**
     * Cuerpo de la respuesta.
     * Contiene los datos principales y resultados de la operación.
     */
    @JsonProperty("body")
    private BodyDTO body;

    // Getters y Setters
    public HeaderDTO getHeader() {
        return header;
    }

    public void setHeader(HeaderDTO header) {
        this.header = header;
    }

    public BodyDTO getBody() {
        return body;
    }

    public void setBody(BodyDTO body) {
        this.body = body;
    }
}

