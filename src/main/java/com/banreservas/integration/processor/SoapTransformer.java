package com.banreservas.integration.processor;


import com.banreservas.integration.model.responseSoap.VerificarListasNegrasResult;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.jboss.logging.Logger;

import java.io.StringWriter;

/**
 * Transformador de respuestas al formato SOAP.
 * Esta clase se encarga de convertir los resultados de verificación
 * en mensajes SOAP XML correctamente formateados.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@RegisterForReflection
public class SoapTransformer {
    private static final Logger logger = Logger.getLogger(SoapTransformer.class);

    /**
     * Transforma un resultado de verificación en una respuesta SOAP XML.
     * Convierte el objeto resultado en una cadena XML formateada siguiendo
     * la estructura SOAP requerida, incluyendo los elementos Body y Response.
     *
     * @param result El resultado de verificación a transformar
     * @return String La respuesta SOAP XML formateada
     * @throws RuntimeException Si ocurre un error durante la transformación
     */
    public String transform(VerificarListasNegrasResult result) {
        try {
            JAXBContext context = JAXBContext.newInstance(VerificarListasNegrasResult.class);
            StringWriter writer = new StringWriter();

            // Configurar el marshaller
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // Evitar la declaración XML
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            // Agregar la respuesta
            writer.write("<Body>");
            writer.write("<VerificarListasNegrasResponse>");
            marshaller.marshal(result, writer);
            writer.write("</VerificarListasNegrasResponse>");
            writer.write("</Body>");

            String xmlResult = writer.toString();
            logger.info("XML generado: " + xmlResult);

            return xmlResult;
        } catch (Exception e) {
            logger.error("Error creando respuesta SOAP", e);
            throw new RuntimeException("Error creando respuesta SOAP", e);
        }
    }
}