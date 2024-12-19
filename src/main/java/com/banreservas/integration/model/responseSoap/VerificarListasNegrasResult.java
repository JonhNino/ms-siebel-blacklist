package com.banreservas.integration.model.responseSoap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Resultado de la verificación de listas negras.
 * Esta clase representa el resultado detallado de una verificación en las listas negras,
 * incluyendo información del solicitante, detalles de la transacción y los hallazgos encontrados.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlRootElement(name = "VerificarListasNegrasResult")
@XmlAccessorType(XmlAccessType.FIELD)
@RegisterForReflection
public class VerificarListasNegrasResult {
    /**
     * Canal por el cual se realizó la consulta.
     */
    @XmlElement(name = "Canal")
    private String canal;
    /**
     * Usuario que realizó la consulta.
     */
    @XmlElement(name = "Usuario")
    private String usuario;
    /**
     * Terminal desde donde se realizó la consulta.
     */
    @XmlElement(name = "Terminal")
    private String terminal;
    /**
     * Fecha y hora de finalización de la consulta.
     */
    @XmlElement(name = "FechaHora")
    private String endDateTime;
    /**
     * Versión del servicio utilizado.
     */
    @XmlElement(name = "Version")
    private String version;
    /**
     * Identificador único de la transacción.
     */
    @XmlElement(name = "TRN_ID")
    private String transactionId;
    /**
     * Resultado de la verificación.
     */
    @XmlElement(name = "Resultado")
    private String resultado;
    /**
     * Mensaje descriptivo del resultado.
     */
    @XmlElement(name = "Mensaje")
    private String mensaje;
    /**
     * Indicador de coincidencia en listas negras.
     */
    @XmlElement(name = "Coincidencia")
    private Boolean concidencia;
    /**
     * Listas en las que se encontraron coincidencias.
     */
    @XmlElement(name = "Listas")
    private Listas listas;


    public VerificarListasNegrasResult() {
    }

    public VerificarListasNegrasResult(String canal, String usuario, String terminal, String endDateTime,
                                       String version, String transactionId, String tipo, String mensaje, Boolean concidencia, Listas listas) {
        this.canal = canal;
        this.usuario = usuario;
        this.terminal = terminal;
        this.endDateTime = endDateTime;
        this.version = version;
        this.transactionId = transactionId;
        this.resultado = tipo;
        this.mensaje = mensaje;
        this.concidencia = concidencia;
        this.listas = listas;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTipo() {
        return resultado;
    }

    public void setTipo(String resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getConcidencia() {
        return concidencia;
    }

    public void setConcidencia(Boolean concidencia) {
        this.concidencia = concidencia;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Listas getListas() {
        return listas;
    }

    public void setListas(Listas listas) {
        this.listas = listas;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "canal='" + canal + '\'' +
                ", usuario='" + usuario + '\'' +
                ", terminal='" + terminal + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", version='" + version + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", tipo='" + resultado + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", listas=" + listas +
                '}';
    }
}
