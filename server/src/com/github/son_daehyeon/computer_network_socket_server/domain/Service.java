package com.github.son_daehyeon.computer_network_socket_server.domain;

import com.github.son_daehyeon.computer_network_socket_server.util.ApiException;
import com.github.son_daehyeon.computer_network_socket_server.util.PasswordUtil;

import java.util.UUID;

public class Service {

    public String login(String email, String password) {

        User user = User.findByEmail(email).orElseThrow(() -> new ApiException("가입되지 않은 이메일입니다."));

        if (!PasswordUtil.compare(password, user.salt(), user.password())) {
            throw new ApiException("비밀번호가 일치하지 않습니다.");
        }

        String sessionId = UUID.randomUUID().toString();
        Session.save(new Session(sessionId, user.uid()));

        return sessionId;
    }

    public void register(String email, String name, String password) {

        if (User.existsByEmail(email)) throw new ApiException("이미 가입된 이메일입니다.");

        String salt = PasswordUtil.generateSalt();
        String hashed = PasswordUtil.hash(password, salt);

        User.save(new User(UUID.randomUUID().toString(), email, name, hashed, salt));
    }

    public User getOtherUser(String userId) {

        User user = User.findById(userId).orElseThrow(() -> new ApiException("존재하지 않는 사용자입니다."));

        return new User(user.uid(), user.email(), user.name(), null, null);
    }

    public User changeName(User user, String name) {

        User newUser = new User(user.uid(), user.email(), name, user.password(), user.salt());

        User.update(user, newUser);

        return newUser;
    }
}
