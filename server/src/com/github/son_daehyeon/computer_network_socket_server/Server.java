package com.github.son_daehyeon.computer_network_socket_server;

import com.github.son_daehyeon.computer_network_socket_server.http.HttpRequest;
import com.github.son_daehyeon.computer_network_socket_server.router.Router;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        Router.initialize();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                try (
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {

                    writer.write(Router.handle(HttpRequest.parse(reader)).toString());
                    writer.flush();
                }
            }
        }
    }
}