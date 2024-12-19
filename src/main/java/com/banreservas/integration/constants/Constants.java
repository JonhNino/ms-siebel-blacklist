package com.banreservas.integration.constants;

/**
 * Clase de constantes para la definición de rutas y endpoints.
 * Esta clase contiene las definiciones estáticas de las rutas utilizadas en la integración
 * para el direccionamiento de mensajes y procesamiento de solicitudes.
 *
 * @author Ing. John Niño
 * @version 1.0
 * @since 2024-12-06
 */
public class Constants {

    /**
     * Ruta directa para la preparación de solicitudes.
     * Define el endpoint para el procesamiento inicial y preparación de las peticiones.
     */
    public static final String DIRECT_PREPARE_REQUEST = "direct:prepareRequest";

    /**
     * Ruta directa para el procesamiento paralelo de solicitudes.
     * Define el endpoint para la ejecución concurrente de múltiples peticiones.
     */
    public static final String DIRECT_PROCESS_PARALLEL = "direct:processParallelRequests";

    /**
     * Ruta directa para llamadas al servicio de defraudadores.
     * Define el endpoint específico para la comunicación con el servicio de verificación de defraudadores.
     */
    public static final String DIRECT_CALL_DEFRAUDADORES = "direct:callDefraudadores";

    /**
     * Ruta directa para llamadas al servicio de restringidos.
     * Define el endpoint específico para la comunicación con el servicio de verificación de restringidos.
     */
    public static final String DIRECT_CALL_RESTRINGIDO = "direct:callRestringido";
    /**
     * Ruta directa para llamadas al servicio de externas.
     * Define el endpoint específico para la comunicación con el servicio de verificación de restringidos.
     */
    public static final String DIRECT_CALL_EXTERNAS = "direct:callExternas";

    /**
     * Ruta directa para la preparación de solicitudes HTTP.
     * Define el endpoint para la configuración y preparación de peticiones HTTP.
     */
    public static final String DIRECT_PREPARE_HTTP_REQUEST = "direct:prepareHttpRequest";

    /**
     * Ruta directa para el procesamiento de respuestas HTTP.
     * Define el endpoint para el manejo y procesamiento de las respuestas HTTP recibidas.
     */
    public static final String DIRECT_PROCESS_HTTP_RESPONSE = "direct:processHttpResponse";

}