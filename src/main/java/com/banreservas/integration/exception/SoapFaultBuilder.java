package com.banreservas.integration.exception;

import org.apache.cxf.interceptor.Fault;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Constructor de Faults SOAP personalizados.
 * Esta clase se encarga de crear y configurar excepciones SOAP (Faults) con información detallada
 * de los errores ocurridos durante el procesamiento de mensajes SOAP.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
public class SoapFaultBuilder {
    /**
     * Namespace del sobre SOAP utilizado en la construcción de Faults.
     */
    private static final String SOAP_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Crea un Fault SOAP con información detallada de validación.
     * Construye un Fault SOAP completo con mensaje de error, código y namespace personalizados.
     *
     * @param message    El mensaje descriptivo del error
     * @param errorCode  El código de error específico
     * @param namespace  El namespace asociado al error
     * @param statusCode El código de estado HTTP correspondiente
     * @return Fault El objeto Fault SOAP configurado
     */
    public static Fault createValidationFault(String message, String errorCode, String namespace, int statusCode) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Fault fault = new Fault(new Exception(message));
            fault.setStatusCode(statusCode);
            fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));

            return fault;
        } catch (Exception e) {
            return createBasicFault(message, statusCode);
        }
    }

    /**
     * Crea un Fault SOAP básico cuando falla la creación del Fault detallado.
     * Método de respaldo que genera un Fault con información mínima necesaria.
     *
     * @param message    El mensaje de error básico
     * @param statusCode El código de estado HTTP
     * @return Fault El objeto Fault SOAP básico
     */
    private static Fault createBasicFault(String message, int statusCode) {
        Fault fault = new Fault(new Exception(message));
        fault.setStatusCode(statusCode);
        fault.setFaultCode(new QName(SOAP_ENVELOPE_NS, "Client", "soap"));
        return fault;
    }
}
