package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import twitter4j.IDs;
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

    public int bandera = 0;
    public int bandera2 = 0;

    ListView lista;
    ArrayAdapter adaptador;
    HttpURLConnection con;
    private String encodedSearch;

    private TextView tweetDisplay;
    private TextView tvSeguidores;
    private TextView tvSiguiendo;
    private TextView tvIntroTxt;

    private String Tiempo;
    private String Status;
    private String Marca;

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
    public String elemento = "";

    private static SharedPreferences mSharedPreferences;

    private EditText edtSearch;
    private ArrayList<String> nombreArrayList;
    private Iterator<String> nombreIterator;
    private int contador = 0;
    private int hola = 0;

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
        Marca = b.getString("marca");
        keyToken = b.getString("keyToken");
        keySecret = b.getString("keySecret");
        photoFile = b.getString("photofile");

        bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Status);
        bb.putString("marca", Marca);
        bb.putString("photofile", photoFile);


        char[] arrayChar = Status.toCharArray();


        for(int i=0; i<arrayChar.length+1; i++){

            contador = i;
            //Toast.makeText(getApplicationContext(), "conteo: "+contador, Toast.LENGTH_SHORT).show();
        }



        lista= (ListView) findViewById(R.id.list);
        tvSeguidores= (TextView) findViewById(R.id.tvSeguidores);
        tvSeguidores.setPaintFlags(tvSeguidores.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSiguiendo= (TextView) findViewById(R.id.tvSiguiendo);
        tvSiguiendo.setPaintFlags(tvSeguidores.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lista= (ListView) findViewById(R.id.list);


        tvIntroTxt= (TextView) findViewById(R.id.intro_txt);

        nombreArrayList = new ArrayList<String>();
        //nombreIterator = nombreArrayList.iterator();

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
                miIntent.putExtras(bb);
                Etiquetar.this.startActivity(miIntent);
                Etiquetar.this.finish();

            }
        });


        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Object[] nombres = nombreArrayList.toArray();
                //String text1 = "";
                for(int i = 0; i<nombres.length; i++) {
                    elemento = elemento + nombres[i];
                }
                System.out.println(elemento);

                tvIntroTxt.setText(elemento);

                Status = Status+elemento;
                bb.putString("datos", Status);

                Dialogo2();

            }
        });

    }

    public void searchTwitter(View view){
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        attemptLogin();
        if(edtSearch.getText().length() != 0) {
            new SearchOnTwitter().execute(edtSearch.getText().toString());
        }

    }

    public void searchSeguidores(View view){

        new SearchFollowers().execute("hola");

    }

    public void searchSiguiendo(View view){

        new SearchFriends().execute("hola");

    }

    private void attemptLogin() {

        // Reset errors.
        edtSearch.setError(null);

        // Store values at the time of the login attempt.
        String usuario = edtSearch.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(usuario)) {
            edtSearch.setError(getString(R.string.error_campo_requirido));
            focusView = edtSearch;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
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
            dialog = ProgressDialog.show(Etiquetar.this, "", getString(R.string.buscando));
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
                            this.tweets.add(new Tweet("@" + user.getScreenName(), user.getDescription(), user.getName()));

                        } else {
                            // the user is protected
                            System.out.println("@" + user.getScreenName());
                        }
                    }
                    page++;
                } while (tweets.size() != 0 && page < 2);
                return SUCCESS;
                //System.out.println("done.");
                //System.exit(0);

            } catch (TwitterException te) {
                te.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al encontrar Usuario: "+ te.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("Error al encontrar Usuario: " + te.getMessage());
                //System.exit(-1);
            }
            return FAILURE;



        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == SUCCESS) {
                lista.setAdapter(new TweetAdapter(Etiquetar.this, tweets));
                lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lista.setItemChecked(2, true);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //String selectedSweet = lista.getItemAtPosition(position).toString();


                        TextView textView = (TextView) view.findViewById(R.id.txtTweetBy);
                        String text = textView.getText().toString();
                        String text2 = " "+text;

                        char[] arrayChar = text2.toCharArray();


                        for(int i=0; i<arrayChar.length+1; i++){
                            hola = i;

                        }
                        //Toast.makeText(getApplicationContext(), "cont:--> "+contador, Toast.LENGTH_SHORT).show();
                        contador = contador + hola;
                        //Toast.makeText(getApplicationContext(), "conteo: "+contador, Toast.LENGTH_SHORT).show();
                        if(contador > 116){
                            Dialogo();
                        }else {

                            nombreArrayList.add(text2);

                            tvIntroTxt.setText(text);

                            Toast.makeText(getApplicationContext(), "Agregado: " + text, Toast.LENGTH_SHORT).show();
                        }


                    }
                });


            } else {
                Toast.makeText(Etiquetar.this, getString(R.string.error_campo_requirido), Toast.LENGTH_LONG).show();
            }
        }

    }


    class SearchFollowers extends AsyncTask<String, Void, Integer> {

        ArrayList<Tweet> tweets;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Etiquetar.this, "", getString(R.string.buscando));
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

                /* Getting user id form access token */
                long userID = accessToken.getUserId();
                final User user1 = twitter.showUser(userID);


                User u1 = null ;
                long cursor = -1;

                IDs ids;
                // List<twitter4j.User> tweets;
                System.out.println("Listing followers's ids.");
                do {
                    ids = twitter.getFollowersIDs(userID, cursor);
                    this.tweets = new ArrayList<Tweet>();

                    for (long id : ids.getIDs()) {

                        User user = twitter.showUser(id);
                        System.out.println(user.getName());
                        this.tweets.add(new Tweet("@" + user.getScreenName(), user.getDescription(), user.getName()));
                        bandera++;
                        System.out.println("-->"+bandera);
                        if(bandera == 55){
                            //return SUCCESS;
                            break;
                        }

                    }


                } while ((cursor = ids.getNextCursor()) != 0);
                return SUCCESS;

            } catch (TwitterException te) {
                te.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al recuperar Seguidores: "+ te.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("Error al recuperar Seguidores: " + te.getMessage());
                //System.exit(-1);
            }
            return FAILURE;



        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == SUCCESS) {
                lista.setAdapter(new TweetAdapter(Etiquetar.this, tweets));
                lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lista.setItemChecked(2, true);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //String selectedSweet = lista.getItemAtPosition(position).toString();


                        TextView textView = (TextView) view.findViewById(R.id.txtTweetBy);
                        String text = textView.getText().toString();
                        String text2 = " "+text;

                        char[] arrayChar = text2.toCharArray();


                        for(int i=0; i<arrayChar.length+1; i++){
                            hola = i;

                        }
                        //Toast.makeText(getApplicationContext(), "cont:--> "+contador, Toast.LENGTH_SHORT).show();
                        contador = contador + hola;
                        //Toast.makeText(getApplicationContext(), "conteo: "+contador, Toast.LENGTH_SHORT).show();
                        if(contador > 116){
                            Dialogo();
                        }else {

                            nombreArrayList.add(text2);

                            tvIntroTxt.setText(text);

                            Toast.makeText(getApplicationContext(), "Agregado: " + text, Toast.LENGTH_SHORT).show();
                        }


                    }
                });


            } else {
                Toast.makeText(Etiquetar.this, getString(R.string.error_campo_requirido), Toast.LENGTH_LONG).show();
            }
        }

    }

    class SearchFriends extends AsyncTask<String, Void, Integer> {

        ArrayList<Tweet> tweets;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Etiquetar.this, "", getString(R.string.buscando));
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

                /* Getting user id form access token */
                long userID = accessToken.getUserId();
                final User user1 = twitter.showUser(userID);


                User u1 = null ;
                long cursor = -1;

                IDs ids;
                // List<twitter4j.User> tweets;
                System.out.println("Listing followers's ids.");
                do {
                    ids = twitter.getFriendsIDs(userID, cursor);
                    this.tweets = new ArrayList<Tweet>();

                    for (long id : ids.getIDs()) {

                        User user = twitter.showUser(id);
                        System.out.println(user.getName());
                        this.tweets.add(new Tweet("@" + user.getScreenName(), user.getDescription(), user.getName()));
                        bandera2++;
                        System.out.println("-->"+bandera2);
                        if(bandera2 == 55){
                            //return SUCCESS;
                            break;
                        }

                    }


                } while ((cursor = ids.getNextCursor()) != 0);
                return SUCCESS;

            } catch (TwitterException te) {
                te.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al recuperar Siguiendo: "+ te.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("Error al recuperar Seguidores: " + te.getMessage());
                //System.exit(-1);
            }
            return FAILURE;



        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == SUCCESS) {
                lista.setAdapter(new TweetAdapter(Etiquetar.this, tweets));
                lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lista.setItemChecked(2, true);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //String selectedSweet = lista.getItemAtPosition(position).toString();


                        TextView textView = (TextView) view.findViewById(R.id.txtTweetBy);
                        String text = textView.getText().toString();
                        String text2 = " "+text;

                        char[] arrayChar = text2.toCharArray();


                        for(int i=0; i<arrayChar.length+1; i++){
                            hola = i;

                        }
                        //Toast.makeText(getApplicationContext(), "cont:--> "+contador, Toast.LENGTH_SHORT).show();
                        contador = contador + hola;
                        //Toast.makeText(getApplicationContext(), "conteo: "+contador, Toast.LENGTH_SHORT).show();
                        if(contador > 116){
                            Dialogo();
                        }else {

                            nombreArrayList.add(text2);

                            tvIntroTxt.setText(text);

                            Toast.makeText(getApplicationContext(), "Agregado: " + text, Toast.LENGTH_SHORT).show();
                        }


                    }
                });



            } else {
                Toast.makeText(Etiquetar.this, getString(R.string.error_campo_requirido), Toast.LENGTH_LONG).show();
            }
        }

    }

    /////////////////////////////////DIALOG ///////////////////////////////////////////
    public void Dialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Informaci\u00f3n");
        builder.setMessage("Hola no se puede agregar usuario limite de caracteres (140) del tweet alcanzado!");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    ////////////////////////////////FIN DIALOG /////////////////////////////////////////

    /////////////////////////////////DIALOG ///////////////////////////////////////////
    public void Dialogo2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Vista Previa Tweet");
        builder.setMessage(Status);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent miIntent = new Intent(Etiquetar.this,Twittealo.class);
                        miIntent.putExtras(bb);
                        Etiquetar.this.startActivity(miIntent);
                        Etiquetar.this.finish();
                    }
        });
        builder.show();
    }
    ////////////////////////////////FIN DIALOG /////////////////////////////////////////



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(Etiquetar.this,Twittealo.class);
            startActivity(i);
            Etiquetar.this.finish();

        }
        return true;
    }
}
