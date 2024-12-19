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

@ApplicationScoped
@RegisterForReflection
public class SoapMetadataBuilder {

    public VerificarListasNegrasResult createVerificarListasNegrasResult(
            VerificarListasNegrasRequest request,
            Map<String, ResponseDTO> response,
            Exchange exchange) {

        VerificarListasNegrasResult result = new VerificarListasNegrasResult();
        setBasicMetadata(result, request, exchange);
        processListResults(result, response, exchange);

        return result;
    }

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

    private void processListResults(VerificarListasNegrasResult result,
                                    Map<String, ResponseDTO> response,
                                    Exchange exchange) {
        ValidationResult validation = validateResponses(response);
        result.setConcidencia(validation.hasAnyMatch());

        if (validation.hasAnyMatch()) {
            processCoincidence(result, validation, exchange);
        } else {
            setNoCoincidenceResult(result);
        }
    }

    private ValidationResult validateResponses(Map<String, ResponseDTO> response) {
        boolean isRestringido = isValidResponse(response.get("restringido"));
        boolean isDefraudador = isValidResponse(response.get("defraudadores"));
        boolean isExternas = isValidResponse(response.get("externas"));
        return new ValidationResult(isDefraudador, isRestringido, isExternas);
    }

    private boolean isValidResponse(ResponseDTO response) {
        return response != null &&
                response.getBody() != null &&
                Boolean.TRUE.equals(response.getBody().getResult());
    }

    private void processCoincidence(VerificarListasNegrasResult result,
                                    ValidationResult validation,
                                    Exchange exchange) {
        result.setTipo("1");
        result.setMensaje(buildMessage(validation, exchange));

        if (validation.hasAnyMatch()) {
            Listas listas = createListas(validation);
            result.setListas(listas);
        }
    }

    private String buildMessage(ValidationResult validation, Exchange exchange) {
        StringBuilder mensaje = new StringBuilder("El usuario " + exchange.getProperty("name") + " se encuentra en ");

        int matchCount = 0;
        if (validation.isDefraudador()) matchCount++;
        if (validation.isRestringido()) matchCount++;
        if (validation.isExternas()) matchCount++;

        if (matchCount > 1) {
            mensaje.append("listas ");
        } else {
            mensaje.append("lista ");
        }

        if (validation.isDefraudador()) {
            mensaje.append("defraudador");
            if (validation.isRestringido() || validation.isExternas()) {
                mensaje.append(", ");
            }
        }
        if (validation.isRestringido()) {
            mensaje.append("restringido");
            if (validation.isExternas()) {
                mensaje.append(" y ");
            }
        }
        if (validation.isExternas()) {
            mensaje.append("externa");
        }

        return mensaje.toString();
    }

    private Listas createListas(ValidationResult validation) {
        Listas listas = new Listas();

        if (validation.isDefraudador()) {
            listas.getLista().add(new Lista("Defraudadores", "Interna"));
        }
        if (validation.isRestringido()) {
            listas.getLista().add(new Lista("Restringidos", "Interna"));
        }
        if (validation.isExternas()) {
            listas.getLista().add(new Lista("Externas", "Externa"));
        }

        return listas;
    }

    private void setNoCoincidenceResult(VerificarListasNegrasResult result) {
        result.setTipo("0");
        result.setMensaje("Exito");
    }
}