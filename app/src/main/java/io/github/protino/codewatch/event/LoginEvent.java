package io.github.protino.codewatch.event;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class LoginEvent {
    private boolean loginSuccess;

    public LoginEvent() {

    }

    public LoginEvent(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public LoginEvent setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
        return this;
    }
}
