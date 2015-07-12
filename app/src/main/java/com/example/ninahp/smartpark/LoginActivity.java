package com.example.ninahp.smartpark;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    protected String uid;
    protected String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View v){
        EditText uidEditText = (EditText) findViewById(R.id.uidEditText);
        EditText pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        uid = uidEditText.getText().toString();
        pwd = pwdEditText.getText().toString();

        try {
            new CheckLoginTask().execute(new URL("https://pacific-tor-4300.herokuapp.com/login.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class CheckLoginTask extends AsyncTask <URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity("{ \"username\" : \"" + uid + "\", \"password\" : \"" + pwd + "\" }");
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
                JSONObject jObject = null;
                int credits = 0;
                try {
                    jObject = new JSONObject(result);
                    System.out.print(jObject.toString());
                    credits = jObject.getInt("credits");
                    System.out.print(credits);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LoginActivity.this, HelloActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("credits", credits);
                startActivity(intent);
            }else{
                //TODO: add some error test, ask a user to repeat loging in, clear edittexts
            }
        }
    }

}
