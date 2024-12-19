package com.banreservas.integration.model.responseSoap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Clase para representar una lista específica en la respuesta SOAP.
 * Esta clase define la estructura de una lista individual dentro de la respuesta
 * de verificación, incluyendo su nombre y origen.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlAccessorType(XmlAccessType.FIELD)
@RegisterForReflection
public class Lista {
    /**
     * Nombre de la lista.
     * Identifica el nombre específico de la lista de verificación.
     */
    @XmlElement(name = "Nombre")
    private String nombre;
    /**
     * Origen de la lista.
     * Indica la procedencia o fuente de la lista de verificación.
     */
    @XmlElement(name = "Origen")
    private String origen;

    public Lista() {
    }

    public Lista(String nombre, String origen) {
        this.nombre = nombre;
        this.origen = origen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
