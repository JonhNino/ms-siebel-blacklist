package com.banreservas.integration.processor;

import com.banreservas.integration.model.VerificarListasNegrasRequest;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Validador de identificaciones para el proceso de verificación de listas negras.
 * Esta clase se encarga de validar el formato y tipo de las identificaciones
 * entrantes antes de proceder con la verificación en los servicios correspondientes.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@Named("identificationValidator")
@RegisterForReflection
public class IdentificationValidator {
    private static final Log log = LogFactory.getLog(IdentificationValidator.class);
    /**
     * Patrón para validación de caracteres alfanuméricos.
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    /**
     * Conjunto de tipos de identificación válidos aceptados por el sistema.
     */
    private static final Set<String> VALID_ID_TYPES = Set.of(
            "Cedula",
            "Pasaporte",
            "RNC",
            "GrupoEconomico"
    );

    /**
     * Procesa y valida la identificación contenida en el intercambio.
     * Extrae la solicitud del intercambio y realiza las validaciones correspondientes.
     *
     * @param exchange El objeto Exchange conteniendo la solicitud a validar
     * @throws IllegalArgumentException Si la validación falla
     */
    public void process(Exchange exchange) {
        try {
            var request = extractRequest(exchange);
            validateIdentification(request);
            exchange.setProperty("validationPassed", true);

            log.debug("Validation passed successfully");
        } catch (Exception e) {
            log.error("Validation failed: " + e.getMessage());
            throw new IllegalArgumentException("Error en validación: " + e.getMessage());
        }
    }

    /**
     * Extrae y construye el objeto de solicitud desde el intercambio.
     * Convierte el mapa de datos recibido en un objeto VerificarListasNegrasRequest.
     *
     * @param exchange El objeto Exchange conteniendo los datos de la solicitud
     * @return VerificarListasNegrasRequest Objeto de solicitud construido
     * @throws IllegalArgumentException Si los datos requeridos están ausentes o son inválidos
     */
    private VerificarListasNegrasRequest extractRequest(Exchange exchange) {
        var body = exchange.getIn().getBody(Map.class);

        if (body == null || body.get("request") == null) {
            throw new IllegalArgumentException("Request inválido o ausente");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> requestMap = (Map<String, String>) body.get("request");

        return new VerificarListasNegrasRequest(
                requestMap.get("Canal"),
                requestMap.get("Usuario"),
                requestMap.get("Terminal"),
                requestMap.get("FechaHora"),
                requestMap.get("Version"),
                requestMap.get("Identificacion"),
                requestMap.get("TipoIdentificacion")
        );
    }

    /**
     * Valida los datos de identificación de la solicitud.
     * Verifica que la identificación y su tipo cumplan con los criterios establecidos.
     *
     * @param request La solicitud conteniendo los datos a validar
     * @throws IllegalArgumentException Si los datos no cumplen con los criterios de validación
     * @throws NullPointerException     Si algún campo requerido es nulo
     */
    private void validateIdentification(VerificarListasNegrasRequest request) {
        var identificacion = Objects.requireNonNull(request.getIdentificacion(),
                "Identificación no puede ser null");
        var tipoIdentificacion = Objects.requireNonNull(request.getTipoIdentificacion(),
                "Tipo de identificación no puede ser null");

        if (identificacion.trim().isEmpty() || tipoIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Identificación y tipo de identificación son requeridos");
        }

        if (!ALPHANUMERIC_PATTERN.matcher(identificacion.trim()).matches()) {
            throw new IllegalArgumentException(
                    "La identificación solo puede contener caracteres alfanuméricos");
        }

        if (!VALID_ID_TYPES.contains(tipoIdentificacion)) {
            throw new IllegalArgumentException(
                    "Tipo de identificación no válido: " + tipoIdentificacion);
        }
    }
}