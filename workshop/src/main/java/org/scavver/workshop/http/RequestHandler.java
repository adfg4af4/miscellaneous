package org.scavver.workshop.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.scavver.workshop.http.controller.WorkshopController;

import java.io.IOException;

public class RequestHandler implements HttpHandler {

    private String requestMethod;
    private String requestURI;

    @Override
    public void handle (HttpExchange exchange) throws IOException {

        requestURI    = exchange.getRequestURI().toString();
        requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {

            /** Handling GET requests */

            case "GET":
                switch (requestURI) {

                    case "/next":
                        WorkshopController.next(exchange); // 200 OK || 400 Bad Request
                        break;

                    default:
                        HttpUtils.response(exchange, 404); // 404 Not Found

                }
                break;

            /** Handling POST requests */

            case "POST":
                switch (requestURI) {

                    case "/numberOfPlaces":
                        WorkshopController.numberOfPlaces(exchange); // 200 OK || 400 Bad Request
                        break;

                    case "/ship":
                        WorkshopController.ship(exchange); // 200 OK || 400 Bad Request
                        break;

                    default:
                        HttpUtils.response(exchange, 404); // 404 Not Found

                }
                break;

            default:
                HttpUtils.response(exchange, 404); // 404 Not Found

        }

    }

}
