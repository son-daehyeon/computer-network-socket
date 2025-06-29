package com.github.son_daehyeon.computer_network_socket.client;

import com.github.son_daehyeon.computer_network_socket.client.http.HttpRequest;
import com.github.son_daehyeon.computer_network_socket.client.http.HttpResponse;
import com.github.son_daehyeon.computer_network_socket.client.util.TestStep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

public class Client {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    private static final Function<HttpRequest, HttpResponse> request = (HttpRequest httpRequest) -> {
        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedWriter writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            return httpRequest.send(writer, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            for (TestStep testStep : TestStep.testSteps) {

                System.out.println("======================================== " + testStep.title() + " ========================================");
                HttpResponse response = request.apply(testStep.request().get());
                testStep.then().accept(response);
                System.out.println(Objects.isNull(response) ? "\n" : response);

                scanner.nextLine();
            }
        }
    }
}