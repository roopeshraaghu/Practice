package com.devops;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("APP_PORT", "8080"));
        String appName = System.getenv().getOrDefault("APP_NAME", "Simple Java App");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/health", new HealthHandler());
        server.createContext("/", new RootHandler(appName));
        server.createContext("/info", new InfoHandler());

        server.setExecutor(null);
        server.start();

        System.out.println(String.format("%s started on port %d", appName, port));
    }

    static class RootHandler implements HttpHandler {
        private final String appName;

        RootHandler(String appName) {
            this.appName = appName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = String.format(
                    "{\"message\": \"Hello from DevOps Pipeline!\", \"appName\": \"%s\", \"status\": \"running\", \"timestamp\": \"%s\"}",
                    appName,
                    LocalDateTime.now().format(formatter)
            );

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = String.format(
                    "{\"status\": \"healthy\", \"timestamp\": \"%s\"}",
                    LocalDateTime.now().format(formatter)
            );

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
            Map<String, String> info = new HashMap<>();
            info.put("javaVersion", System.getProperty("java.version"));
            info.put("osName", System.getProperty("os.name"));
            info.put("availableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()));

            StringBuilder json = new StringBuilder("{");
            int count = 0;
            for (Map.Entry<String, String> entry : info.entrySet()) {
                if (count++ > 0) json.append(",");
                json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            }
            json.append("}");

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(json.toString().getBytes());
            }
        }
    }
}
