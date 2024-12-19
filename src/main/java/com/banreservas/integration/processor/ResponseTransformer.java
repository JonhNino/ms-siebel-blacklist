package com.banreservas.integration.processor;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.Exchange;

import java.util.Map;
import java.util.Optional;

/**
 * Transformador de respuestas para el procesamiento de identificaciones.
 * Esta clase se encarga de transformar y validar las respuestas relacionadas con
 * identificaciones, asegurando la presencia y validez de los campos requeridos.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@Named("responsesTransformer")
@RegisterForReflection
public class ResponseTransformer {
    private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(ResponseTransformer.class);
    /**
     * Clave para el campo de identificación en las solicitudes.
     */
    private static final String IDENTIFICATION_KEY = "Identificacion";
    /**
     * Clave para el campo de tipo de identificación en las solicitudes.
     */
    private static final String IDENTIFICATION_TYPE_KEY = "TipoIdentificacion";

    /**
     * Procesa y transforma la respuesta contenida en el intercambio.
     * Transforma la respuesta original en un formato estandarizado.
     *
     * @param exchange El objeto Exchange conteniendo la respuesta a transformar
     * @throws IllegalStateException Si ocurre un error durante la transformación
     */
    public void process(Exchange exchange) {
        try {
            var transformedResponse = transformResponse(exchange);
            exchange.getIn().setBody(transformedResponse);

            log.debug("Response transformed successfully");
        } catch (Exception e) {
            log.error("Error transforming response: " + e.getMessage());
            throw new IllegalStateException("Error en transformación de respuesta: " + e.getMessage());
        }
    }

    /**
     * Transforma la respuesta en un formato estandarizado.
     * Extrae y valida los campos de identificación requeridos.
     *
     * @param exchange El objeto Exchange conteniendo los datos a transformar
     * @return Map<String, String> Mapa con los campos transformados
     * @throws IllegalStateException Si los datos requeridos están ausentes o son inválidos
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> transformResponse(Exchange exchange) {
        var body = Optional.ofNullable(exchange.getIn().getBody(Map.class))
                .orElseThrow(() -> new IllegalStateException("Body no puede ser null"));

        var request = Optional.ofNullable((Map<String, String>) body.get("request"))
                .orElseThrow(() -> new IllegalStateException("Request no puede ser null"));

        validateRequestFields(request);

        return Map.of(
                "identificationNumber", request.get(IDENTIFICATION_KEY),
                "identificationType", request.get(IDENTIFICATION_TYPE_KEY)
        );
    }

    /**
     * Valida los campos requeridos en la solicitud.
     * Verifica la presencia y validez de los campos de identificación.
     *
     * @param request El mapa conteniendo los campos de la solicitud
     * @throws IllegalStateException Si los campos requeridos están ausentes o son inválidos
     */
    private void validateRequestFields(Map<String, String> request) {
        if (!request.containsKey(IDENTIFICATION_KEY) || !request.containsKey(IDENTIFICATION_TYPE_KEY)) {
            throw new IllegalStateException(
                    "Los campos de identificación son requeridos en el request"
            );
        }

        if (request.get(IDENTIFICATION_KEY) == null || request.get(IDENTIFICATION_TYPE_KEY) == null) {
            throw new IllegalStateException(
                    "Los valores de identificación no pueden ser null"
            );
        }
    }
}