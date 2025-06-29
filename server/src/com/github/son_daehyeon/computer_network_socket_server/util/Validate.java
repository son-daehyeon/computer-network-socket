package com.github.son_daehyeon.computer_network_socket_server.util;

import java.util.Map;
import java.util.Objects;

public class Validate {

    public static void validate(Map<String, String> body, String... requiredFields) {

        for (String field : requiredFields) {

            if (Objects.isNull(body) || !body.containsKey(field) || Objects.isNull(body.get(field)) || body.get(field).isBlank()) {

                throw new ApiException("%s를 입력해주세요".formatted(field));
            }
        }
    }
}
