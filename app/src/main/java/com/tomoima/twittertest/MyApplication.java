package com.tomoima.twittertest;

import android.app.Application;

/**
 * Created by tomoaki on 2015/01/04.
 */
public class MyApplication extends Application{

    private static MyApplication mApp;
    private String mScreenName;

    private MyApplication(){

    }

    public static MyApplication getInstance(){
        if(mApp == null){
            mApp = new MyApplication();
        }
        return mApp;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mApp = this;
    }

    public void setScreenName(String screenName){
        mScreenName = screenName;
    }

    public String getScreenName(){
        return mScreenName;

    }

}
