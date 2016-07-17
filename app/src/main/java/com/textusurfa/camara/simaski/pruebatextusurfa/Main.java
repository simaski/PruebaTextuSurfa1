package com.textusurfa.camara.simaski.pruebatextusurfa;

/**
 * Created by simaski on 25/06/16.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import twitter.WebViewActivity;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class Main extends Activity {

    //****************Button**************************//
    public FloatingActionButton ftbTwitter;
    public FloatingActionButton ftbConfigura;
    public FloatingActionButton ftbIngresa;



    public FloatingActionsMenu ftbMenu;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private String Tiempo;
    private String Datos;
    private String Marca;
    private int bandera = 0;


    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 100;

    private ProgressDialog pDialog;

    private static Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences mSharedPreferences;

    private EditText mShareEditText;
    private TextView userName;
    private View loginLayout;
    private View shareLayout;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    public String keyToken;
    public String keySecret;

    public String date;
    public String fecha = "20160701";
    public String fecha1 = "20160702";
    public String fecha2 = "20160703";


    private Intent i;
    private Bundle b;
    private Bundle bb;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();

		/* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.main);


        //Date today = Calendar.getInstance().getTime();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
        //date = formatter.format(today);
        ftbMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);

        date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //Toast.makeText(getApplicationContext(), "hh "+date, Toast.LENGTH_SHORT).show();


        //if(date.compareTo(fecha) == 0 || date.compareTo(fecha1) == 0 || date.compareTo(fecha2) == 0) {


        userName = (TextView) findViewById(R.id.user_name);

        /* Check if required twitter keys are set */
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key and secret not configured", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        /*  if already logged in, then hide login layout and show share layout */
        if (isLoggedIn) {
            //loginLayout.setVisibility(View.VISIBLE);
            bandera = 1;
            userName.setVisibility(View.VISIBLE);

            String username = mSharedPreferences.getString(PREF_USER_NAME, "");
            userName.setText(getResources().getString(R.string.hola) + username);

        } else {
            //loginLayout.setVisibility(View.GONE);
            //shareLayout.setVisibility(View.GONE);

            Uri uri = getIntent().getData();

            if (uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {

					/* Getting oAuth authentication token */
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                    final String username = user.getName();


					/* save updated token */
                    saveTwitterInfo(accessToken);

                    //loginLayout.setVisibility(View.GONE);
                    //shareLayout.setVisibility(View.VISIBLE);
                    userName.setText(getString(R.string.hola) + username);
                    Toast.makeText(this, "Aqui Estoy 22222", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("Failed to login Twitter!!", e.getMessage());
                }
            }
        }

        ftbTwitter = (FloatingActionButton) findViewById(R.id.fbtTwitter);
        ftbTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_SHORT).show();
                loginToTwitter();
            }
        });

        ftbConfigura = (FloatingActionButton) findViewById(R.id.fbtConfigura);
        ftbConfigura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Hola "+prueba,Toast.LENGTH_SHORT).show();
                Intent miIntent = new Intent(Main.this, Configuracion.class);
                Main.this.startActivity(miIntent);
                Main.this.finish();


            }
        });

        ftbIngresa = (FloatingActionButton) findViewById(R.id.ftbIngresa);
        ftbIngresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i = getIntent();
                b = i.getExtras();

                if (bandera == 0 || b == null) {
                    // Toast.makeText(getApplication(), "Algunos datos son necesarios", Toast.LENGTH_SHORT).show();
                    Dialogo();
                } else {

                    Datos = b.getString("datos");
                    Tiempo = b.getString("tiempo");
                        bb = new Bundle();
                        bb.putString("datos", Datos);
                        bb.putString("tiempo", Tiempo);
                        bb.putString("keytoken", keyToken);
                        bb.putString("keysecret", keySecret);

                        Intent miIntent = new Intent(Main.this, MainActivity.class);
                        miIntent.putExtras(bb);
                        Main.this.startActivity(miIntent);
                        Main.this.finish();

                }
            }
        });
        /*} else {
            DialogoPeriodo();
            ftbMenu.setVisibility(View.GONE);
        }*/

    }


    /////////////////////////////////DIALOG ///////////////////////////////////////////
    public void Dialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Informaci\u00f3n");
        builder.setMessage("Hola error al conectar con Twitter o no haz configurado correctamente " +
                "el tiempo y comentario de la aplicaci\u00f3n");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    ////////////////////////////////FIN DIALOG /////////////////////////////////////////


    /////////////////////////////////DIALOG ///////////////////////////////////////////
    public void DialogoPeriodo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Informaci\u00f3n");
        builder.setMessage("Periodo de Prueba terminado. Por favor contacte con el desarrollador si desea seguir usando la APP");
        builder.show();
    }
    ////////////////////////////////FIN DIALOG /////////////////////////////////////////

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.commit();
            keyToken = accessToken.getToken();
            keySecret = accessToken.getTokenSecret();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    /* Reading twitter essential configuration parameters from strings.xml */
    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void loginToTwitter() {
        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
                Toast.makeText(this, "Aqui Estoy 4444",Toast.LENGTH_SHORT).show();

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {

            //loginLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Disculpe! Ya esta conectado a Twitter!",Toast.LENGTH_SHORT).show();
            //shareLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String verifier = data.getExtras().getString(oAuthVerifier);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();

                saveTwitterInfo(accessToken);

                userName.setVisibility(View.VISIBLE);
                //shareLayout.setVisibility(View.VISIBLE);
                userName.setText(Main.this.getResources().getString(R.string.hola) + username);
                Toast.makeText(this, "Aqui Estoy 5555",Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Publicando en twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
                //InputStream is = getResources().openRawResource(R.drawable.splash);
                //statusUpdate.setMedia("test.jpg", is);

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

            Toast.makeText(Main.this, "Publicado en Twitter!", Toast.LENGTH_SHORT).show();

            // Clearing EditText field
            //mShareEditText.setText("");
        }

    }


}
