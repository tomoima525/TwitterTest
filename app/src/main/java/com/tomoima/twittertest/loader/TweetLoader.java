package com.tomoima.twittertest.loader;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.tomoima.twittertest.Const;
import com.tomoima.twittertest.MyApplication;
import com.tomoima.twittertest.models.TweetEvent;
import com.tomoima.twittertest.models.TweetModel;
import com.tomoima.twittertest.models.TweetResponse;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import io.realm.Realm;

/**
 * Created by tomoaki on 2015/01/04.
 */
public class TweetLoader extends AsyncTaskLoader<Void> {
    private int insertMode = Const.REALM;
    private Context mContext;
    private Realm realm;
    public TweetLoader(Context context){
        super(context);
        mContext = context;
        if(insertMode == Const.REALM) {
            // Clear the realm from last time
            Realm.deleteRealmFile(context);
            // Create a new empty instance of Realm
            realm = Realm.getInstance(context);
        }
    }

    @Override
    public Void loadInBackground() {
        final long startTime = System.currentTimeMillis();
        final TweetModel tweetModel = TweetModel.getInstance(mContext);
        tweetModel.getMultipleTweets(16,null, MyApplication.getInstance().getScreenName(),new TweetModel.getTweetsCallback(){

            @Override
            public void success(List<Tweet> tweets) {
                switch (insertMode) {
                    case Const.REALM: {
                        realm.beginTransaction();
                        for(Tweet tweet : tweets){
                            TweetResponse tweetResponse = realm.createObject(TweetResponse.class);
                            tweetResponse.setId((int)tweet.id);
                            tweetResponse.setMessage(tweet.text);
                            tweetResponse.setDate(tweet.createdAt);
                        }
                        realm.commitTransaction();

                        break;
                    }
                    case Const.BULK_INSERT:{
                        int size = tweets.size();
                        ContentValues values = new ContentValues();
                        ContentValues[] valueses = new ContentValues[size];
                        for (int i = 0; i < size; i++) {
                            Tweet tweet = tweets.get(i);
                            //contentProviderにデータを保存
                            values.clear();
                            values.put(TweetEvent.TWEET_ID, tweet.id);
                            values.put(TweetEvent.MSG, tweet.text);
                            values.put(TweetEvent.TWEET_TIME, tweet.createdAt);
                            valueses[i] = values;
                        }
                        //Log.d("Tweet", "Tweets:" + valueses[size-1].get(TweetEvent.TWEET_ID));
                        getContext().getContentResolver().bulkInsert(TweetEvent.CONTENT_URI, valueses);
                        break;
                    }
                    default: {
                        ContentValues values = new ContentValues();
                        for (Tweet tweet : tweets) {

                            //contentProviderにデータを保存
                            values.clear();
                            values.put(TweetEvent.TWEET_ID, tweet.id);
                            values.put(TweetEvent.MSG, tweet.text);
                            values.put(TweetEvent.TWEET_TIME, tweet.createdAt);
                            getContext().getContentResolver().insert(TweetEvent.CONTENT_URI, values);
                        }
                        break;
                    }
                }

            }

            @Override
            public void failure(TwitterException e) {
                Log.d("error","Error:" + e.getMessage());
            }

            @Override
            public void complete() {
                long resultTime = System.currentTimeMillis() - startTime;
                Log.d(Const.TWEETS, "time taken: " + resultTime +"ms" );
                realm.close();
            }
        });
        return null;
    }


}
