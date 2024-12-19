package com.banreservas.integration.model.responseRest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * DTO para la estructura del cuerpo de respuestas REST.
 * Esta clase representa la estructura del cuerpo de las respuestas REST,
 * conteniendo información sobre resultados y restricciones.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class BodyDTO {

    /**
     * Resultado de la verificación.
     * Indica el resultado booleano de la operación realizada.
     */
    @JsonProperty("result")
    private Boolean result;
    /**
     * Indicador de restricción.
     * Señala si existe alguna restricción en la verificación.
     */
    @JsonProperty("isRestricted")
    private Boolean isRestricted;

    /**
     * Detalles de restricción.
     * Contiene la información detallada sobre las restricciones encontradas.
     */
    @JsonProperty("restricted")
    private RestrictedDTO restricted;

    // Getters y Setters
    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Boolean getIsRestricted() {
        return isRestricted;
    }

    public void setIsRestricted(Boolean isRestricted) {
        this.isRestricted = isRestricted;
    }

    public RestrictedDTO getRestricted() {
        return restricted;
    }

    public void setRestricted(RestrictedDTO restricted) {
        this.restricted = restricted;
    }
}
