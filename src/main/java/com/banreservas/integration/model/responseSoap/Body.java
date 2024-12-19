package com.banreservas.integration.model.responseSoap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Clase para el cuerpo de la respuesta SOAP de verificación.
 * Esta clase representa la estructura del cuerpo (Body) en la respuesta SOAP
 * para el servicio de verificación de listas negras.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
@RegisterForReflection
public class Body {
    /**
     * Respuesta de la verificación de listas negras.
     * Contiene la respuesta detallada del servicio de verificación.
     */
    @XmlElement(name = "VerificarListasNegrasResponse")
    private VerificarListasNegrasResponse verificarListasNegrasResponse;

    public Body() {
    }

    public Body(VerificarListasNegrasResponse verificarListasNegrasResponse) {
        this.verificarListasNegrasResponse = verificarListasNegrasResponse;
    }

    public VerificarListasNegrasResponse getVerificarListasNegrasResponse() {
        return verificarListasNegrasResponse;
    }

    public void setVerificarListasNegrasResponse(VerificarListasNegrasResponse verificarListasNegrasResponse) {
        this.verificarListasNegrasResponse = verificarListasNegrasResponse;
    }

    @Override
    public String toString() {
        return "VerificarListasNegrasResponse{" +
                "verificarListasNegrasResponse=" + verificarListasNegrasResponse +
                '}';
    }
}
