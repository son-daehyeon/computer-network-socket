package com.github.son_daehyeon.computer_network_socket_server.constant;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    HTTP_3_0("HTTP/3.0"),
    ;

    private final String version;

    HttpVersion(String version) {

        this.version = version;
    }

    public static HttpVersion fromString(String version) {

        for (HttpVersion httpVersion : values()) {
            if (httpVersion.version.equalsIgnoreCase(version)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP version: " + version);
    }

    @Override
    public String toString() {

        return version;
    }
}