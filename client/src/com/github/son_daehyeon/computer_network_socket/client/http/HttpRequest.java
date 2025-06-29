package com.github.son_daehyeon.computer_network_socket.client.http;

import com.github.son_daehyeon.computer_network_socket.client.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket.client.constant.HttpVersion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequest {

    private final StringBuilder http = new StringBuilder();

    private final HttpMethod method;
    private final HttpVersion version;
    private final String path;
    private final Map<String, Object> headers;
    private final Map<String, Object> body;

    private HttpRequest(Builder builder) {

        String body = generateBodyString(builder.body);

        writeGeneralInfo(builder.method, builder.version, builder.path);
        writeHeader(builder.header, builder.host, body);
        writeBody(body);

        this.method = builder.method;
        this.version = builder.version;
        this.path = builder.path;
        this.headers = builder.header;
        this.body = builder.body;
    }

    public static Builder builder() {

        return new Builder();
    }

    public static Builder base() {

        return builder().version(HttpVersion.HTTP_1_1).host("127.0.0.1");
    }

    @Override
    public String toString() {

        return http.toString();
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

    public Map<String, Object> getHeaders() {

        return headers;
    }

    public Map<String, Object> getBody() {

        return body;
    }

    public HttpResponse send(BufferedWriter writer, BufferedReader reader) {

        try {
            writer.write(http.toString());
            writer.flush();

            return HttpResponse.parse(this, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateBodyString(Map<String, Object> body) {

        if (Objects.isNull(body)) return "";

        return body.entrySet().stream()
                .map(entry -> {
                    String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                    String value = URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8);

                    return key + '=' + value;
                })
                .collect(Collectors.joining("&"));
    }

    private void writeGeneralInfo(HttpMethod method, HttpVersion version, String path) {

        http.append(method.name()).append(" ").append(path).append(" ").append(version).append("\r\n");
    }

    private void writeHeader(Map<String, Object> header, String host, String body) {

        http.append("Host: ").append(host).append("\r\n");

        if (Objects.nonNull(body) && !body.isEmpty()) {
            http.append("Content-Type: application/x-www-form-urlencoded\r\n");
            http.append("Content-Length: ").append(body.length()).append("\r\n");
        }

        if (Objects.nonNull(header)) {
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                http.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
        }
    }

    private void writeBody(String body) {

        http.append("\r\n");

        if (Objects.nonNull(body) && !body.isBlank()) {
            http.append(body);
        }
    }

    public static class Builder {

        private final Map<String, Object> header = new HashMap<>();
        private final Map<String, Object> body = new HashMap<>();
        private HttpMethod method;
        private HttpVersion version = HttpVersion.HTTP_1_1;
        private String host;
        private String path;

        private Builder() {

        }

        public Builder method(HttpMethod method) {

            this.method = method;
            return this;
        }

        public Builder version(HttpVersion version) {

            this.version = version;
            return this;
        }

        public Builder host(String host) {

            this.host = host;
            return this;
        }

        public Builder path(String path) {

            this.path = path;
            return this;
        }

        public Builder header(String key, Object value) {

            this.header.put(key, value);
            return this;
        }

        public Builder body(String key, Object value) {

            this.body.put(key, value);
            return this;
        }

        public HttpRequest build() {

            Objects.requireNonNull(method, "Method cannot be null");
            Objects.requireNonNull(host, "Host cannot be null");
            Objects.requireNonNull(path, "Path cannot be null");

            return new HttpRequest(this);
        }
    }
}