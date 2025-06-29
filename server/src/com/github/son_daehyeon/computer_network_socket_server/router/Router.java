package com.github.son_daehyeon.computer_network_socket_server.router;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket_server.constant.HttpStatus;
import com.github.son_daehyeon.computer_network_socket_server.constant.HttpVersion;
import com.github.son_daehyeon.computer_network_socket_server.domain.Controller;
import com.github.son_daehyeon.computer_network_socket_server.http.HttpRequest;
import com.github.son_daehyeon.computer_network_socket_server.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket_server.util.ApiException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Router {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Route> ROUTES = new ArrayList<>();

    public static void initialize() {

        Arrays.stream(Controller.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .peek(method -> method.setAccessible(true))
                .peek(method -> {
                    if (method.getAnnotation(RequestMapping.class).method().equals(HttpMethod.GET) && method.getAnnotation(RequestMapping.class).supportHead()) {
                        new Route(
                                HttpMethod.HEAD,
                                method.getAnnotation(RequestMapping.class).path(),
                                (request) -> {
                                    try {
                                        HttpResponse response = (HttpResponse) method.invoke(
                                                new Controller(),
                                                request.getBody(),
                                                request.getPathVariables(),
                                                request.getHeaders()
                                        );
                                        response.removeBody();
                                        return response;
                                    } catch (InvocationTargetException | IllegalAccessException e) {
                                        throw new RuntimeException("", e.getCause());
                                    }
                                }
                        );
                    }
                })
                .forEach(method -> new Route(
                        method.getAnnotation(RequestMapping.class).method(),
                        method.getAnnotation(RequestMapping.class).path(),
                        (request) -> {
                            try {
                                return (HttpResponse) method.invoke(
                                        new Controller(),
                                        request.getBody(),
                                        request.getPathVariables(),
                                        request.getHeaders()
                                );
                            } catch (InvocationTargetException | IllegalAccessException e) {
                                throw new RuntimeException("", e.getCause());
                            }
                        }
                ));
    }

    public static HttpResponse handle(HttpRequest request) {

        if (request.getVersion() != HttpVersion.HTTP_1_1) {

            return HttpResponse.builder().status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED).build();
        }

        try {
            for (Route route : ROUTES) {
                if (route.matches(request.getMethod(), request.getPath())) {
                    request.setPathVariables(route.parseVariables(request.getPath()));
                    return route.getHandler().apply(request);
                }
            }
            return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {

            return HttpResponse.builder()
                    .status(e.getCause() instanceof ApiException ae ? ae.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error", e.getCause().getMessage())
                    .build();
        }
    }
}