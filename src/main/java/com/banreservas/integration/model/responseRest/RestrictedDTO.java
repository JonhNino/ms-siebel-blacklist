package com.banreservas.integration.model.responseRest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * DTO para la información detallada de personas restringidas.
 * Esta clase representa la estructura de datos para almacenar información
 * detallada sobre personas que se encuentran en listas de restricción.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class RestrictedDTO {


    /**
     * Número de identificación de la persona restringida.
     */
    @JsonProperty("identificationNumber")
    private String identificationNumber;
    /**
     * Nombre completo de la persona restringida.
     */
    @JsonProperty("name")
    private String name;
    /**
     * Alias o apodo de la persona restringida.
     */
    @JsonProperty("nickname")
    private String nickname;
    /**
     * Nacionalidad de la persona restringida.
     */
    @JsonProperty("nationality")
    private String nationality;
    /**
     * Tipo de cliente en el sistema.
     */
    @JsonProperty("clientType")
    private String clientType;
    /**
     * Motivo de la restricción.
     */
    @JsonProperty("reason")
    private String reason;
    /**
     * Número de pasaporte de la persona restringida.
     */
    @JsonProperty("passport")
    private String passport;

    // Getters y Setters
    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
