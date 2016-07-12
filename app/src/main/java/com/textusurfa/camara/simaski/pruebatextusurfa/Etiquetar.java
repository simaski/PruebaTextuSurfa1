package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by simaski on 19/05/16.
 */
public class Etiquetar extends AppCompatActivity {

    public Button btCancelar;
    public Button btAceptar;

    ListView lista;
    ArrayAdapter adaptador;
    HttpURLConnection con;
    private String encodedSearch;

    private TextView tweetDisplay;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etiquetar);

        i = getIntent();
        b = i.getExtras();

        Tiempo = b.getString("tiempo");
        Status = b.getString("datos");
        keyToken = b.getString("keyToken");
        keySecret = b.getString("keySecret");
        photoFile = b.getString("photofile");

        bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Status);
        bb.putString("photofile", photoFile);


        lista= (ListView) findViewById(R.id.listaUsuarios);

        //tweetDisplay = (TextView)findViewById(R.id.tweet_txt);

        /* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

        EditText searchTxt = (EditText)findViewById(R.id.search_edit);
        String searchTerm = searchTxt.getText().toString();

        try {
            encodedSearch = URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        btCancelar = (Button) findViewById(R.id.btCancelar);
        btAceptar = (Button) findViewById(R.id.btAceptar);


        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent miIntent = new Intent(Etiquetar.this,Twittealo.class);
                Etiquetar.this.startActivity(miIntent);
                Etiquetar.this.finish();

            }
        });


        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Proximamente", Toast.LENGTH_SHORT).show();

            }
        });






    }

    public void searchTwitter(View view){
        
    }





    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(Etiquetar.this,Twittealo.class);
            startActivity(i);
            Etiquetar.this.finish();

        }
        return true;
    }
}
