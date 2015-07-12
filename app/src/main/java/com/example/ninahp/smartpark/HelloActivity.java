package com.example.ninahp.smartpark;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class HelloActivity extends ActionBarActivity {

    protected String longitude;
    protected String latitude;
    protected String time;
    protected String uid;
    protected String location;
    protected int credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        TextView uidTextView = (TextView) findViewById(R.id.uidTextView);
        uidTextView.setText(uid);
        credits = intent.getIntExtra("credits", 8);
        TextView creditsTextView = (TextView) findViewById(R.id.creditsTextView);
        creditsTextView.setText(credits + " credits!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello, menu);
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

    public void offer(View v){
        EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        location = locationEditText.getText().toString();
        //TODO: calculate latitude and logitude
        latitude = location.split(" ")[0];
        longitude = location.split(" ")[1];
        //send lon and lat and time to ws



        try {
            new CheckLoginTask().execute(new URL("https://pacific-tor-4300.herokuapp.com/requests.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public void search(View v){
        EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        location = locationEditText.getText().toString().replaceAll("\\s+","");

        //TODO: calculate latitude and logitude

        try {
            new getLatLongFromAddress().execute(new URL("https://pacific-tor-4300.herokuapp.com/requests.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //getLatLongFromAddress("Kolodvorska10Ljubljana");


    }

    private class getLatLongFromAddress extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {

            HttpClient httpclient = new DefaultHttpClient();
            String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                    location.replaceAll("\\s+","") + "&sensor=false";
            HttpGet httpGet = new HttpGet(uri);


            InputStream inputStream = null;
            String result = "";
            try {
                HttpResponse response = httpclient.execute(httpGet);
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
            JSONObject jsonObject = new JSONObject();
            double lng = 0.0;
            double lat = 0.0;

            try {
                jsonObject = new JSONObject(result);

                lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                System.out.println("latitude" + " " + lat);
                System.out.println("longitude" +  " " + lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("\n" + longitude + "   " + latitude);
            TimePicker tp  =(TimePicker) findViewById(R.id.timePicker);
            time =  tp.getCurrentHour() + ":" + tp.getCurrentMinute();
            System.out.print(time);
            Intent intent = new Intent(HelloActivity.this, SearchActivity.class);
            intent.putExtra("longitude", Double.toString(lat));
            intent.putExtra("latitude", Double.toString(lng));
            intent.putExtra("time", time);
            intent.putExtra("myId", uid);
            intent.putExtra("credits", credits);

            startActivity(intent);
        }
    }

    public static void getLatLongFromAddress(String youraddress) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress + "&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            System.out.println("latitude" + " " + lat);
            System.out.println("longitude" +  " " + lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class CheckLoginTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            StringEntity stringEntity = null;
            try {
                TimePicker tp  =(TimePicker) findViewById(R.id.timePicker);
                String time1 =  tp.getCurrentHour() + ":" + tp.getCurrentMinute();

                stringEntity = new StringEntity("{ \"requestType\" : \"give\"," +
                        "\"longtitude\" : \"" + longitude + "\"," +
                        "\"latitude\" : \"" + latitude + "\"," +
                        "\"expires\" : \"" + time1 + "\"," +
                        "\"uid\" : \"" + uid + "\"" +
                        "}");
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

                Intent intent = new Intent(HelloActivity.this, OfferActivity.class);
                startActivity(intent);
            }else{
                //TODO: add some error test, ask a user to repeat loging in, clear edittexts
            }
        }
    }


}
