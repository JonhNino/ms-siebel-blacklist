package com.banreservas.integration.processor;

import com.banreservas.integration.exception.AggregationException;
import com.banreservas.integration.exception.AggregationValidationException;
import com.banreservas.integration.model.ValidationResult;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static io.quarkus.arc.ComponentsProvider.LOG;

@RegisterForReflection
public class ResponseAggregationStrategy implements AggregationStrategy {
    private static final String AGGREGATED_RESPONSES = "AggregatedResponses";
    private final ConcurrentHashMap<String, Boolean> processedServices = new ConcurrentHashMap<>();

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        try {
            // Si es la primera llamada, inicializar el mapa de respuestas agregadas
            if (oldExchange == null) {
                processNewExchange(newExchange);
                return newExchange;
            }

            // Obtener el mapa actual de respuestas agregadas
            @SuppressWarnings("unchecked")
            Map<String, Object> aggregatedResponse = oldExchange.getIn().getBody(Map.class);

            // Si el mapa es null, inicializarlo
            if (aggregatedResponse == null) {
                aggregatedResponse = new ConcurrentHashMap<>();
            }

            // Procesar la nueva respuesta
            String newServiceName = newExchange.getProperty("ServiceName", String.class);
            Map<String, Object> newResponse = newExchange.getIn().getBody(Map.class);

            LOG.debugf("Processing response for service: {} with content: {}", newServiceName, newResponse);

            // Validar la nueva respuesta
            ValidationResult validation = validateResponse(newResponse, newServiceName);
            if (!validation.isValid()) {
                throw new AggregationValidationException(
                        String.format("Error en validación de respuesta - %s: %s",
                                newServiceName, validation.getMessage())
                );
            }

            // Agregar la respuesta al mapa agregado
            aggregatedResponse.put(newServiceName, newResponse);
            processedServices.put(newServiceName, true);

            // Actualizar el cuerpo del exchange
            oldExchange.getIn().setBody(aggregatedResponse);

            // Si tenemos las tres respuestas, realizar la validación final
            if (processedServices.size() == 3) {
                validateAllResponses(aggregatedResponse);
                // Limpiar el mapa de servicios procesados para la siguiente agregación
                processedServices.clear();
            }

            return oldExchange;

        } catch (AggregationValidationException e) {
            LOG.errorf("Error de validación en agregación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Error inesperado en agregación: {}", e.getMessage(), e);
            throw new AggregationException("Error inesperado durante la agregación", e);
        }
    }

    private void processNewExchange(Exchange exchange) {
        String serviceName = exchange.getProperty("ServiceName", String.class);
        Map<String, Object> response = exchange.getIn().getBody(Map.class);

        LOG.debugf("Processing first exchange for service: {} with content: {}", serviceName, response);

        ValidationResult validation = validateResponse(response, serviceName);
        if (!validation.isValid()) {
            throw new AggregationValidationException(
                    String.format("Error en validación de respuesta inicial - %s: %s",
                            serviceName, validation.getMessage())
            );
        }

        Map<String, Object> initialResponse = new ConcurrentHashMap<>();
        initialResponse.put(serviceName, response);
        processedServices.put(serviceName, true);

        exchange.getIn().setBody(initialResponse);
    }

    private void validateAllResponses(Map<String, Object> aggregatedResponse) {
        String[] requiredServices = {"defraudadores", "restringido", "externas"};
        for (String service : requiredServices) {
            if (!aggregatedResponse.containsKey(service)) {
                LOG.errorf("Falta la respuesta del servicio: {} en el mapa agregado: {}", service, aggregatedResponse);
                throw new AggregationValidationException(
                        String.format("Falta la respuesta del servicio: %s", service)
                );
            }
        }
        LOG.debug("Todas las respuestas requeridas están presentes y son válidas");
    }

    private ValidationResult validateResponse(Map<String, Object> response, String serviceName) {
        if (response == null) {
            return new ValidationResult(false, serviceName + ": Respuesta nula");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> header = (Map<String, Object>) response.get("header");

        if (header == null) {
            return new ValidationResult(false, serviceName + ": Header no encontrado");
        }

        Object responseCode = header.get("responseCode");
        if (responseCode == null) {
            return new ValidationResult(false, serviceName + ": Código de respuesta no encontrado");
        }

        int code = Integer.parseInt(responseCode.toString());
        if (isValidResponseCode(code)) {
            return new ValidationResult(true, "OK");
        } else {
            return new ValidationResult(false, serviceName + ": Código de respuesta inválido: " + code);
        }
    }

    private boolean isValidResponseCode(int code) {
        return code == 200 || code == 400 || code == 404 || code == 500 || code == 503;
    }
}