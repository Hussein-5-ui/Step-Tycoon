package com.example.steptycoon.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.steptycoon.data.LoginRepository;
import com.example.steptycoon.data.Result;
import com.example.steptycoon.data.model.LoggedInUser;
import com.example.steptycoon.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    // Define valid username-password pairs
    private static final String[][] VALID_CREDENTIALS = {
            {"jeremiah", "pass123"},
            {"hussein", "mapgame"},
            {"lemia", "tycoonup"},
            {"jade", "statsqueen"}
    };

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(username, password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        // Check if username exists in valid credentials
        for (String[] credential : VALID_CREDENTIALS) {
            if (credential[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordValid(String username, String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        // Check if password matches the username
        for (String[] credential : VALID_CREDENTIALS) {
            if (credential[0].equals(username) && credential[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
}