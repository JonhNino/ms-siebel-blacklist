package com.banreservas.integration.config;

import com.banreservas.integration.exception.java.SoapMessageHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración del endpoint CXF para servicios SOAP.
 * Esta clase se encarga de gestionar la configuración y personalización de endpoints CXF
 * para la comunicación con servicios web SOAP, incluyendo interceptores, validación y logging.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
@ApplicationScoped
public class CxfEndpointConfig {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CxfEndpointConfig.class);
    @ConfigProperty(name = "blacklist.service.soap.address")
    private String address;

    /**
     * Configura y produce un endpoint CXF para la comunicación SOAP.
     *
     * @param camelContext El contexto de Camel necesario para la configuración del endpoint
     * @return CxfEndpoint Endpoint configurado con las propiedades específicas del servicio
     */
    @Produces
    @ApplicationScoped
    @Named("cxfEndpoint")
    public CxfEndpoint cxfEndpoint(CamelContext camelContext) {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setAddress(address);
        cxfEndpoint.setCamelContext(camelContext);
        cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);

        configureEndpointProperties(cxfEndpoint);
        configureInterceptors(cxfEndpoint);
        configureLogging(cxfEndpoint);

        return cxfEndpoint;
    }

    /**
     * Configura las propiedades específicas del endpoint CXF.
     * Establece configuraciones como la validación de esquema, manejo de excepciones
     * y validación de entrada.
     *
     * @param cxfEndpoint El endpoint CXF a configurar
     */
    private void configureEndpointProperties(CxfEndpoint cxfEndpoint) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", false);
        properties.put("exceptionMessageCauseEnabled", true);
        properties.put("allowEmptyInput", false);
        //   properties.put("validationEnabled", true);
        cxfEndpoint.setProperties(properties);
    }

    /**
     * Configura los interceptores para el procesamiento de mensajes SOAP.
     * Implementa la validación de mensajes SOAP entrantes mediante interceptores
     * en la fase de recepción.
     *
     * @param cxfEndpoint El endpoint CXF al que se añadirán los interceptores
     */
    public void configureInterceptors(CxfEndpoint cxfEndpoint) {
        cxfEndpoint.getInInterceptors().add(
                new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
                    @Override
                    public void handleMessage(Message message) {
                        // Agregar mensaje de depuración para verificar si el interceptor se está ejecutando
                        logger.debug("Interceptor ejecutado para validar el mensaje SOAP");

                        // Validar el mensaje
                        SoapMessageHandler.validateMessage(message);

                        // Agregar otro log para confirmar si se lanza la excepción
                        logger.debug("Validación completada en el interceptor");
                    }
                }
        );
    }

    /**
     * Configura el logging para el endpoint CXF.
     * Habilita el logging detallado de mensajes SOAP con formato pretty printing.
     *
     * @param cxfEndpoint El endpoint CXF para el cual se configurará el logging
     */
    private void configureLogging(CxfEndpoint cxfEndpoint) {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        cxfEndpoint.getFeatures().add(loggingFeature);
    }
}