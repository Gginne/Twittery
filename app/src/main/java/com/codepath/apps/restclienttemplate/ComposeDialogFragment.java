package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import okhttp3.Headers;
// ...

public class ComposeDialogFragment extends DialogFragment {
    public static final int MAX_TWEET_LENGTH = 280;
    private static final String TAG = "ComposeDialog";
    private EditText composeInput;
    private Button composeBtn;
    private TextView composeChars;
    TwitterClient client;

    // 1. Defines the listener interface with a method passing back data result.
    public interface TweetPublishListener {
        void onPublishTweet(Tweet tweet);
    }

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(String title) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Get elements from view
        composeInput = (EditText) view.findViewById(R.id.compose_input);
        composeBtn = (Button) view.findViewById(R.id.compose_btn);
        composeChars = (TextView) view.findViewById(R.id.compose_chars);
        //Set listener for updating char count
        composeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if(String.valueOf(s).length() == 280){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int tweetChars = String.valueOf(s).length();
                int remainingChars = MAX_TWEET_LENGTH-tweetChars;

                composeChars.setText(String.valueOf(remainingChars));

            }
        });
        //Set Twitter client
        client = TwitterApp.getRestClient(getActivity());
        //Set click listener for button
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get text from input
                final String tweetContent = composeInput.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(getActivity(), "Sorry, you can't publish an empty tweet", Toast.LENGTH_LONG).show();
                    return;
                } else if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getActivity(), "Sorry, your tweet can't exceed "+MAX_TWEET_LENGTH+" characters", Toast.LENGTH_LONG).show();
                    return;
                }
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Successfully published tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            // Return input text back to activity through the implemented listener
                            TweetPublishListener listener = (TweetPublishListener) getActivity();
                            listener.onPublishTweet(tweet);
                            // Close the dialog and return back to the parent activity
                            dismiss();
                            Log.i(TAG, "Publisehd tweet "+tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Failed to publish tweet", throwable);
                    }
                });

            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}