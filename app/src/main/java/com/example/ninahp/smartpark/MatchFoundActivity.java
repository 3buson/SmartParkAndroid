package com.example.ninahp.smartpark;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class MatchFoundActivity extends ActionBarActivity {
    protected String myId;
    protected String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_found);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        String phone = intent.getStringExtra("phone");
        String distance = intent.getStringExtra("distance");
        String reputation = Integer.toString(intent.getIntExtra("reputation", 0));
        myId = intent.getStringExtra("myId");

        TextView partner = (TextView) findViewById(R.id.partnerTextView1);
        partner.setText(uid);

        TextView distanceTextView = (TextView) findViewById(R.id.destinationTextView);
        distanceTextView.setText("who is " + distance + " metres away! ");

        TextView phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        phoneTextView.setText(phone);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_found, menu);
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

    public void confirm(View v){
        try {
            new CheckLoginTask().execute(new URL("https://pacific-tor-4300.herokuapp.com/requests.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class CheckLoginTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity("{ \"request_id\" : \"" + uid + "\", " +
                        "\"user_id\" : \"" + myId + "\"}");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = null;
            try {
                httppost = new HttpPost(new java.net.URI(params[0].toString()));
                httppost.setHeader("Content-type", "application/json");
                httppost.setEntity(stringEntity);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            InputStream inputStream = null;
            String result = "";
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try{if(inputStream != null)inputStream.close();}catch(Exception e){
                    e.printStackTrace();
                }
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result){
            boolean success = true;
            if(result.length()==0){
                success = false;
            }
            if(success){
                Intent intent = new Intent(MatchFoundActivity.this, EndActivity.class);
                intent.putExtra("user", uid);
                intent.putExtra("uid", myId);
                startActivity(intent);
            }else{
                //TODO: add some error test, ask a user to repeat loging in, clear edittexts
            }
        }
    }
}
