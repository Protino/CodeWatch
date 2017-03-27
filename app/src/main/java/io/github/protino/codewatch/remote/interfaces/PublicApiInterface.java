package io.github.protino.codewatch.remote.interfaces;

import io.github.protino.codewatch.model.leaders.LeadersResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Gurupad Mamadapur on 20-Feb-17.
 */

public interface PublicApiInterface {

    /**
     * Returns leaderboard data based on coding average
     * @return {@link LeadersResponse}
     */
    @GET("api/v1/leaders")
    Call<LeadersResponse> getLeaders();

}
