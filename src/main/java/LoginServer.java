package main.java;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class LoginServer {
  private static Users registeredUser = new Users("testUser", "testPass", "test@example.com");
  private static Auth auth = new Auth(registeredUser);

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    // Serve static files
    server.createContext("/", new StaticFileHandler());

    // API endpoint for login
    server.createContext("/api/login", new LoginHandler());

    server.setExecutor(null);
    server.start();

    System.out.println("Server started on http://localhost:8080");
    System.out.println("Demo credentials - Username: testUser, Password: testPass");
  }

  static class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String path = exchange.getRequestURI().getPath();

      // Default to index.html
      if (path.equals("/")) {
        path = "/index.html";
      }

      try {
        // Try to serve from resources
        String resourcePath = "src/main/resource" + path;
        File file = new File(resourcePath);

        if (file.exists() && file.isFile()) {
          String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
          String contentType = getContentType(path);

          exchange.getResponseHeaders().set("Content-Type", contentType);
          exchange.sendResponseHeaders(200, content.getBytes(StandardCharsets.UTF_8).length);

          try (OutputStream os = exchange.getResponseBody()) {
            os.write(content.getBytes(StandardCharsets.UTF_8));
          }
        } else {
          // 404 Not Found
          String response = "404 - File Not Found";
          exchange.sendResponseHeaders(404, response.length());
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
          }
        }
      } catch (IOException e) {
        String response = "500 - Internal Server Error";
        exchange.sendResponseHeaders(500, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(response.getBytes());
        }
      }
    }

    private String getContentType(String path) {
      if (path.endsWith(".html"))
        return "text/html";
      if (path.endsWith(".css"))
        return "text/css";
      if (path.endsWith(".js"))
        return "application/javascript";
      if (path.endsWith(".json"))
        return "application/json";
      return "text/plain";
    }
  }

  static class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if ("POST".equals(exchange.getRequestMethod())) {
        try {
          // Read request body
          String requestBody;
          try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            requestBody = reader.lines().collect(Collectors.joining("\n"));
          }

          // Parse JSON (simple parsing for username/password)
          String username = extractJsonValue(requestBody, "username");
          String password = extractJsonValue(requestBody, "password");

          String response;
          int responseCode;

          try {
            auth.login(username, password);
            response = "{\"success\": true, \"message\": \"Login successful for user: " + username + "\"}";
            responseCode = 200;
          } catch (InvalidUserException | InvalidPasswordException e) {
            response = "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
            responseCode = 401;
          }

          exchange.getResponseHeaders().set("Content-Type", "application/json");
          exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
          exchange.sendResponseHeaders(responseCode, response.getBytes(StandardCharsets.UTF_8).length);

          try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
          }

        } catch (Exception e) {
          String response = "{\"success\": false, \"message\": \"Server error\"}";
          exchange.sendResponseHeaders(500, response.length());
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
          }
        }
      } else {
        // Method not allowed
        exchange.sendResponseHeaders(405, 0);
        exchange.getResponseBody().close();
      }
    }

    private String extractJsonValue(String json, String key) {
      String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
      java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
      java.util.regex.Matcher m = p.matcher(json);
      return m.find() ? m.group(1) : "";
    }
  }
}