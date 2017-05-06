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

package io.github.protino.codewatch.remote.interfaces;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.github.protino.codewatch.model.AccessToken;
import io.github.protino.codewatch.model.project.ProjectsResponse;
import io.github.protino.codewatch.model.project.summary.GenericSummaryResponse;
import io.github.protino.codewatch.model.project.summary.ProjectSummaryResponse;
import io.github.protino.codewatch.model.statistics.StatsResponse;
import io.github.protino.codewatch.model.user.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static io.github.protino.codewatch.utils.Constants.API_SUFFIX;
import static io.github.protino.codewatch.utils.Constants._30_DAYS;
import static io.github.protino.codewatch.utils.Constants._6_MONTHS;
import static io.github.protino.codewatch.utils.Constants._7_DAYS;
import static io.github.protino.codewatch.utils.Constants._YEAR;

/**
 * All relevant interfaces needed to fetch coding activity data
 * from wakatime API endpoint.
 *
 * @author Gurupad Mamadapur
 */

public interface ApiInterface {

    /**
     * Gets access token from Wakatime
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> getAccessToken(@Field("client_id") String clientId,
                                     @Field("client_secret") String clientSecret,
                                     @Field("redirect_uri") String redirectUri,
                                     @Field("grant_type") String grantType,
                                     @Field("code") String code);


    /**
     * @param range has to be one of these - {_7_DAYS, _30_DAYS, _6_MONTHS, _YEAR}
     * @return {@link StatsResponse}
     */
    @GET(API_SUFFIX + "stats/{range}")
    Call<StatsResponse> getStats(@Path("range") @Range String range);

    /**
     * List of all the projects of the user
     *
     * @return {@link ProjectsResponse}
     */
    @GET(API_SUFFIX + "projects")
    Call<ProjectsResponse> getProjects();

    /**
     * @param project Project whose summary is needed
     * @return {@link ProjectSummaryResponse}
     */
    @GET(API_SUFFIX + "summaries")
    Call<ProjectSummaryResponse> getProjectSummary(@Query("project") String project,
                                                   @Query("start") String start,
                                                   @Query("end") String end);

    /**
     * @param start start date
     * @param end   end date
     * @return {@link GenericSummaryResponse}
     */
    @GET(API_SUFFIX + "summaries")
    Call<GenericSummaryResponse> getSummary(@Query("start") String start, @Query("end") String end);

    /**
     * Returns profile information of the logged in user and not any user
     *
     * @return {@link UserResponse}
     */
    @GET(API_SUFFIX)
    Call<UserResponse> getUserProfileData();

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({_7_DAYS, _30_DAYS, _6_MONTHS, _YEAR})
    @interface Range {
    }
}
