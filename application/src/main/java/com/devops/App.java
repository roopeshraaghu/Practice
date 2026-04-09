package com.devops;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final Gson gson = new Gson();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("APP_PORT", "8080"));
        String appName = System.getenv().getOrDefault("APP_NAME", "Simple Java App");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Health check endpoint
        server.createContext("/health", new HealthHandler());

        // Main endpoint
        server.createContext("/", new RootHandler(appName));

        // Info endpoint
        server.createContext("/info", new InfoHandler());

        // Metrics endpoint
        server.createContext("/metrics", new MetricsHandler());

        server.setExecutor(null);
        server.start();

        logger.info(String.format("%s started on port %d", appName, port));
        logger.info("Available endpoints: /, /health, /info, /metrics");
    }

    static class RootHandler implements HttpHandler {
        private final String appName;

        RootHandler(String appName) {
            this.appName = appName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = String.format(
                    "{\n" +
                            "  \"message\": \"Hello from DevOps Pipeline!\",\n" +
                            "  \"appName\": \"%s\",\n" +
                            "  \"status\": \"running\",\n" +
                            "  \"timestamp\": \"%s\",\n" +
                            "  \"version\": \"1.0.0\"\n" +
                            "}",
                    appName,
                    LocalDateTime.now().format(formatter)
            );

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            logger.info("Root endpoint accessed");
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            JsonObject health = new JsonObject();
            health.addProperty("status", "healthy");
            health.addProperty("timestamp", LocalDateTime.now().format(formatter));

            String response = gson.toJson(health);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> info = new HashMap<>();
            info.put("javaVersion", System.getProperty("java.version"));
            info.put("osName", System.getProperty("os.name"));
            info.put("osArch", System.getProperty("os.arch"));
            info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
            info.put("environment", System.getenv().getOrDefault("APP_ENV", "development"));

            String response = gson.toJson(info);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("freeMemory", Runtime.getRuntime().freeMemory());
            metrics.put("totalMemory", Runtime.getRuntime().totalMemory());
            metrics.put("maxMemory", Runtime.getRuntime().maxMemory());
            metrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());

            String response = gson.toJson(metrics);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}