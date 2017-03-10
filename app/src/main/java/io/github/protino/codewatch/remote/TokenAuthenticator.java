package io.github.protino.codewatch.remote;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Gurupad Mamadapur on 22-Feb-17.
 */

public class TokenAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        //refresh access_token
        return null;
    }
}
