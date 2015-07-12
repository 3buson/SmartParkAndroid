package com.example.ninahp.smartpark;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;



public class SearchActivity extends ActionBarActivity implements AsyncResponse {



    protected String latitude;
    protected String longitude;
    protected ListView listView;
    protected String time;
    protected String myId;
    protected int credits;

    private static final String SERVER_BASE = "http://a5a38f0.ngrok.com";
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getToken();
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        time = intent.getStringExtra("time");
        myId = intent.getStringExtra("myId");
        credits = intent.getIntExtra("credits",0);

        listView = (ListView) findViewById(R.id.resultsList);

        findOffers(latitude, longitude, time);
    }

    private void findOffers(String latitude, String longitude, String time) {
        try {
            new CheckLoginTask(this).execute(new URL("https://pacific-tor-4300.herokuapp.com/find.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output){
        User[] users = parseJson(output);

        final ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,
                android.R.layout.simple_list_item_1, users);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (credits == 0) {
                    ((TextView) findViewById(R.id.noTokens)).setText("You have no tokens");
                } else {

                    User selectedUser = (User) adapter.getItem(position);

                    Intent intent = new Intent(SearchActivity.this, MatchFoundActivity.class);

                    intent.putExtra("uid", selectedUser.getUid());
                    intent.putExtra("phone", selectedUser.getPhone());
                    intent.putExtra("distance", selectedUser.getDistance());
                    intent.putExtra("reputation", selectedUser.getReputation());
                    intent.putExtra("myId", myId);

                    startActivity(intent);
                }
            }
        });
    }

    private class CheckLoginTask extends AsyncTask<URL, Integer, String> {
        private AsyncResponse listener;

        public CheckLoginTask(AsyncResponse listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(URL... params) {
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity("{ \"latitude\" : " + latitude + ", \"longtitude\" : " + longitude + ", \"time\" : \"" + time + "\" }");
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
            listener.processFinish(result);
        }
    }


    public void search(View v){
        findOffers(latitude, longitude, time);
    }

    private User[] parseJson(String json) {
        //TODO: put results into list and display it

        //System.out.print(json);

        User[] users = null;

        try {
            JSONArray jsonArr = new JSONArray(json);
            users = new User[jsonArr.length()];

            Random rand = new Random();
            for(int i = 0; i<jsonArr.length(); i++){
                JSONObject obj = jsonArr.getJSONObject(i);
                User u = new User(obj.getInt("id"), obj.getJSONObject("user").getString("username"),
                        String.valueOf(rand.nextInt((400 - 50) + 1) + 50), obj.getJSONObject("user").getString("number"),
                        Float.parseFloat(obj.getJSONObject("user").getString("reputation")));
                users[i] = u;
                //System.out.print(u.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    public void buy(View v){
        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Awesome payment")
                .secondaryDescription("Using the Client SDK")
                .amount("$1.00")
                .submitButtonText("Pay")
                .build();
        System.out.println("ooohohpj");
        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);
        System.out.println("ooohohpdsdfsdfdsj");

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(resultCode);
        System.out.println(BraintreePaymentActivity.RESULT_OK);
        if (resultCode == BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR) {
            System.out.println("test");

        } else if (resultCode == BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR) {
            System.out.println("test2");
        }

        if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            System.out.println("hello");
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

            RequestParams requestParams = new RequestParams();
            requestParams.put("payment_method_nonce", paymentMethodNonce);
            requestParams.put("amount", "10.00");

            client.post("https://pacific-tor-4300.herokuapp.com" + "/payment_methods", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    System.out.print("olala");
                    Toast.makeText(SearchActivity.this, "You've just bought a credit!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getToken() {
        client.get("https://pacific-tor-4300.herokuapp.com" + "/token", new TextHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                System.out.println(content);
                try {
                    JSONObject response = new JSONObject(content);
                    clientToken = response.getString("client_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                findViewById(R.id.buyButton).setEnabled(true);
            }
        });
    }
}
