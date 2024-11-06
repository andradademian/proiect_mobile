package com.example.melodify_app.Model_Auxiliare;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {

    // Hash the password
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verify if the given password matches the stored hashed password
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
