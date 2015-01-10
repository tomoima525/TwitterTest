package com.tomoima.twittertest.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tomoima.twittertest.BuildConfig;
import com.tomoima.twittertest.Const;
import com.tomoima.twittertest.MyApplication;
import com.tomoima.twittertest.R;
import com.tomoima.twittertest.views.ListTwitterActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity {

    private TwitterLoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        final TextView loginStatus = (TextView) findViewById(R.id.login_status);

        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken;
        if (session != null) {
            authToken = session.getAuthToken();
            if (authToken.token != null) {
                loginStatus.setText("Login token: " + authToken.token);
                getScreenName();
            }
        } else {
            loginStatus.setText("Not Logged in");
        }
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
              TwitterAuthToken authToken = result.data.getAuthToken();
                Log.d(Const.TWEETS,"success! token: " + authToken.token);
                getScreenName();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    public void gotoListTwitterActivity(){

        startActivity(new Intent(this, ListTwitterActivity.class));
    }

    public void getScreenName(){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService account = twitterApiClient.getAccountService();
        account.verifyCredentials(true,false, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                MyApplication.getInstance().setScreenName(userResult.data.screenName);
                Log.d(Const.TWEETS, "screenName:" + MyApplication.getInstance().getScreenName());
                gotoListTwitterActivity();
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
