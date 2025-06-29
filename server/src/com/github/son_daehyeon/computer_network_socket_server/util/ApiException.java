package com.github.son_daehyeon.computer_network_socket_server.util;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message) {

        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApiException(String message, HttpStatus status) {

        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {

        return status;
    }
}
