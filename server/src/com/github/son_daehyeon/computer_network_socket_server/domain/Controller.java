package com.github.son_daehyeon.computer_network_socket_server.domain;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket_server.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket_server.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket_server.router.RequestMapping;

import java.util.Map;

import static com.github.son_daehyeon.computer_network_socket_server.util.Validate.validate;

public class Controller {

    private final Service service = new Service();

    @RequestMapping(method = HttpMethod.POST, path = "/auth/login")
    public HttpResponse login(Map<String, String> body, Map<String, String> pathVariables, Map<String, String> headers) {

        validate(body, "email", "password");

        String email = body.get("email");
        String password = body.get("password");

        String sessionId = service.login(email, password);

        return HttpResponse.builder()
                .body("message", "로그인 성공")
                .header("Set-Cookie", "sessionId=" + sessionId + ";")
                .build();
    }

    @RequestMapping(method = HttpMethod.POST, path = "/auth/register")
    public HttpResponse register(Map<String, String> body, Map<String, String> pathVariables, Map<String, String> headers) {

        validate(body, "email", "name", "password");

        String email = body.get("email");
        String name = body.get("name");
        String password = body.get("password");

        service.register(email, name, password);

        return HttpResponse.builder()
                .status(HttpStatus.CREATED)
                .body("message", "회원가입 성공")
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/auth/user", supportHead = true)
    public HttpResponse getUser(Map<String, String> body, Map<String, String> pathVariables, Map<String, String> headers) {

        User user = AuthMiddleware.getUser(headers);

        return HttpResponse.builder()
                .body("message", "사용자 정보 조회 성공")
                .body("user", user)
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/auth/user/{userId}", supportHead = true)
    public HttpResponse getOtherUser(Map<String, String> body, Map<String, String> pathVariables, Map<String, String> headers) {

        User user = service.getOtherUser(pathVariables.get("userId"));

        return HttpResponse.builder()
                .body("message", "사용자 정보 조회 성공")
                .body("user", user)
                .build();
    }

    @RequestMapping(method = HttpMethod.PUT, path = "/user/name")
    public HttpResponse changeName(Map<String, String> body, Map<String, String> pathVariables, Map<String, String> headers) {

        User user = AuthMiddleware.getUser(headers);
        validate(body, "name");

        String name = body.get("name");

        User newUser = service.changeName(user, name);

        return HttpResponse.builder()
                .body("message", "사용자 이름 변경 성공")
                .body("user", newUser)
                .build();
    }
}
