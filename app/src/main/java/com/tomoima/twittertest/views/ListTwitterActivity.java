package com.tomoima.twittertest.views;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.tomoima.twittertest.Const;
import com.tomoima.twittertest.R;
import com.tomoima.twittertest.models.TweetEvent;
import com.tomoima.twittertest.loader.TweetLoader;
import com.tomoima.twittertest.models.TweetProvider;
import com.tomoima.twittertest.models.TweetResponse;

import io.realm.Realm;


public class ListTwitterActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Void> {
    int insertMode = Const.REALM;
    TweetAdapter adapter;
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_twitter);
        mListView = (ListView) findViewById(R.id.tweet_list);
        adapter = new TweetAdapter(this,R.layout.tweet_layout);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0,null,this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_twitter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alltweets) {
            String[] proj = new String[]{TweetEvent.TWEET_ID,TweetEvent.MSG,TweetEvent.TWEET_TIME};
            Cursor c = getContentResolver().query(TweetEvent.CONTENT_URI,proj,null,null,null,null);
            if(c != null && c.moveToFirst()){
                do {
                    Log.d(Const.TWEETS, "Id: " + c.getString(0) +"Tweet:" + c.getString(1) + "time:" + c.getString(2));
                } while(c.moveToNext());
                c.close();
            }
            return true;
        } else if (id == R.id.action_count){
            switch(insertMode){
                case Const.REALM:
                    // Update the realm object affected by the user
                    Realm realm = Realm.getInstance(this);
                    long count = realm.where(TweetResponse.class).count();
                    Log.d(Const.TWEETS,"tweets:" + count);
                    break;
                default:
                    Toast.makeText(this, "tweets:" + TweetProvider.getCount(this), Toast.LENGTH_SHORT);
                    Log.d(Const.TWEETS,"tweets:" + TweetProvider.getCount(this));
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        TweetLoader tweetLoader = new TweetLoader(getApplicationContext());
        tweetLoader.forceLoad();
        return tweetLoader;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }
}
