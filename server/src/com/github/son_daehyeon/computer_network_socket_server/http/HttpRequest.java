package com.github.son_daehyeon.computer_network_socket_server.http;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket_server.constant.HttpVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final Map<String, String> headers = new HashMap<>();
    private HttpMethod method;
    private HttpVersion version;
    private String path;
    private Map<String, String> pathVariables = new HashMap<>();
    private Map<String, String> body;

    public static HttpRequest parse(BufferedReader reader) throws IOException {

        HttpRequest request = new HttpRequest();

        parseGeneralInfo(request, reader);
        parseHeader(request, reader);
        parseBody(request, reader);

        System.out.println("======================================== 요청 ========================================");
        System.out.println(request);
        System.out.println();

        return request;
    }

    private static void parseGeneralInfo(HttpRequest request, BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null);

        String[] parts = line.split(" ");

        request.method = HttpMethod.valueOf(parts[0]);
        request.path = parts[1];
        request.version = HttpVersion.fromString(parts[2]);
    }

    private static void parseHeader(HttpRequest request, BufferedReader reader) throws IOException {

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(": ", 2);
            request.headers.put(parts[0], parts[1]);
        }
    }

    private static void parseBody(HttpRequest request, BufferedReader reader) throws IOException {

        int contentLength = Integer.parseInt(request.headers.getOrDefault("Content-Length", "0"));

        if (contentLength > 0) {
            char[] chars = new char[contentLength];
            if (reader.read(chars) != contentLength) throw new IOException("body length mismatch");
            String body = new String(chars);
            request.body = parseBodyString(body);
        }
    }

    private static Map<String, String> parseBodyString(String raw) {

        return Arrays.stream(raw.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                ));
    }

    public HttpMethod getMethod() {

        return method;
    }

    public HttpVersion getVersion() {

        return version;
    }

    public String getPath() {

        return path;
    }

    public Map<String, String> getPathVariables() {

        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {

        this.pathVariables = pathVariables;
    }

    public Map<String, String> getHeaders() {

        return headers;
    }

    public Map<String, String> getBody() {

        return body;
    }

    @Override
    public String toString() {

        return "Method: " + method + "\n" +
                "Version: " + version + "\n" +
                "Path: " + path + "\n" +
                "Path Variables: " + pathVariables + "\n" +
                "Headers: " + headers + "\n" +
                "Body: " + body;
    }
}