package com.yamba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;


public class StatusActivity extends ActionBarActivity implements OnClickListener {
    private static final String TAG = "com.yamba.StatusActivity";
    private EditText editStatus;
    private Button buttonTweet;
    private TextView textCount;
    private int defaultTextColor;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Find views
        editStatus = (EditText) findViewById(R.id.editStatus);
        buttonTweet = (Button) findViewById(R.id.buttonTweet);
        textCount = (TextView) findViewById(R.id.textCount);

        //buttonTweet.setOnClickListener(this);

        defaultTextColor = textCount.getTextColors().getDefaultColor();
        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 140 - editStatus.length();
                textCount.setText(Integer.toString(count));
                textCount.setTextColor(Color.GREEN);

                if (count < 10) textCount.setTextColor(Color.RED);
                else textCount.setTextColor(defaultTextColor);
            }
        });
    }

    // Called when button is clicked
    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked with status: " + status);

        new PostTask().execute(status);
    }

    /**
     * This method is called from the activity button element and does the same things as the onClick method
     * based on the OnClickListener. It is just a test to see the different ways on using the onClick
     * functionalities of an activity button, either from the xml file or in the java file
     * @param view - A View object
     */
    public void sendTweet(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked with status: " + status);

        new PostTask().execute(status);
    }

    /**
     * Asynchronously post to yamba service
     */
    private final class PostTask extends AsyncTask<String, Void, String> {
        // Call to initiate the background activity
        @Override
        protected String doInBackground(String... params) {
            try {
                prefs = PreferenceManager.getDefaultSharedPreferences(StatusActivity.this);
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                /**
                 * Check that username and password are not empty.
                 * If empty, Toast a message to set login info and bounce to SettingsActivity
                 * Hint: TextUtils
                 */
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    StatusActivity.this.startActivity(new Intent(StatusActivity.this, SettingsActivity.class));

                    return "Please set your username and password";
                }

                if (TextUtils.isEmpty(params[0])) return "You cannot post an empty tweet"; // Ensure that user does not try posting an empty tweet
                else {
                    YambaClient yambaCloud = new YambaClient(username, password);
                    yambaCloud.postStatus(params[0]);

                    return "Successfully posted";
                }
            } catch (YambaClientException e) {
                Log.e(TAG, "When posting to Yamba Service: " +  e.toString());
                e.printStackTrace();

                return "Failed to post to Yamba Service";
            }
        }

        // Call once the background activity has completed
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_tweet:
                //startActivity(new Intent("com.marakana.android.yamba.action.tweet"));
                return true;

            case R.id.action_refresh:
                startService(new Intent(this, RefreshService.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
