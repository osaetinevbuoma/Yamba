package com.yamba;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

import winterwell.jtwitter.Twitter;


public class StatusActivity extends ActionBarActivity implements OnClickListener {
    private static final String TAG = "com.yamba.StatusActivity";
    EditText editText;
    Button updateButton;
    Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        // Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);

        updateButton.setOnClickListener(this);

        twitter = new Twitter("student", "password");
        twitter.setAPIRootUrl("http://yamba.com/api");
    }

    // Called when button is clicked
    public void onClick(View view) {
        twitter.setStatus(editText.getText().toString());
        Log.d(TAG, "onClicked");
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
