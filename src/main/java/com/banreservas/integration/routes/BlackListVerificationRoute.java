package com.banreservas.integration.routes;

import com.banreservas.integration.constants.Constants;
import com.banreservas.integration.exception.routes.ErrorHandlerConfig;
import com.banreservas.integration.processor.ResponseAggregationStrategy;
import com.banreservas.integration.utils.UuidGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.apache.camel.builder.component.ComponentsBuilderFactory.bean;


@ApplicationScoped
public class BlackListVerificationRoute extends RouteBuilder {

    @Inject
    CxfEndpoint cxfEndpoint;
    @ConfigProperty(name = "service.defraudadores.url")
    String defraudadoresUrl;

    @ConfigProperty(name = "service.restringido.url")
    String restringidoUrl;
    @ConfigProperty(name = "service.externas.url")
    String externasUrl;
    Logger loggerAudit = LoggerFactory.getLogger("ms-siebel-blacklist");


    @Inject
    UuidGenerator uuidGenerator;
    @Override
    public void configure() throws Exception {
        // Error handler global
        ErrorHandlerConfig.configureErrorHandler(this);

        // Ruta principal SOAP df
        from(cxfEndpoint)
                .routeId("SoapServiceRoute")
                .setProperty("RequestUUID", method(uuidGenerator, "generateUuid"))
                .log(LoggingLevel.INFO, "UUID generado: ${exchangeProperty.RequestUUID}")
                .log(LoggingLevel.INFO, "Iniciando procesamiento de solicitud SOAP: ${body} ")
                .unmarshal().jacksonXml()
                .setProperty("VerificarListasNegrasRequest", simple("${body}"))
                .setHeader("channel", simple("${body[request][Canal]}"))
                .setHeader("user", simple("${body[request][Usuario]}"))
                .setHeader("terminal", simple("${body[request][Terminal]}"))
                .setHeader("version", simple("${body[request][Version]}"))
                .setHeader("application", simple("${body[request][Canal]}"))
                .log(LoggingLevel.INFO,
                        "headers ${headers}")
                .setProperty("name",simple("${body[request][PrimerNombre]} ${body[request][PrimerApellido]}"))
                .log(LoggingLevel.INFO,
                        "Procesamiento VerificarListasNegrasRequest ${exchangeProperty.VerificarListasNegrasRequest}")
                .to(Constants.DIRECT_PREPARE_REQUEST)
                .to(Constants.DIRECT_PROCESS_PARALLEL)
                .bean("soapResponseProcessor", "process")
                .log(LoggingLevel.INFO, "Procesamiento completado exitosamente")
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.VerificarListasNegrasRequest} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=Success |");


        // Ruta de preparación de la solicitud
        from(Constants.DIRECT_PREPARE_REQUEST)
                .routeId("RequestPreparationRoute")
                .log(LoggingLevel.INFO, "Solicitud unmarshalled: ${body}")
                .bean("identificationValidator", "process")
                .log(LoggingLevel.INFO, "Validación completada: ${body}")
                .bean("responsesTransformer", "process")
                .log(LoggingLevel.INFO, "Transformación completada: ${body}");

        // Ruta de procesamiento paralelo
        from(Constants.DIRECT_PROCESS_PARALLEL)
                .routeId("ParallelProcessingRoute")
                .multicast()
                    .parallelProcessing(true)
                    .stopOnException()
                    .aggregationStrategy(new ResponseAggregationStrategy())
                .to(Constants.DIRECT_CALL_DEFRAUDADORES, Constants.DIRECT_CALL_RESTRINGIDO, Constants.DIRECT_CALL_EXTERNAS)
                .end()
                .log(LoggingLevel.INFO, "Respuestas agregadas: ${body}")
                .bean("finalResponseProcessor", "process")
                .log(LoggingLevel.INFO,
                        "Despus del FinalResponseProcessor: ${body} ");

        // Ruta para llamada a defraudadores
        from(Constants.DIRECT_CALL_DEFRAUDADORES)
                .routeId("DefraudadoresRoute")
                .doTry()
                    .setProperty("ServiceName", constant("defraudadores"))
                    .setProperty("originalBody", body())
                    .marshal().json()
                    .toD(defraudadoresUrl + "?bridgeEndpoint=true")
                    .setProperty("defraudadoresHttpCode", header("CamelHttpResponseCode"))
                    .log(LoggingLevel.INFO, "Código HTTP de ${exchangeProperty.ServiceName} : ${exchangeProperty.defraudadoresHttpCode}")
                    .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=Success |")
                    .to(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .doCatch(Exception.class)
                    .setProperty("defraudadoresErrorHttpCode", header("CamelHttpResponseCode"))
                    .log(LoggingLevel.ERROR, "Error en llamada a defraudadores: ${exception.message}")
                    .log(LoggingLevel.ERROR,
                            "Código HTTP de error defraudadores: ${exchangeProperty.defraudadoresErrorHttpCode}")
                    .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=Success |");

        // Ruta para llamada a restringido
        from(Constants.DIRECT_CALL_RESTRINGIDO)
                .routeId("RestringidoRoute")
                .doTry()
                    .setProperty("ServiceName", constant("restringido"))
                    .setProperty("originalBody", body())
                    .marshal().json()
                    .toD(restringidoUrl + "?bridgeEndpoint=true")
                    .setProperty("restringidoHttpCode", header("CamelHttpResponseCode"))
                    .log(LoggingLevel.INFO, "Código HTTP de ${exchangeProperty.ServiceName} : ${exchangeProperty.restringidoHttpCode}")
                    .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=Success |")
                    .to(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .doCatch(Exception.class)
                    .setProperty("restringidoErrorHttpCode", header("CamelHttpResponseCode"))
                    .log(LoggingLevel.INFO, "Body RestringidoRoute: ${body}")
                    .log(LoggingLevel.ERROR, "Error en llamada a restringido: ${exception.message}")
                    .log(LoggingLevel.ERROR,
                            "Código HTTP de error restringido: ${exchangeProperty.restringidoErrorHttpCode}")
                    .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.defraudadoresHttpCode} | errorMessage=Success |");

        // Ruta para llamada a Listas externas
        from(Constants.DIRECT_CALL_EXTERNAS)
                .routeId("ExternasRoute")
                .doTry()
                .setProperty("ServiceName", constant("externas"))
                .setProperty("originalBody", body())
                .marshal().json()
                .toD(restringidoUrl + "?bridgeEndpoint=true")
                .setProperty("externasHttpCode", header("CamelHttpResponseCode"))
                .log(LoggingLevel.INFO, "Código HTTP de ${exchangeProperty.ServiceName} : ${exchangeProperty.externasHttpCode}")
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.externasHttpCode} | errorMessage=Success |")
                .to(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .doCatch(Exception.class)
                .setProperty("restringidoErrorHttpCode", header("CamelHttpResponseCode"))
                .log(LoggingLevel.INFO, "Body externasRoute: ${body}")
                .log(LoggingLevel.ERROR, "Error en llamada a externas: ${exception.message}")
                .log(LoggingLevel.ERROR,
                        "Código HTTP de error externas: ${exchangeProperty.externasErrorHttpCode}")
                .log(LoggingLevel.INFO, loggerAudit,"sessionID=${exchangeProperty.RequestUUID} | request=${exchangeProperty.originalBody} | response=${body} | headers=${headers} | errorCode =${exchangeProperty.externasHttpCode} | errorMessage=Success |");

        // Ruta para preparar request HTTP
        from(Constants.DIRECT_PREPARE_HTTP_REQUEST)
                .routeId("HttpRequestPreparationRoute")
                .log(LoggingLevel.INFO, "Iniciando llamada a ${exchangeProperty.ServiceName} con body: ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        // Ruta para procesar respuesta HTTP
        from(Constants.DIRECT_PROCESS_HTTP_RESPONSE)
                .routeId("HttpResponseProcessingRoute")
                .unmarshal().json()
                .log(LoggingLevel.INFO, "Respuesta de ${exchangeProperty.ServiceName} recibida: ${body}");
    }
}