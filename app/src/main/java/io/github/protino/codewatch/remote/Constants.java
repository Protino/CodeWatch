package io.github.protino.codewatch.remote;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public final class Constants {
    private Constants(){}

    /* Oauth related constants */
    public static final String REDIRECT_URI = "codewatch://callback";
    public static final String ACCESS_CODE_PREF_KEY = "ACCESS_CODE_PREF_KEY";
    public static final String STATE_PREF_KEY = "STATE_PREF_KEY";
    public static final String ACCESS_TOKEN_PREF_KEY = "ACCESS_TOKEN_PREF_KEY";
    public static final String SCHEME = "codewatch";

    /* Stats range */
    public static final String _7_DAYS = "last_7_days";
    public static final String _30_DAYS = "last_30_days";
    public static final String _6_MONTHS = "last_6_months";
    public static final String _YEAR = "last_year";

    /* API core constants */
    public static final String API_SUFFIX ="api/v1/users/current/";


    /* Cache constants */
    public static final String WAKATIME_DATA = "WAKATIME_DATA_PREF_KEY";
}
