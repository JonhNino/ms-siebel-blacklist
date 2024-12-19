package com.banreservas.integration.exception.java;


import com.banreservas.integration.exception.SoapFaultBuilder;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;

import java.util.List;
import java.util.Map;

/**
 * Manejador de mensajes SOAP para la validación y procesamiento de solicitudes.
 * Esta clase proporciona funcionalidades para validar la integridad y estructura
 * de los mensajes SOAP entrantes, asegurando que cumplan con los requisitos establecidos.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
public class SoapMessageHandler {
    private static final String BANRESERVAS_NS = "http://banreservas.com/integration";

    /**
     * Valida un mensaje SOAP entrante.
     * Verifica que el mensaje no esté vacío y cumpla con los requisitos básicos de estructura.
     *
     * @param message El mensaje SOAP a validar
     * @throws Fault Si el mensaje está vacío o mal formado
     */
    public static void validateMessage(Message message) {
        if (isEmptyMessage(message)) {
            throw createEmptyMessageFault();
        }
    }

    /**
     * Verifica si un mensaje SOAP está vacío.
     * Comprueba tanto los headers como el contenido del mensaje para determinar si está vacío.
     *
     * @param message El mensaje SOAP a verificar
     * @return boolean Verdadero si el mensaje está vacío, falso en caso contrario
     */
    private static boolean isEmptyMessage(Message message) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        if (hasEmptyContentLength(headers)) {
            return true;
        }

        return message.getContent(java.io.InputStream.class) == null;
    }

    /**
     * Verifica si los headers del mensaje indican un contenido vacío.
     * Examina el header Content-Length para determinar si el mensaje tiene contenido.
     *
     * @param headers Los headers del mensaje SOAP a verificar
     * @return boolean Verdadero si el Content-Length indica mensaje vacío, falso en caso contrario
     */
    private static boolean hasEmptyContentLength(Map<String, List<String>> headers) {
        if (headers != null) {
            List<String> contentLengthHeader = headers.get("Content-Length");
            return contentLengthHeader != null &&
                    !contentLengthHeader.isEmpty() &&
                    "0".equals(contentLengthHeader.get(0));
        }
        return false;
    }

    /**
     * Crea una excepción Fault para mensajes SOAP vacíos.
     * Genera una excepción con información detallada sobre el error de validación.
     *
     * @return Fault Excepción conteniendo los detalles del error de validación
     */
    private static Fault createEmptyMessageFault() {
        return SoapFaultBuilder.createValidationFault(
                "El mensaje SOAP está vacío o mal formado",
                "SOAP-ERR-001",
                BANRESERVAS_NS,
                400
        );
    }
}