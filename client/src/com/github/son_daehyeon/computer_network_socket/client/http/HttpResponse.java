package com.github.son_daehyeon.computer_network_socket.client.http;

import com.github.son_daehyeon.computer_network_socket.client.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket.client.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket.client.constant.HttpVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final Map<String, String> headers = new HashMap<>();
    private HttpVersion version;
    private HttpStatus status;
    private Map<String, String> body;

    public static HttpResponse parse(HttpRequest request, BufferedReader reader) throws IOException {

        HttpResponse response = new HttpResponse();

        parseGeneralInfo(response, reader);
        parseHeader(response, reader);
        parseBody(request, response, reader);

        return response;
    }

    private static void parseGeneralInfo(HttpResponse response, BufferedReader reader) throws IOException {

        String line;

        //noinspection StatementWithEmptyBody
        while ((line = reader.readLine()) == null);

        String[] parts = line.split(" ", 2);

        response.version = HttpVersion.fromString(parts[0]);
        response.status = HttpStatus.fromString(parts[1]);
    }

    private static void parseHeader(HttpResponse response, BufferedReader reader) throws IOException {

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(": ", 2);
            response.headers.put(parts[0], parts[1]);
        }
    }

    private static void parseBody(HttpRequest request, HttpResponse response, BufferedReader reader) throws IOException {

        if (request.getMethod().equals(HttpMethod.HEAD)) return;

        int contentLength = Integer.parseInt(response.headers.getOrDefault("Content-Length", "0"));

        if (contentLength > 0) {
            char[] chars = new char[contentLength];
            if (reader.read(chars) != contentLength) throw new IOException("body length mismatch");
            String body = new String(chars);
            response.body = parseBodyString(body);
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

    @Override
    public String toString() {

        return "Status: " + status + "\n" +
                "Headers: " + headers + "\n" +
                "Body: " + body;
    }

    public HttpVersion getVersion() {

        return version;
    }

    public HttpStatus getStatus() {

        return status;
    }

    public Map<String, String> getHeaders() {

        return headers;
    }

    public Map<String, String> getBody() {

        return body;
    }
}
