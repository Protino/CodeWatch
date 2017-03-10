package io.github.protino.codewatch.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Gurupad Mamadapur on 22-Feb-17.
 */

public class AuthenticationInterceptor implements Interceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private String accessToken;

    public AuthenticationInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .header(AUTHORIZATION_HEADER, "Bearer "+accessToken);
        return chain.proceed(builder.build());
    }
}
