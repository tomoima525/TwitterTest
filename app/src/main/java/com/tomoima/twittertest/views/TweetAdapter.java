package com.tomoima.twittertest.views;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tomoima.twittertest.R;
import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by tomoaki on 2014/12/28.
 */
public class TweetAdapter extends ArrayAdapter<Tweet> {

    //private final int layoutResId;
    public TweetAdapter(Context context, int layoutResId){
        super(context,layoutResId);
        //this.layoutResId = layoutResId;
    }

    @Override
    public View getView(int pos, View view,ViewGroup parent){
        ViewHolder holder;
        if(view == null || view.getTag() == null){
            view = View.inflate(getContext(), R.layout.tweet_layout,null);
            //view = LayoutInflater.from(getContext()).inflate(layoutResId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        bindView(getItem(pos), holder);

        return view;
    }

    public void bindView(Tweet tweet, ViewHolder viewHolder){

        viewHolder.text.setText(Html.fromHtml(tweet.text));
        Linkify.addLinks(viewHolder.text, Linkify.ALL);
        viewHolder.time.setText(tweet.createdAt);
        viewHolder.id.setText(tweet.idStr);

    }


    static class ViewHolder {
        TextView text;
        TextView time;
        TextView id;
        public ViewHolder(View view){
            text = (TextView) view.findViewById(R.id.tweet_text);
            time = (TextView) view.findViewById(R.id.tweet_time);
            id = (TextView) view.findViewById(R.id.tweet_id);
        }
    }
}
