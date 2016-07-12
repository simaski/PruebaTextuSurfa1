package com.textusurfa.camara.simaski.pruebatextusurfa;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by simaski on 18/05/16.
 */
public class MeEncanta extends AppCompatActivity {

    public Button btEncanta;
    public Button btIntenta;
    public ImageView imv_encanta;

    private String Tiempo;
    private String Status;
    public String keyToken;
    public String keySecret;
    public String photoFile;

    private Intent i;
    private Bundle b;
    private Bundle bb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_encanta);



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
        bb.putString("keytoken", keyToken);
        bb.putString("keysecret", keySecret);
        bb.putString("photofile", photoFile);

        //Toast.makeText(getApplicationContext(), "Proximamentettttt"+Tiempo, Toast.LENGTH_SHORT).show();

        btEncanta = (Button) findViewById(R.id.btEncanta);
        btIntenta = (Button) findViewById(R.id.btIntenta);
        imv_encanta = (ImageView) findViewById(R.id.imv_encanta);

        Bitmap bMap = BitmapFactory.decodeFile(
                Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile);
        //Añadimos el bitmap al imageView para
        //mostrarlo por pantalla
        imv_encanta.setImageBitmap(bMap);


        btIntenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent miIntent = new Intent(MeEncanta.this,MainActivity.class);
                miIntent.putExtras(bb);
                MeEncanta.this.startActivity(miIntent);
                MeEncanta.this.finish();

            }
        });


        btEncanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MeEncanta.this,Siguiente.class);
                miIntent.putExtras(bb);
                MeEncanta.this.startActivity(miIntent);
                MeEncanta.this.finish();

            }
        });




    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(MeEncanta.this,MainActivity.class);
            startActivity(i);
            MeEncanta.this.finish();

        }
        return true;
    }
}