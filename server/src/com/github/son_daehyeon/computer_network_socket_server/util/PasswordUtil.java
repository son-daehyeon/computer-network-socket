package com.github.son_daehyeon.computer_network_socket_server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

    public static String generateSalt() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[4];
        secureRandom.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hash(String password, String salt) {

        try {
            StringBuilder sb = new StringBuilder();

            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update((salt+password).getBytes(StandardCharsets.UTF_8));
            for (byte b : instance.digest()) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해싱에 실패했습니다.", e);
        }
    }

    public static boolean compare(String password, String salt, String hashed) {

        return hashed.equals(hash(password, salt));
    }
}
