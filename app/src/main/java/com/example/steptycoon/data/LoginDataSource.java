package com.example.steptycoon.data;

import com.example.steptycoon.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class tyLoginDataSource {
    // Predefined list of users and passwords
    private static final Map<String, String> VALID_USERS = new HashMap<String, String>() {{
        put("jeremiah", "pass123");
        put("hussein", "mapgame");
        put("lemia", "tycoonup");
        put("jade", "statsqueen");
    }};

    public Result<LoggedInUser> login(String username, String password) {
        try {
            if (VALID_USERS.containsKey(username) && VALID_USERS.get(username).equals(password)) {
                LoggedInUser user = new LoggedInUser(
                        username,
                        "User " + username.charAt(username.length() - 1) // Simple display name
                );
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Invalid username or password"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // No special cleanup needed for this simple implementation
    }
}