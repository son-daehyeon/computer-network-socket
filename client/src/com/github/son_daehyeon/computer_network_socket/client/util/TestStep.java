package com.github.son_daehyeon.computer_network_socket.client.util;

import com.github.son_daehyeon.computer_network_socket.client.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket.client.http.HttpRequest;
import com.github.son_daehyeon.computer_network_socket.client.http.HttpResponse;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record TestStep(

        String title,
        Supplier<HttpRequest> request,
        Consumer<HttpResponse> then
) {

    private static String sessionId;
    private static String userId;

    public static final TestStep[] testSteps = {
            new TestStep(
                    "회원가입 [POST : 201 Created]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.POST)
                            .path("/auth/register")
                            .body("email", "sondaehyeon01@gmail.com")
                            .body("name", "손대현")
                            .body("password", "p4ssw0rd")
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "로그인 [POST : 200 OK]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.POST)
                            .path("/auth/login")
                            .body("email", "sondaehyeon01@gmail.com")
                            .body("password", "p4ssw0rd")
                            .build(),
                    response -> {
                        sessionId = Arrays.stream(response.getHeaders().get("Set-Cookie").split(";"))
                                .map(raw -> Map.entry(raw.split("=", 2)[0].trim(), raw.split("=", 2)[1].trim()))
                                .filter(entry -> entry.getKey().equals("sessionId"))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("세션 ID를 찾을 수 없습니다."));

                        System.out.println("[Session] sessionId: " + sessionId);
                    }
            ),

            new TestStep(
                    "토큰으로 사용자 정보 조회 [GET : 200 OK]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.GET)
                            .path("/auth/user")
                            .header("Authorization", "Bearer " + sessionId)
                            .build(),
                    response -> {
                        String rawUser = response.getBody().get("user");
                        userId = rawUser.split("uid=")[1].split(",")[0];

                        System.out.println("[User] userId: " + userId);
                    }
            ),

            new TestStep(
                    "토큰으로 사용자 정보 조회 [HEAD : 200 OK]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.HEAD)
                            .path("/auth/user")
                            .header("Authorization", "Bearer " + sessionId)
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "사용자 이름 변경 [PUT : 401 Unauthorized]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.PUT)
                            .path("/user/name")
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "사용자 이름 변경 [PUT : 400 Bad Request]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.PUT)
                            .path("/user/name")
                            .header("Authorization", "Bearer " + sessionId)
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "사용자 이름 변경 [PUT : 200 OK]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.PUT)
                            .path("/user/name")
                            .header("Authorization", "Bearer " + sessionId)
                            .body("name", "손대현 2")
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "ID로 사용자 정보 조회 [GET : 200 OK]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.GET)
                            .path("/auth/user/" + userId)
                            .header("Authorization", "Bearer " + sessionId)
                            .build(),
                    response -> {
                    }
            ),

            new TestStep(
                    "존재하지 않는 페이지 [GET : 404 Not Found]",
                    () -> HttpRequest.base()
                            .method(HttpMethod.GET)
                            .path("/not-found")
                            .build(),
                    response -> {
                    }
            ),
    };
}
