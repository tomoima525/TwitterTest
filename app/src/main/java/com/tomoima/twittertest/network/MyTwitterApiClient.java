package com.tomoima.twittertest.network;

import com.tomoima.twittertest.network.MyTwitterApi;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;

/**
 * Created by tomoaki on 2014/12/28.
 */
public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(Session session) {
        super(session);
    }

    public MyTwitterApi getMyTwitterApi(){
        return getService(MyTwitterApi.class);
    }


}
