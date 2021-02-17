package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView ivDetailProfile;
    TextView tvDetailName;
    TextView tvDetailBody;
    TextView tvDetailTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));


        tvDetailBody = findViewById(R.id.tvDetailBody);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        ivDetailProfile = findViewById(R.id.ivDetailProfile);

        tvDetailBody.setText(tweet.body);
        tvDetailName.setText(tweet.user.screenName);
        tvDetailTime.setText(tweet.getFormattedTimestamp());

        int radius = 30;
        int margin = 10;
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivDetailProfile);
    }
}