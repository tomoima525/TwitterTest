package com.tomoima.twittertest.models;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tomoaki on 2015/01/03.
 */
public class TweetEvent implements BaseColumns {
    public static final String EVENTS_TABLE = "events";
    public static final Uri CONTENT_URI = Uri.parse("content://" + TweetProvider.AUTHORITY + "/events");
    public static final Uri CONTENT_ID_URI_BASE = Uri.parse("content://" + TweetProvider.AUTHORITY + "/events/");
    public static final String ID = "_id";
    public static final String TWEET_ID = "tweet_id";
    public static final String MSG = "msg";
    public static final String TWEET_TIME = "tweet_time";

}
