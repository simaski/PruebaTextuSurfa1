package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by simaski on 19/05/16.
 */
public class Twittealo extends AppCompatActivity {

    public Button btEtiquetar;
    public Button btTwittealo;
    public Button btTweet;

    private String Tiempo;
    private String Status;

    private Intent i;
    private Bundle b;
    private Bundle bb;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    public String keyToken;
    public String keySecret;
    public String photoFile;

    private static SharedPreferences mSharedPreferences;

    private ProgressDialog pDialog;

    public ImageView imv_twittealo;
    public Bitmap bMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twittealo);

        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();

        i = getIntent();
        b = i.getExtras();

        Tiempo = b.getString("tiempo");
        Status = b.getString("datos");
        keyToken = b.getString("keyToken");
        keySecret = b.getString("keySecret");
        photoFile = b.getString("photofile");

        Toast.makeText(getApplication(), "--> "+Status, Toast.LENGTH_SHORT).show();

        bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Status);
        bb.putString("photofile", photoFile);


        btEtiquetar = (Button) findViewById(R.id.btEtiquetar);
        btTwittealo = (Button) findViewById(R.id.btTwittealo);
        btTweet = (Button) findViewById(R.id.btTweet);

        imv_twittealo = (ImageView) findViewById(R.id.imv_twittealo);

        bMap = BitmapFactory.decodeFile(
                Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile);
        //Añadimos el bitmap al imageView para
        //mostrarlo por pantalla
        imv_twittealo.setImageBitmap(bMap);

        /* Check if required twitter keys are set */
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key and secret not configured",Toast.LENGTH_SHORT).show();
            return;
        }

        /* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);





        btEtiquetar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent miIntent = new Intent(Twittealo.this,Etiquetar.class);
                miIntent.putExtras(bb);
                Twittealo.this.startActivity(miIntent);
                Twittealo.this.finish();

            }
        });


        btTwittealo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(getApplicationContext(), "Proximamente", Toast.LENGTH_SHORT).show();
                if (Status.trim().length() > 0) {
                    new updateTwitterStatus().execute(Status);
                } else {
                    Toast.makeText(getApplication(), "Message is empty!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }


    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*pDialog = new ProgressDialog(Twittealo.this, R.style.MyDialog);
            pDialog.setMessage("Publicando en twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
            esperar();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bMap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                InputStream is = new ByteArrayInputStream(bitmapdata);
                //InputStream is = getResources().openRawResource(R.drawable.splash);
                statusUpdate.setMedia("test.png", is);

                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
            //supDialog.dismiss();
            //pDialog.dismiss();
            Toast.makeText(Twittealo.this, "Publicado en Twitter!", Toast.LENGTH_SHORT).show();

            // Clearing EditText field
            //mShareEditText.setText("");
        }

    }

    //ESPERAR//////////////////////////////////////////////////////////////////////////////////
    public void esperar(){
        new CountDownTimer(4000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btTwittealo.setVisibility(View.GONE);
                btEtiquetar.setVisibility(View.GONE);
                btTweet.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                Intent miIntent = new Intent(Twittealo.this,MainActivity.class);
                miIntent.putExtras(bb);
                Twittealo.this.startActivity(miIntent);
                Twittealo.this.finish();

            }
        }.start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(Twittealo.this,MainActivity.class);
            startActivity(i);
            Twittealo.this.finish();

        }
        return true;
    }


}