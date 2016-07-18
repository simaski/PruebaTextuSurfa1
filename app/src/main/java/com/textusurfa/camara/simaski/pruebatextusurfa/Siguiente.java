package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Created by simaski on 25/06/16.
 */
public class Siguiente extends Activity implements View.OnClickListener {

    //custom drawing view
    private DrawingView drawView;
    //buttons
    private ImageButton currPaint, bt1, eraseBtn, newBtn, saveBtn;

    private ImageButton currPaint1;
    private ImageButton currPaint2;
    private ImageButton currPaint3;
    private ImageButton currPaint4;
    private ImageButton currPaint5;
    private ImageButton currPaint6;
    private ImageButton currPaint7;
    private ImageButton currPaint8;
    //sizes
    private float smallBrush, mediumBrush, largeBrush;

    private Button btVolver;
    private Button btSiguiente;

    private String Tiempo;
    private String Status;
    private String Marca;
    public String keyToken;
    public String keySecret;
    public String photoFile;

    private Intent i;
    private Bundle b;
    private Bundle bb;

    public Bitmap bMap;

    public LinearLayout paintLayout;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.siguiente);

        paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

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


        //get drawing view
        drawView = (DrawingView) findViewById(R.id.drawing);
        //bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile);
        //Añadimos el bitmap al imageView para
        //mostrarlo por pantalla
        //imv_twittealo.setImageBitmap(bMap);
        //drawView.setBackground();
        //pathName = "/drawable/img1"
        //drawView.setBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawView.setBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/myphotos12/"+photoFile));
        }

        //get the palette and first color button

        //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //sizes from dimensions
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //draw button
        /*drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);*/

        //set initial size
        drawView.setBrushSize(smallBrush);

        //erase button
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        btVolver = (Button) findViewById(R.id.btVolver);
        btVolver.setOnClickListener(this);


        btSiguiente = (Button) findViewById(R.id.btSiguiente);
        btSiguiente.setOnClickListener(this);

        //new button

        //save button
       /* saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);*/
    }


    //user clicked paint
    public void paintClicked(View view) {
        //use chosen color

        //set erase false
        drawView.setErase(false);
        drawView.setBrushSize(smallBrush);
        drawView.setLastBrushSize(smallBrush);
        //drawView.setBrushSize(drawView.getLastBrushSize());

        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            String arreglo[] = {"#14BBFF","#E30713","#FDFE00","#90F30F","#B51DC7","#6B6B6B","#FFFFFF"};
            drawView.setColor(color);

            //for (int i = 0; i <= arreglo.length; i++){
            if(color.compareTo(arreglo[0]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();

                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[1]) == 0) {
                //Toast.makeText(getApplicationContext(), "color0 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[2]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[3]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[4]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[5]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint6 = (ImageButton) paintLayout.getChildAt(6);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint6.setImageDrawable(getResources().getDrawable(R.drawable.paint6));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            if(color.compareTo(arreglo[6]) == 0) {
                //Toast.makeText(getApplicationContext(), "color1 "+color, Toast.LENGTH_SHORT).show();
                currPaint = (ImageButton) paintLayout.getChildAt(0);
                currPaint1 = (ImageButton) paintLayout.getChildAt(1);
                currPaint2 = (ImageButton) paintLayout.getChildAt(2);
                currPaint3 = (ImageButton) paintLayout.getChildAt(3);
                currPaint4 = (ImageButton) paintLayout.getChildAt(4);
                currPaint5 = (ImageButton) paintLayout.getChildAt(5);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint1.setImageDrawable(getResources().getDrawable(R.drawable.paint1));
                currPaint2.setImageDrawable(getResources().getDrawable(R.drawable.paint2));
                currPaint3.setImageDrawable(getResources().getDrawable(R.drawable.paint3));
                currPaint4.setImageDrawable(getResources().getDrawable(R.drawable.paint4));
                currPaint5.setImageDrawable(getResources().getDrawable(R.drawable.paint5));
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            }
            // }
            //update ui
            //Toast.makeText(getApplicationContext(), "color "+color, Toast.LENGTH_SHORT).show();

            //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.erase_btn) {
            //switch to erase - choose size

            drawView.setErase(true);
            drawView.setBrushSize(largeBrush);
            drawView.setLastBrushSize(largeBrush);
        }

        if (view.getId() == R.id.btSiguiente) {

            View content = drawView;
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
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error! Salvando Imagen.", Toast.LENGTH_SHORT).show();
            }
            //save drawing
           /* drawView.setDrawingCacheEnabled(true);
            //attempt to save
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString() + ".png", "drawing");
            //feedback
            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }*/
            drawView.destroyDrawingCache();

            Intent miIntent = new Intent(Siguiente.this, Twittealo.class);
            miIntent.putExtras(bb);
            Siguiente.this.startActivity(miIntent);
            Siguiente.this.finish();
        }

        if (view.getId() == R.id.btVolver) {
            Intent miIntent = new Intent(Siguiente.this, MainActivity.class);
            miIntent.putExtras(bb);
            Siguiente.this.startActivity(miIntent);
            Siguiente.this.finish();
        }
    }
}
