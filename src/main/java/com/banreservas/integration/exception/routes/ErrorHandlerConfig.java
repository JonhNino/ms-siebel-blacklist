package com.banreservas.integration.exception.routes;

import com.banreservas.integration.exception.SoapFaultBuilder;
import com.banreservas.integration.exception.AggregationException;
import com.banreservas.integration.exception.AggregationValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlerConfig {
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String INTEGRATION_NAMESPACE = "http://banreservas.com/integration/faults";
    static Logger loggerAudit = LoggerFactory.getLogger("ms-siebel-blacklist");
    public static void configureErrorHandler(RouteBuilder routeBuilder) {
        routeBuilder.errorHandler(routeBuilder.defaultErrorHandler()
                .maximumRedeliveries(2)
                .redeliveryDelay(1000)
                .logRetryAttempted(true));

        // Manejo de errores de validación en agregación
        routeBuilder.onException(AggregationValidationException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error de validación en agregación: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    exchange.getIn().setBody(SoapFaultBuilder.createValidationFault(
                            cause.getMessage(),
                            "AGGREGATION_VALIDATION_ERROR",
                            INTEGRATION_NAMESPACE,
                            400
                    ));
                })
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${exception.message} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=${exception.message} |");

        // Manejo de errores generales de agregación
        routeBuilder.onException(AggregationException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error en agregación: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
                    exchange.getIn().setBody(SoapFaultBuilder.createValidationFault(
                            cause.getMessage(),
                            "AGGREGATION_ERROR",
                            INTEGRATION_NAMESPACE,
                            500
                    ));
                })
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${exception.message} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=${exception.message} |");

        // Manejo de excepciones de validación (400 Bad Request)
        routeBuilder.onException(IllegalArgumentException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error de validación: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            cause.getMessage(),
                            "VAL-400",
                            NAMESPACE,
                            400
                    );
                })
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${exception.message} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=${exception.message} |");

        // Manejo de excepciones de conexión (503 Service Unavailable)
        routeBuilder.onException(java.net.ConnectException.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error de conexión: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            "Servicio no disponible: " + cause.getMessage(),
                            "SERVICE_UNAVAILABLE",
                            "http://banreservas.com/integration/faults",
                            503
                    );
                })
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${exception.message} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=${exception.message} |");
        // Manejo de excepciones por defecto (500 Internal Server Error)
        routeBuilder.onException(Exception.class)
                .handled(true)
                .logHandled(true)
                .log(LoggingLevel.ERROR, "Error interno: ${exception.message}")
                .process(exchange -> {
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    throw SoapFaultBuilder.createValidationFault(
                            "Error interno del servidor: " + cause.getMessage(),
                            "INTERNAL_ERROR",
                            "http://banreservas.com/integration/faults",
                            500
                    );
                })
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${exception.message} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=${exception.message} |");
    }
}