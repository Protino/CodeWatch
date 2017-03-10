package io.github.protino.codewatch.remote;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.github.codewatchtestbase.remote.model.AccessToken;
import io.github.codewatchtestbase.remote.model.Stats;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Gurupad Mamadapur on 20-Feb-17.
 */

public interface ApiInterface {

    String _7_DAYS = "last_7_days";
    String _30_DAYS = "last_30_days";
    String _6_MONTHS = "last_6_months";
    String _YEAR = "last_year";

    /**
     * @param clientId
     * @param clientSecret
     * @param redirectUri
     * @param grantType
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> getAccessToken(@Field("client_id") String clientId,
                                     @Field("client_secret") String clientSecret,
                                     @Field("redirect_uri") String redirectUri,
                                     @Field("grant_type") String grantType,
                                     @Field("code") String code);

    @GET("api/v1/users/current/stats/{range}?timeout=10")
    Call<Stats> getStats(@Path("range") @Range String range);

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({_7_DAYS, _30_DAYS, _6_MONTHS, _YEAR})
    @interface Range {
    }
}
