package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(
                    (salt + password).getBytes(StandardCharsets.UTF_8)
            );
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String password = "test123";

        String salt1 = generateSalt();
        String hash1 = hash(password, salt1);
        String hash2 = hash(password, salt1);

        String salt2 = generateSalt();
        String hash3 = hash(password, salt2);

        System.out.println("Salt 1: " + salt1);
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Same password + same salt: " + hash1.equals(hash2));
        System.out.println("Different salt gives different hash: " + !hash1.equals(hash3));
    }
}