package org.scavver.workshop.http.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.scavver.workshop.Config;
import org.scavver.workshop.entity.Starship;
import org.scavver.workshop.entity.Workshop;
import org.scavver.workshop.http.HttpUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class WorkshopController {

    private static Workshop workshop;

    private static int numberOfPlaces;

    private static Starship starship;

    private static int arrivalTime;
    private static int handleTime;

    private static Queue<Starship> starships;

    private static Gson gson;

    private static int shipsInCurrentPeriod;
    private static int previousShipArrivalTime;
    private static int previousShipMaintenanceStartTime;
    private static int nextShipMaintenanceTime;

    private static int totalShips;

    private static boolean isFirstShip;

    /**
     * Initializes workshop for starships, and sets the number of places,
     * if the places range is valid returns 200 OK, else 400 Bad Request.
     *
     * @param exchange HttpExchange
     * @throws IOException
     */
    public static void numberOfPlaces(HttpExchange exchange) throws IOException {

        gson = new Gson();

        workshop = gson.fromJson(HttpUtils.jsonHandler(exchange), Workshop.class); // Deserialization

        numberOfPlaces = workshop.getNumberOfPlaces();

        if ( numberOfPlaces >= Config.MIN_PLACES && numberOfPlaces <= Config.MAX_PLACES ) {

            starships = new LinkedList<>();

            HttpUtils.response(exchange, 200); // 200 OK

        } else {

            HttpUtils.response(exchange, 400);

        }

    }

    /**
     * Handling newly came starship. Responses with HTTP code 200
     * if the starship was added to this queue, else with code 400.
     *
     * @param exchange HttpExchange
     * @throws IOException
     */
    public static void ship(HttpExchange exchange) throws IOException {

        if (totalShips >= Config.MAX_SHIPS) { HttpUtils.response(exchange, 400); }

        gson = new Gson();

        starship = gson.fromJson(HttpUtils.jsonHandler(exchange), Starship.class); // Deserialization

        arrivalTime = starship.getTimeOfArrival();
        handleTime  = starship.getHandleTime();

        if (starships == null) {

            HttpUtils.response(exchange, 400); // 400 Bad Request

        } else {

            if ((arrivalTime >= Config.MIN_ARRIVAL_TIME && arrivalTime <= Config.MAX_ARRIVAL_TIME)
                    && (handleTime >= Config.MIN_HANDLE_TIME && handleTime <= Config.MAX_HANDLE_TIME)) {

                    if (starships.offer(starship)) {

                        totalShips++;
                        HttpUtils.response(exchange, 200); // 200 OK

                    } else {

                        HttpUtils.response(exchange, 400); // 400 Bad Request

                    }

            } else {

                HttpUtils.response(exchange, 400); // 400 Bad Request

            }

        }

    }

    /**
     * Gets subsequent starship maintenance start time.
     *
     * @param exchange
     * @throws IOException
     */
    public static void next(HttpExchange exchange) throws IOException {

        if (workshop == null) {

            HttpUtils.response(exchange, 400); // 400 Bad Request

        } else {

            numberOfPlaces = workshop.getNumberOfPlaces();

        }

        if (starships == null) {

            HttpUtils.response(exchange, 200); // 400 Bad Request

        } else {

            Starship subsequentStarship = starships.poll();

            if (subsequentStarship == null) {

                HttpUtils.response(exchange, 200); // 200 OK

            } else {

                if (subsequentStarship.getTimeOfArrival() == previousShipArrivalTime) {

                    shipsInCurrentPeriod++;

                } else {

                    shipsInCurrentPeriod = 0;

                }

                if (shipsInCurrentPeriod > workshop.getNumberOfPlaces()) {

                    HttpUtils.response(exchange, "{ \"response\": " + "-1" + " }"); // 200 OK

                }

                if (isFirstShip) {

                    nextShipMaintenanceTime = 0;

                    isFirstShip = false;

                } else {

                    nextShipMaintenanceTime = previousShipMaintenanceStartTime + subsequentStarship.getHandleTime();

                }

                HttpUtils.response(exchange, "{ \"response\": " + nextShipMaintenanceTime + " }"); // 200 OK

            }

        }

    }

    static {

        shipsInCurrentPeriod = 0;
        previousShipArrivalTime = 0;
        previousShipMaintenanceStartTime = 0;
        nextShipMaintenanceTime = 0;

        totalShips = 0;

        isFirstShip = true;

    }

}
