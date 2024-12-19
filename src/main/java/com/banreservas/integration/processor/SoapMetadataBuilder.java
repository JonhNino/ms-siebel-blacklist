package com.banreservas.integration.processor;

import com.banreservas.integration.model.ValidationResult;
import com.banreservas.integration.model.VerificarListasNegrasRequest;
import com.banreservas.integration.model.responseRest.ResponseDTO;
import com.banreservas.integration.model.responseSoap.Lista;
import com.banreservas.integration.model.responseSoap.Listas;
import com.banreservas.integration.model.responseSoap.VerificarListasNegrasResult;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Constructor de metadatos SOAP para verificación de listas negras.
 * Esta clase se encarga de construir y estructurar la respuesta SOAP con los resultados
 * de la verificación en las diferentes listas negras.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
@RegisterForReflection
public class SoapMetadataBuilder {
    /**
     * Crea el resultado de la verificación de listas negras.
     * Construye la respuesta completa con los metadatos básicos y los resultados de las verificaciones.
     *
     * @param request  La solicitud original de verificación
     * @param response Mapa con las respuestas de los diferentes servicios
     * @param exchange El objeto Exchange conteniendo información adicional
     * @return VerificarListasNegrasResult El resultado completo de la verificación
     */
    public VerificarListasNegrasResult createVerificarListasNegrasResult(
            VerificarListasNegrasRequest request,
            Map<String, ResponseDTO> response,
            Exchange exchange) {

        VerificarListasNegrasResult result = new VerificarListasNegrasResult();
        setBasicMetadata(result, request, exchange);
        processListResults(result, response, exchange);

        return result;
    }

    /**
     * Establece los metadatos básicos del resultado.
     * Configura la información general como canal, usuario, terminal y timestamps.
     *
     * @param result   El objeto resultado donde se establecerán los metadatos
     * @param request  La solicitud original con los datos básicos
     * @param exchange El objeto Exchange con información adicional
     */
    private void setBasicMetadata(VerificarListasNegrasResult result,
                                  VerificarListasNegrasRequest request,
                                  Exchange exchange) {
        result.setCanal(request.getCanal());
        result.setUsuario(request.getUsuario());
        result.setTerminal(request.getTerminal());
        result.setEndDateTime(LocalDateTime.now().toString());
        result.setVersion(request.getVersion());
        result.setTransactionId(exchange.getProperty("RequestUUID", String.class));
    }

    /**
     * Procesa los resultados de las verificaciones en las listas.
     * Analiza las respuestas y determina si hay coincidencias en las listas negras.
     *
     * @param result   El objeto resultado donde se establecerán los hallazgos
     * @param response Las respuestas de los servicios de verificación
     * @param exchange El objeto Exchange con información adicional
     */
    private void processListResults(VerificarListasNegrasResult result,
                                    Map<String, ResponseDTO> response, Exchange exchange) {
        ValidationResult validation = validateResponses(response);
        result.setConcidencia(validation.hasCoincidence());

        if (validation.hasCoincidence()) {
            processCoincidence(result, validation, exchange);
        } else {
            setNoCoincidenceResult(result);
        }
    }

    /**
     * Valida las respuestas de los servicios.
     * Verifica si hay coincidencias en las listas de defraudadores o restringidos.
     *
     * @param response Mapa con las respuestas de los servicios
     * @return ValidationResult Resultado de la validación con las coincidencias encontradas
     */
    private ValidationResult validateResponses(Map<String, ResponseDTO> response) {
        boolean isRestringido = isValidResponse(response.get("restringido"));
        boolean isDefraudador = isValidResponse(response.get("defraudadores"));
        return new ValidationResult(isDefraudador, isRestringido);
    }

    /**
     * Verifica si una respuesta individual es válida.
     * Comprueba la estructura y el resultado de la respuesta.
     *
     * @param response La respuesta a validar
     * @return boolean true si la respuesta es válida y contiene coincidencias
     */
    private boolean isValidResponse(ResponseDTO response) {
        return response != null &&
                response.getBody() != null &&
                Boolean.TRUE.equals(response.getBody().getResult());
    }

    /**
     * Procesa las coincidencias encontradas.
     * Establece el tipo y mensaje de respuesta según las coincidencias.
     *
     * @param result     El objeto resultado a actualizar
     * @param validation El resultado de la validación
     * @param exchange   El objeto Exchange con información adicional
     */
    private void processCoincidence(VerificarListasNegrasResult result,
                                    ValidationResult validation, Exchange exchange) {
        result.setTipo("1");
        result.setMensaje(buildMessage(validation, exchange));

        if (validation.hasAnyMatch()) {
            Listas listas = createListas(validation);
            result.setListas(listas);
        }
    }

    /**
     * Construye el mensaje de respuesta.
     * Genera un mensaje descriptivo según las coincidencias encontradas.
     *
     * @param validation El resultado de la validación
     * @param exchange   El objeto Exchange con información adicional
     * @return String El mensaje construido
     */
    private String buildMessage(ValidationResult validation, Exchange exchange) {
        StringBuilder mensaje = new StringBuilder("El usuario " + exchange.getProperty("name") + " se encuentra en lista");
        if (validation.isDefraudador() && validation.isRestringido()) {
            mensaje.append("s defraudador y restringido");
        } else if (validation.isDefraudador()) {
            mensaje.append(" defraudador");
        } else {
            mensaje.append(" restringido");
        }
        return mensaje.toString();
    }

    /**
     * Crea la estructura de listas con las coincidencias.
     * Genera un objeto Listas con las coincidencias encontradas.
     *
     * @param validation El resultado de la validación
     * @return Listas Objeto con las listas donde se encontraron coincidencias
     */
    private Listas createListas(ValidationResult validation) {
        Listas listas = new Listas();

        if (validation.isDefraudador()) {
            listas.getLista().add(new Lista("Defraudadores", "Interna"));
        }
        if (validation.isRestringido()) {
            listas.getLista().add(new Lista("Restringidos", "Interna"));
        }

        return listas;
    }

    /**
     * Establece el resultado para casos sin coincidencias.
     * Configura el resultado con los valores predeterminados cuando no hay coincidencias.
     *
     * @param result El objeto resultado a configurar
     */
    private void setNoCoincidenceResult(VerificarListasNegrasResult result) {
        result.setTipo("0");
        result.setMensaje("Exito");
    }

}