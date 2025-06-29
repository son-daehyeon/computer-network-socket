package com.github.son_daehyeon.computer_network_socket_server.router;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpMethod;
import com.github.son_daehyeon.computer_network_socket_server.http.HttpRequest;
import com.github.son_daehyeon.computer_network_socket_server.http.HttpResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    private final HttpMethod method;
    private final Function<HttpRequest, HttpResponse> handler;
    private final Pattern pattern;
    private final List<String> variables;

    public Route(HttpMethod method, String path, Function<HttpRequest, HttpResponse> handler) {

        this.method = method;
        this.handler = handler;
        this.pattern = Pattern.compile("^" + path.replaceAll("\\{[^}]+}", "([^/]+)") + "$");
        this.variables = parseVariableNames(path);

        registerRouter(method, path);
    }

    public HttpMethod getMethod() {

        return method;
    }

    public String getPath() {

        return pattern.pattern();
    }

    public Function<HttpRequest, HttpResponse> getHandler() {

        return handler;
    }

    public boolean matches(HttpMethod requestMethod, String requestPath) {

        return this.method.equals(requestMethod) && pattern.matcher(requestPath).matches();
    }

    private List<String> parseVariableNames(String pattern) {

        List<String> names = new ArrayList<>();
        Pattern variablePattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = variablePattern.matcher(pattern);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }

        return names;
    }

    public Map<String, String> parseVariables(String requestPath) {

        Map<String, String> variables = new HashMap<>();
        Matcher matcher = pattern.matcher(requestPath);

        if (matcher.matches()) {
            for (int i = 0; i < this.variables.size(); i++) {
                variables.put(this.variables.get(i), matcher.group(i + 1));
            }
        }

        return variables;
    }

    private void registerRouter(HttpMethod method, String path) {

        try {
            Field routes = Router.class.getDeclaredField("ROUTES");
            routes.setAccessible(true);

            //noinspection unchecked
            ((ArrayList<Route>) routes.get(null)).add(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}