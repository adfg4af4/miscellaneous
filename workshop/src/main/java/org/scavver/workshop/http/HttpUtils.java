package org.scavver.workshop.http;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {

    /**
     * Gets a HTTP request body with 'Content-Type: application/json' header.
     *
     * @param exchange
     * @return String|null
     * @throws IOException
     */
    public static String jsonHandler(HttpExchange exchange) throws IOException {

        List<String> requestHeader = new ArrayList<>();

        requestHeader.add("application/json");

        if (exchange.getRequestHeaders().containsValue(requestHeader)) {

            return IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8.name());

        } else {

            return null;

        }

    }

    /**
     * 200 HTTP response with a body.
     *
     * @param exchange HttpExchange
     * @param responseBody String
     * @throws IOException
     */
    public static void response(HttpExchange exchange, String responseBody) throws IOException {

        OutputStream outputStream = exchange.getResponseBody();

        exchange.getResponseHeaders().add("Content-Type", "application/json");

        exchange.sendResponseHeaders(200, responseBody.length());

        outputStream.write(responseBody.getBytes());
        outputStream.flush();
        outputStream.close();

    }

    /**
     * Empty HTTP response with custom code.
     *
     * @param exchange HttpExchange
     * @param rCode HTTP Response code
     * @throws IOException
     */
    public static void response(HttpExchange exchange, int rCode) throws IOException {

        exchange.sendResponseHeaders(rCode, -1);

    }

}
