package com.github.son_daehyeon.computer_network_socket_server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record Session(

        String sessionId,
        String userId
) {

    private static final List<Session> memory = new ArrayList<>();

    public static Optional<Session> findBySessionId(String sessionId) {

        return memory.stream()
                .filter(session -> session.sessionId().equals(sessionId))
                .findFirst();
    }

    public static void save(Session session) {

        memory.add(session);

        System.out.println("[Session] 저장됨. " + session);
        System.out.println();
    }
}
