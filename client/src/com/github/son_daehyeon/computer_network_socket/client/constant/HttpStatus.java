package com.github.son_daehyeon.computer_network_socket.client.constant;

public enum HttpStatus {

    OK("200 OK"),
    CREATED("201 Created"),
    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    HTTP_VERSION_NOT_SUPPORTED("505 HTTP Version Not Supported"),
    ;

    private final String status;

    HttpStatus(String status) {

        this.status = status;
    }

    public static HttpStatus fromString(String status) {

        for (HttpStatus httpStatus : values()) {
            if (httpStatus.status.equalsIgnoreCase(status)) {
                return httpStatus;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status: " + status);
    }

    @Override
    public String toString() {

        return status;
    }
}