package in.edu.kristujayanti.services;

import java.util.UUID;

public class TokenGenerator {
    public static String generatePassword(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    public static String generateToken() {
        return "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
