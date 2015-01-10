package com.tomoima.twittertest.network;

import com.twitter.sdk.android.core.Callback;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tomoaki on 2014/12/28.
 */
public interface MyTwitterApi {
    @GET("/1.1/statuses/user_timeline.json")
    void show(@Query("screen_name") String screenName,@Query("count") int count, Callback cb);

}
