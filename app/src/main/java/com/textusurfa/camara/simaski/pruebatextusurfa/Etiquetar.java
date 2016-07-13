package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
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

    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etiquetar);

        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();

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


        lista= (ListView) findViewById(R.id.list);

        btCancelar = (Button) findViewById(R.id.btCancelar);
        btAceptar = (Button) findViewById(R.id.btAceptar);

        /* Check if required twitter keys are set */
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key and secret not configured",Toast.LENGTH_SHORT).show();
            return;
        }

        /* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);


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
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        new SearchOnTwitter().execute(edtSearch.getText().toString());
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    class SearchOnTwitter extends AsyncTask<String, Void, Integer> {

        ArrayList<Tweet> tweets;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Etiquetar.this, "", getString(R.string.hola));
        }

        protected Integer doInBackground(String... params) {

            String status = params[0];
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

                /*Query query = new Query(params[0]);
                // YOu can set the count of maximum records here
                query.setCount(50);
                QueryResult result;
                result = twitter.search(query);
                List<twitter4j.Status> tweets = result.getTweets();
                StringBuilder str = new StringBuilder();
                if (tweets != null) {
                    this.tweets = new ArrayList<Tweet>();
                    for (twitter4j.Status tweet : tweets) {
                        str.append("@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n");
                        System.out.println(str);
                        this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                    }
                    return SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return FAILURE;*/

                int page = 1;
                //ResponseList<User> users;
                List<twitter4j.User> tweets;
                StringBuilder str = new StringBuilder();
                do {
                    tweets = twitter.searchUsers(params[0], page);
                    this.tweets = new ArrayList<Tweet>();
                    for (User user : tweets) {
                        if (user.getStatus() != null) {
                            str.append("@" + user.getScreenName() + " - " + user.getStatus().getText() + "\n");
                            System.out.println(str);
                            this.tweets.add(new Tweet("@" + user.getScreenName(), user.getName()));

                        } else {
                            // the user is protected
                            System.out.println("@" + user.getScreenName());
                        }
                    }
                    page++;
                } while (tweets.size() != 0 && page < 10);
                //System.out.println("done.");
                //System.exit(0);
                return SUCCESS;
            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to search users: " + te.getMessage());
                System.exit(-1);
            }
            return FAILURE;



        }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                dialog.dismiss();
                if (result == SUCCESS) {
                    lista.setAdapter(new TweetAdapter(Etiquetar.this, tweets));
                } else {
                    Toast.makeText(Etiquetar.this, getString(R.string.error_campo_requirido), Toast.LENGTH_LONG).show();
                }
            }

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
