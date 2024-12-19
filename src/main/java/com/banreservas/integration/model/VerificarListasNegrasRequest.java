package com.banreservas.integration.model;


import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Solicitud de verificación en listas negras.
 * Esta clase representa la estructura de una solicitud para verificar
 * la presencia de una identificación en las listas negras del sistema.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlRootElement(name = "VerificarListasNegrasRequest")
@RegisterForReflection
public class VerificarListasNegrasRequest {
    /**
     * Canal por el cual se realiza la solicitud.
     */
    @XmlElement(name = "Canal")
    private String canal;
    /**
     * Usuario que realiza la solicitud.
     */
    @XmlElement(name = "Usuario")
    private String usuario;
    /**
     * Terminal desde donde se realiza la solicitud.
     */
    @XmlElement(name = "Terminal")
    private String terminal;
    /**
     * Fecha y hora de la solicitud.
     */
    @XmlElement(name = "FechaHora")
    private String fechaHora;
    /**
     * Versión del servicio utilizado.
     */
    @XmlElement(name = "Version")
    private String version;
    /**
     * Número de identificación a verificar.
     */
    @XmlElement(name = "Identificacion")
    private String identificacion;
    /**
     * Tipo de identificación a verificar.
     */
    @XmlElement(name = "TipoIdentificacion")
    private String tipoIdentificacion;

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

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public VerificarListasNegrasRequest() {
    }

    public VerificarListasNegrasRequest(String canal, String usuario, String terminal, String fechaHora, String version, String identificacion, String tipoIdentificacion) {
        this.canal = canal;
        this.usuario = usuario;
        this.terminal = terminal;
        this.fechaHora = fechaHora;
        this.version = version;
        this.identificacion = identificacion;
        this.tipoIdentificacion = tipoIdentificacion;
    }

    @Override
    public String toString() {
        return "VerificarListasNegrasRequest{" +
                "canal='" + canal + '\'' +
                ", usuario='" + usuario + '\'' +
                ", terminal='" + terminal + '\'' +
                ", fechaHora='" + fechaHora + '\'' +
                ", version='" + version + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", tipoIdentificacion='" + tipoIdentificacion + '\'' +
                '}';
    }
}
