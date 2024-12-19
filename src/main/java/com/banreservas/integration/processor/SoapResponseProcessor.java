package com.banreservas.integration.processor;

import com.banreservas.integration.model.VerificarListasNegrasRequest;
import com.banreservas.integration.model.responseRest.ResponseDTO;
import com.banreservas.integration.model.responseSoap.VerificarListasNegrasResult;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.Exchange;

import java.util.Map;

/**
 * Procesador de respuestas SOAP para el servicio de verificación de listas negras.
 * Esta clase se encarga de transformar las respuestas agregadas de los servicios REST
 * en una respuesta SOAP estandarizada.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@Named("soapResponseProcessor")
@RegisterForReflection
public class SoapResponseProcessor {
    @Inject
    SoapMetadataBuilder metadataBuilder;

    @Inject
    SoapTransformer soapTransformer;

    /**
     * Procesa y transforma las respuestas agregadas en una respuesta SOAP.
     * Recupera las respuestas REST agregadas y la solicitud original, construye la estructura
     * de respuesta SOAP y la transforma al formato final.
     *
     * @param exchange El objeto Exchange conteniendo las respuestas agregadas y la solicitud original
     * @throws IllegalArgumentException Si el body del mensaje es nulo o faltan propiedades requeridas
     * @throws Exception                Si ocurre un error durante el procesamiento de la respuesta
     */
    public void process(Exchange exchange) throws Exception {
        Map<String, ResponseDTO> aggregatedResponse = exchange.getIn().getBody(Map.class);
        if (aggregatedResponse == null) {
            throw new IllegalArgumentException("El body del mensaje no puede ser null");
        }

        Map<String, Object> verificarListasMap = exchange.getProperty("VerificarListasNegrasRequest", Map.class);
        if (verificarListasMap == null || verificarListasMap.get("request") == null) {
            throw new IllegalArgumentException("La propiedad 'VerificarListasNegrasRequest' o 'request' está ausente");
        }

        Map<String, Object> requestMap = (Map<String, Object>) verificarListasMap.get("request");
        VerificarListasNegrasRequest originalRequest = new VerificarListasNegrasRequest(
                (String) requestMap.get("Canal"),
                (String) requestMap.get("Usuario"),
                (String) requestMap.get("Terminal"),
                (String) requestMap.get("FechaHora"),
                (String) requestMap.get("Version"),
                (String) requestMap.get("Identificacion"),
                (String) requestMap.get("TipoIdentificacion")
        );

        // Usar el nuevo método unificado
        VerificarListasNegrasResult result = metadataBuilder.createVerificarListasNegrasResult(
                originalRequest,
                aggregatedResponse,
                exchange
        );
        // Transformar a respuesta SOAP
        String soapResponse = soapTransformer.transform(result);
        exchange.getMessage().setBody(soapResponse);
    }
}
