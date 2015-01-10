package com.tomoima.twittertest.models;

import android.content.Context;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

/**
 * Created by tomoaki on 2015/01/03.
 */
public class TweetModel {

    private static TweetModel tweetModel;
    private static final int COUNT = 200;
    private TwitterApiClient mTwitterApiClient;
    private Long mLastId = 0L;

    private TweetModel(Context context){
        mTwitterApiClient = TwitterCore.getInstance().getApiClient();
    }

    public static TweetModel getInstance(Context context){
        if(tweetModel == null){
            tweetModel = new TweetModel(context);
        }
        return tweetModel;
    }


    public void getTweets(String userName, final getTweetsCallback cb){
        getTweets(null,userName,cb);
    }

    public void getTweets(Long lastId, String userName, final getTweetsCallback cb){
        getTweets(COUNT, lastId, userName, cb);
    }

    private void getTweets(int count, Long lastId, String userName, final getTweetsCallback cb){
        StatusesService statuses = mTwitterApiClient.getStatusesService();
        statuses.userTimeline(null, userName, count, null, lastId, false, false, false, true, new Callback<List<Tweet>>() {

            @Override
            public void success(Result<List<Tweet>> listResult) {

                cb.complete();
                cb.success(listResult.data);
                setLastId(listResult.data);
            }

            @Override
            public void failure(TwitterException e) {
                cb.complete();
                cb.failure(e);
            }
        });

    }

    public void getMultipleTweets(final int lot, Long lastId, final String userName,final getTweetsCallback cb){
        StatusesService statuses = mTwitterApiClient.getStatusesService();
        statuses.userTimeline(null, userName, COUNT, null, lastId, false, false, false, true, new Callback<List<Tweet>>() {

            @Override
            public void success(Result<List<Tweet>> listResult) {

                cb.success(listResult.data);

                if(lot > 0) {
                    getMultipleTweets(lot - 1, setLastId(listResult.data), userName, cb);
                } else {
                    cb.complete();
                }
            }

            @Override
            public void failure(TwitterException e) {
                cb.complete();
                cb.failure(e);
            }
        });
    }


    private Long setLastId(List<Tweet> tweets){
        mLastId = tweets.get(tweets.size()-1).id;
        return mLastId;
    }
    private Long getLastId(){
        return mLastId;
    }

    public interface getTweetsCallback {
        public void success(List<Tweet> tweets);
        public void failure(TwitterException e);
        public void complete();
    }

}
