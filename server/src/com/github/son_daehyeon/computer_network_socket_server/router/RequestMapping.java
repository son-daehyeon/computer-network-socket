package com.github.son_daehyeon.computer_network_socket_server.router;

import com.github.son_daehyeon.computer_network_socket_server.constant.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    HttpMethod method();
    String path();
    boolean supportHead() default false;
}