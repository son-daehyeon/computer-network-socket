package com.github.son_daehyeon.computer_network_socket_server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record User(

        String uid,
        String email,
        String name,
        String password,
        String salt
) {

    private static final List<User> memory = new ArrayList<>();

    public static Optional<User> findById(String uid) {

        return memory.stream()
                .filter(user -> user.uid().equals(uid))
                .findFirst();
    }

    public static Optional<User> findByEmail(String email) {

        return memory.stream()
                .filter(user -> user.email().equals(email))
                .findFirst();
    }

    public static boolean existsByEmail(String email) {

        return memory.stream()
                .anyMatch(user -> user.email().equals(email));
    }

    public static void save(User user) {

        memory.add(user);

        System.out.println("[User] 저장됨. " + user);
        System.out.println();
    }

    public static void update(User user, User newUser) {

        int index = memory.indexOf(user);

        if (index != -1) {
            memory.set(index, newUser);
            System.out.println("[User] 업데이트됨. " + newUser);
        } else {
            System.out.println("[User] 업데이트 실패. 사용자 없음: " + user);
        }

        System.out.println();
    }
}
