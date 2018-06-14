package com.internship.thien.flicks.data.remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://api.themoviedb.org";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
