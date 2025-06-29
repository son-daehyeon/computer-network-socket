package com.github.son_daehyeon.computer_network_socket_server.domain;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket_server.util.ApiException;

import java.util.Map;
import java.util.Objects;

public class AuthMiddleware {

    private static final ApiException EXCEPTION = new ApiException("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);

    public static User getUser(Map<String, String> headers) {

        String authorization = headers.get("Authorization");
        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) throw EXCEPTION;

        String token = authorization.substring("Bearer ".length());
        Session session = Session.findBySessionId(token).orElseThrow(() -> EXCEPTION);

        return User.findById(session.userId()).orElseThrow(() -> EXCEPTION);
    }
}
