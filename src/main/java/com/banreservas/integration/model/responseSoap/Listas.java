package com.banreservas.integration.model.responseSoap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor de listas en la respuesta SOAP.
 * Esta clase actúa como contenedor para múltiples listas de verificación,
 * permitiendo la agrupación de diferentes tipos de listas en la respuesta.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@XmlAccessorType(XmlAccessType.FIELD)
@RegisterForReflection
public class Listas {
    /**
     * Colección de listas de verificación.
     * Almacena todas las listas individuales que forman parte de la respuesta.
     */
    @XmlElement(name = "Lista")
    private List<Lista> lista;

    public Listas() {
        this.lista = new ArrayList<>();
    }

    public List<Lista> getLista() {
        return lista;
    }

    public void setLista(List<Lista> lista) {
        this.lista = lista;
    }


    @Override
    public String toString() {
        return "Listas{" +
                "listasControl=" + lista +
                '}';
    }
}