package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by simaski on 18/05/16.
 */
public class MeEncantaOtro extends AppCompatActivity {

    public Button btEncanta;
    public Button btIntenta;
    public ImageView imv_encanta;
    public TextView tvEtiqueta;

    private String Tiempo;
    private String Status;
    private String Marca;
    public String keyToken;
    public String keySecret;
    public String photoFile;

    private Intent i;
    private Bundle b;
    private Bundle bb;

    //custom drawing view
    private DrawingView drawView;
    private RelativeLayout rl1;
    private RelativeLayout rl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_encanta_otro);



        i = getIntent();
        b = i.getExtras();

        Tiempo = b.getString("tiempo");
        Status = b.getString("datos");
        Marca = b.getString("marca");
        keyToken = b.getString("keyToken");
        keySecret = b.getString("keySecret");
        photoFile = b.getString("photofile");

        /*bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Status);
        bb.putString("keytoken", keyToken);
        bb.putString("keysecret", keySecret);
        bb.putString("photofile", photoFile);*/
        bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Status);
        bb.putString("marca", Marca);
        bb.putString("keytoken", keyToken);
        bb.putString("keysecret", keySecret);
        bb.putString("photofile", photoFile);

        //Toast.makeText(getApplicationContext(), "Proximamentettttt"+Tiempo, Toast.LENGTH_SHORT).show();

        btEncanta = (Button) findViewById(R.id.btEncanta);
        btIntenta = (Button) findViewById(R.id.btIntenta);
        tvEtiqueta = (TextView) findViewById(R.id.tvEtiqueta);
        tvEtiqueta.setText(Marca);


        //get drawing view
        drawView = (DrawingView) findViewById(R.id.drawing);
        //bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile);
        //Añadimos el bitmap al imageView para
        //mostrarlo por pantalla
        //imv_twittealo.setImageBitmap(bMap);
        //drawView.setBackground();
        //pathName = "/drawable/img1"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawView.setBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile));
        }

        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);



        btIntenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent miIntent = new Intent(MeEncantaOtro.this,MainActivity.class);
                miIntent.putExtras(bb);
                MeEncantaOtro.this.startActivity(miIntent);
                MeEncantaOtro.this.finish();

            }
        });


        btEncanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rl2.setVisibility(View.GONE);

                View content = rl1;
                content.setDrawingCacheEnabled(true);
                content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = content.getDrawingCache();
                //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                String path = String.format(Environment.getExternalStorageDirectory() + "/myphotos12/"+ photoFile);
                File file = new File(path);
                FileOutputStream ostream;

                //Toast.makeText(getApplicationContext(), "ied"+path, Toast.LENGTH_SHORT).show();

                try {
                    file.createNewFile();
                    ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.flush();
                    ostream.close();
                    //Toast.makeText(getApplicationContext(), "image saved", Toast.LENGTH_SHORT).show();
                    Intent miIntent = new Intent(MeEncantaOtro.this,Siguiente.class);
                    miIntent.putExtras(bb);
                    MeEncantaOtro.this.startActivity(miIntent);
                    MeEncantaOtro.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error! Salvando Imagen.", Toast.LENGTH_SHORT).show();
                }





            }
        });

    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(MeEncantaOtro.this,MainActivity.class);
            i.putExtras(bb);
            startActivity(i);
            MeEncantaOtro.this.finish();

        }
        return true;
    }
}