package com.banreservas.integration.model.responseSoap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Respuesta SOAP para la verificación de listas negras.
 * Esta clase representa la estructura principal de la respuesta SOAP
 * del servicio de verificación de listas negras, conteniendo el resultado
 * detallado de la verificación.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlRootElement(name = "VerificarListasNegrasResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@RegisterForReflection
public class VerificarListasNegrasResponse {
    /**
     * Resultado de la verificación de listas negras.
     * Contiene el resultado detallado de la verificación realizada.
     */
    @XmlElement(name = "VerificarListasNegrasResult")
    private VerificarListasNegrasResult verificarListasNegrasResult;

    public VerificarListasNegrasResponse() {
    }

    public VerificarListasNegrasResponse(VerificarListasNegrasResult verificarListasNegrasResult) {
        this.verificarListasNegrasResult = verificarListasNegrasResult;
    }

    public VerificarListasNegrasResult getVerificarListasNegrasResult() {
        return verificarListasNegrasResult;
    }

    public void setVerificarListasNegrasResult(VerificarListasNegrasResult verificarListasNegrasResult) {
        this.verificarListasNegrasResult = verificarListasNegrasResult;
    }

    @Override
    public String toString() {
        return "VerificarListasNegrasResponse{" +
                "verificarListasNegrasResult=" + verificarListasNegrasResult +
                '}';
    }
}
