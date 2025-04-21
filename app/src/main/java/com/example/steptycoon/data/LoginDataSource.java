package com.example.steptycoon.data;

import com.example.steptycoon.data.model.LoggedInUser;
import com.example.steptycoon.util.PasswordHasher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    // Predefined list of users with their hashed passwords and salts
    private static final Map<String, UserCredentials> VALID_USERS = new HashMap<String, UserCredentials>() {{
        // Generate salts and hash passwords for each user
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();
        String salt3 = PasswordHasher.generateSalt();
        String salt4 = PasswordHasher.generateSalt();

        put("jeremiah", new UserCredentials(
            PasswordHasher.hashPassword("pass123", salt1),
            salt1
        ));
        put("hussein", new UserCredentials(
            PasswordHasher.hashPassword("mapgame", salt2),
            salt2
        ));
        put("lemia", new UserCredentials(
            PasswordHasher.hashPassword("tycoonup", salt3),
            salt3
        ));
        put("jade", new UserCredentials(
            PasswordHasher.hashPassword("statsqueen", salt4),
            salt4
        ));
    }};

    public Result<LoggedInUser> login(String username, String password) {
        try {
            if (VALID_USERS.containsKey(username)) {
                UserCredentials credentials = VALID_USERS.get(username);
                if (PasswordHasher.verifyPassword(password, credentials.salt, credentials.hashedPassword)) {
                    LoggedInUser user = new LoggedInUser(
                            username,
                            "User " + username.charAt(username.length() - 1) // Simple display name
                    );
                    return new Result.Success<>(user);
                }
            }
            return new Result.Error(new IOException("Invalid username or password"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // No special cleanup needed for this simple implementation
    }

    // Helper class to store hashed password and salt
    private static class UserCredentials {
        final String hashedPassword;
        final String salt;

        UserCredentials(String hashedPassword, String salt) {
            this.hashedPassword = hashedPassword;
            this.salt = salt;
        }
    }
}