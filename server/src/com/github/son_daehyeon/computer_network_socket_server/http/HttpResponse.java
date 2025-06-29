package com.github.son_daehyeon.computer_network_socket_server.http;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket_server.constant.HttpVersion;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpResponse {

    private final StringBuilder http = new StringBuilder();

    private HttpResponse(Builder builder) {

        String body = generateBodyString(builder.body);

        writeGeneralInfo(builder.version, builder.status);
        writeHeader(builder.header, body);
        writeBody(body);
    }

    public static Builder builder() {

        return new Builder();
    }

    @Override
    public String toString() {

        return http.toString();
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

    private void writeGeneralInfo(HttpVersion version, HttpStatus status) {

        http.append(version).append(" ").append(status).append("\r\n");
    }

    private void writeHeader(Map<String, Object> header, String body) {

        if (Objects.nonNull(header)) {
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                http.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
        }

        if (Objects.nonNull(body) && !body.isEmpty()) {
            http.append("Content-Type: application/x-www-form-urlencoded\r\n");
            http.append("Content-Length: ").append(body.length()).append("\r\n");
        }
    }

    private void writeBody(String body) {

        if (Objects.nonNull(body) && !body.isBlank()) {
            http.append("\r\n").append(body);
        }
    }

    public void removeBody() {
        int bodyStartIndex = http.indexOf("\r\n\r\n");
        if (bodyStartIndex != -1) {
            http.delete(bodyStartIndex + 4, http.length());
        }
    }

    public static class Builder {

        private final Map<String, Object> header = new HashMap<>();
        private final Map<String, Object> body = new HashMap<>();
        private HttpVersion version = HttpVersion.HTTP_1_1;
        private HttpStatus status = HttpStatus.OK;

        private Builder() {

        }

        public Builder version(HttpVersion version) {

            this.version = version;
            return this;
        }

        public Builder status(HttpStatus status) {

            this.status = status;
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

        public HttpResponse build() {

            return new HttpResponse(this);
        }
    }
}