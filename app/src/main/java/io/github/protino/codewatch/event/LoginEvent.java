/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.event;

/**
 * Model class for login event
 *
 * @author Gurupad Mamadapur
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
