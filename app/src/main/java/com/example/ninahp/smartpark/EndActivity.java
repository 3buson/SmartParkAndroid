package com.example.ninahp.smartpark;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class EndActivity extends ActionBarActivity {

    public String user;
    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        user = getIntent().getStringExtra("user");
        uid = getIntent().getStringExtra("uid");
        TextView tv = (TextView) findViewById(R.id.partnerTextView1);
        tv.setText(user);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end, menu);
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

    public void upvote(View v){
        Button uv = (Button) findViewById(R.id.upvoteButton);
        uv.setEnabled(false);

        Button dv = (Button) findViewById(R.id.downvoteButton);
        dv.setEnabled(false);
    }

    public void downvote(View v){
        Button uv = (Button) findViewById(R.id.upvoteButton);
        uv.setEnabled(false);

        Button dv = (Button) findViewById(R.id.downvoteButton);
        dv.setEnabled(false);
    }

    public void swap(View v){

        Intent intent = new Intent(EndActivity.this, HelloActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }
}
