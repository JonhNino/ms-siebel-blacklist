package com.banreservas.integration.processor;

import com.banreservas.integration.exception.SoapFaultBuilder;
import com.banreservas.integration.model.responseRest.BodyDTO;
import com.banreservas.integration.model.responseRest.HeaderDTO;
import com.banreservas.integration.model.responseRest.ResponseDTO;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Procesador para la generación de respuestas finales de servicios integrados.
 * Esta clase se encarga de procesar, validar y transformar las respuestas agregadas
 * de los servicios de defraudadores, restringidos y listas externas en un formato estandarizado.
 *
 * @author Ing. John Niño
 * @version 1.1
 * @since 2024-12-19
 */
@ApplicationScoped
@Named("finalResponseProcessor")
@RegisterForReflection
public class FinalResponseProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FinalResponseProcessor.class);
    private static final String[] REQUIRED_SERVICES = {"defraudadores", "restringido", "externas"};

    /**
     * Procesa el intercambio para generar la respuesta final combinada.
     * Transforma y valida las respuestas de los tres servicios en un formato unificado.
     *
     * @param exchange El objeto Exchange conteniendo las respuestas agregadas
     * @throws Exception Si ocurre un error durante el procesamiento
     */
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> aggregatedResponses = exchange.getIn().getBody(Map.class);

        LOG.info("Procesando respuesta final: {}", aggregatedResponses);

        // Verificar que todas las respuestas requeridas estén presentes
        validateRequiredResponses(aggregatedResponses);

        // Mapear todas las respuestas a DTOs
        Map<String, ResponseDTO> mappedResponses = new HashMap<>();
        for (String service : REQUIRED_SERVICES) {
            mappedResponses.put(service, mapToDto(aggregatedResponses.get(service)));
        }

        // Validar todas las respuestas mapeadas
        if (!isValidFinalResponse(mappedResponses)) {
            LOG.error("Validación fallida para las respuestas: {}", mappedResponses);
            throw SoapFaultBuilder.createValidationFault(
                    "Error en la validación de las respuestas finales",
                    "VALIDATION_ERROR",
                    "http://banreservas.com/faults",
                    400
            );
        }

        // Configurar la respuesta final
        exchange.getIn().setBody(mappedResponses);
    }

    /**
     * Valida que todas las respuestas requeridas estén presentes.
     *
     * @param responses El mapa de respuestas a validar
     * @throws Exception Si falta alguna respuesta requerida
     */
    private void validateRequiredResponses(Map<String, Object> responses) throws Exception {
        for (String service : REQUIRED_SERVICES) {
            if (!responses.containsKey(service)) {
                String error = String.format("Falta la respuesta del servicio: %s", service);
                LOG.error(error);
                throw SoapFaultBuilder.createValidationFault(
                        error,
                        "MISSING_SERVICE_RESPONSE",
                        "http://banreservas.com/faults",
                        400
                );
            }
        }
    }

    /**
     * Mapea una respuesta genérica a un objeto ResponseDTO.
     * Convierte la estructura de datos Map en un objeto DTO estandarizado.
     *
     * @param response El objeto respuesta a mapear
     * @return ResponseDTO El objeto DTO resultante
     * @throws IllegalArgumentException Si el mapeo no puede completarse
     */
    private ResponseDTO mapToDto(Object response) {
        if (response == null) {
            LOG.warn("Respuesta nula para deserializar.");
            return null;
        }

        if (!(response instanceof Map)) {
            LOG.error("El objeto de respuesta no es un Map. Objeto recibido: {}", response);
            throw new IllegalArgumentException("El objeto recibido no es un Map.");
        }

        try {
            Map<String, Object> responseMap = (Map<String, Object>) response;

            ResponseDTO responseDTO = new ResponseDTO();

            // Mapear el header
            Map<String, Object> headerMap = (Map<String, Object>) responseMap.get("header");
            if (headerMap != null) {
                HeaderDTO headerDTO = new HeaderDTO();
                headerDTO.setResponseCode((Integer) headerMap.get("responseCode"));
                headerDTO.setResponseMessage((String) headerMap.get("responseMessage"));
                responseDTO.setHeader(headerDTO);
            }

            // Mapear el body
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap != null) {
                BodyDTO bodyDTO = new BodyDTO();
                bodyDTO.setResult((Boolean) bodyMap.get("result"));
                responseDTO.setBody(bodyDTO);
            }

            return responseDTO;
        } catch (Exception e) {
            LOG.error("Error mapeando la respuesta al DTO: {}", response, e);
            throw new IllegalArgumentException("Error mapeando la respuesta al ResponseDTO", e);
        }
    }

    /**
     * Valida todas las respuestas finales.
     * Verifica que todas las respuestas de los servicios sean válidas.
     *
     * @param responses Mapa con todas las respuestas a validar
     * @return boolean true si todas las respuestas son válidas, false en caso contrario
     */
    private boolean isValidFinalResponse(Map<String, ResponseDTO> responses) {
        return responses.values().stream().allMatch(this::isValidResponse);
    }

    /**
     * Valida una respuesta individual.
     * Verifica que la respuesta tenga la estructura correcta y un código de respuesta válido.
     *
     * @param response La respuesta a validar
     * @return boolean true si la respuesta es válida, false en caso contrario
     */
    private boolean isValidResponse(ResponseDTO response) {
        if (response == null || response.getHeader() == null) {
            LOG.warn("Respuesta o encabezado inválido: {}", response);
            return false;
        }

        boolean isValid = Integer.valueOf(200).equals(response.getHeader().getResponseCode());
        if (!isValid) {
            LOG.warn("Código de respuesta inválido: {}", response.getHeader().getResponseCode());
        }
        return isValid;
    }
}