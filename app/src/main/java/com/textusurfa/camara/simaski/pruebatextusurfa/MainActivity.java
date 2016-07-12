package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {

    Camera mCamera;
    SurfaceView mPreview;
    String filePath;
    int currentCameraId = 1;
    protected String imageFilePath;

    private TextView mEsperar;
    public Camera.ShutterCallback sc = this;
    public Camera.PictureCallback pc = this;

    public int TiempoFinal = 0;

    private String Tiempo;
    private String Datos;
    private String keyToken;
    private String keySecret;
    private String photofile;

    private Intent i;
    private Bundle b;
    private Bundle bb;

    private ImageButton btCapturar;


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        i = getIntent();
        b = i.getExtras();

        Tiempo = b.getString("tiempo");
        Datos = b.getString("datos");
        keyToken = b.getString("keyToken");
        keySecret = b.getString("keySecret");


        btCapturar = (ImageButton) findViewById(R.id.btCapturar);


        TiempoFinal = (Integer.parseInt(Tiempo) * 1000) + 1000;
        Toast.makeText(getApplicationContext(), "AQUI"+TiempoFinal, Toast.LENGTH_SHORT).show();

        bb = new Bundle();
        bb.putString("tiempo", Tiempo);
        bb.putString("datos", Datos);
        bb.putString("keytoken", keyToken);
        bb.putString("keysecret", keySecret);


        mPreview = (SurfaceView) findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open(currentCameraId);

        mEsperar = (TextView) findViewById(R.id.tvEsperar);

    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.d("CAMERA", "Destroy");
    }

    public void onCancelClick(View v) {

        mCamera.stopPreview();
        mCamera.release();
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(currentCameraId);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
       /* int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }*/
        //int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        //params.setRotation(rotate);
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.setParameters(params);
       // mCamera.setDisplayOrientation(0);
        mCamera.startPreview();
    }

    public void onSnapClick(View v) {
        btCapturar.setVisibility(View.GONE);
        esperar();
    }

    @Override
    public void onShutter() {
        //Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }

    //ESPERAR//////////////////////////////////////////////////////////////////////////////////
    public void esperar(){
        new CountDownTimer(TiempoFinal,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mEsperar.setText(""+ (millisUntilFinished/1000));
                mEsperar.setTextSize(300);

            }

            @Override
            public void onFinish() {
                mEsperar.setText("");
                mCamera.takePicture(sc, null, null, pc);

            }
        }.start();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //Here, we chose internal storage
        FileOutputStream outStream = null;
        try {
            File miDirs = new File(Environment.getExternalStorageDirectory() + "/myphotos12");
            if (!miDirs.exists())
                miDirs.mkdirs();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = simpleDateFormat.format(new Date());
            photofile = "PhotoMirror"+date+".png";

            imageFilePath = String.format(
                    Environment.getExternalStorageDirectory() + "/myphotos12/"
                            + photofile);

            Uri selectedImage = Uri.parse(imageFilePath);
            File file = new File(imageFilePath);
            String path = file.getAbsolutePath();
            Toast.makeText(getApplicationContext(), "Ruta "+path,Toast.LENGTH_SHORT).show();
            Bitmap bitmap = null;

            outStream = new FileOutputStream(file);
            outStream.write(data);
            outStream.close();

            if (path != null) {
                if (path.startsWith("content")) {
                    bitmap = decodeStrem(file, selectedImage,
                            MainActivity.this);
                } else {
                    bitmap = decodeFile(file, 10);
                }
            }
            if (bitmap != null) {
                Toast.makeText(MainActivity.this,"Imagen Capturada", Toast.LENGTH_LONG)
                        .show();
                bb.putString("photofile", photofile);
                Intent miIntent = new Intent(MainActivity.this,MeEncanta.class);
                miIntent.putExtras(bb);
                MainActivity.this.startActivity(miIntent);
                MainActivity.this.finish();
                //imv_prueba.setImageBitmap(bitmap);
                //imv_prueba.setRotation(0);
                        /*Bundle b = new Bundle();
                        Intent i = new Intent(getApplicationContext(),OtraActivity.class);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,0,bs);
                        b.putByteArray("img",bs.toByteArray());
                        i.putExtras(b);
                        startActivity(i);*/
            } else {
                Toast.makeText(MainActivity.this,"Fallo al capturar la imagen", Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
       /* Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }*/
        //int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        //params.setRotation(rotate);
        mCamera.setParameters(params);
       // mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW", "surfaceDestroyed");
    }

    public static Bitmap decodeStrem(File fil, Uri selectedImage,
                                     Context mContext) {

        Bitmap bitmap = null;
        try {

            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                    .openInputStream(selectedImage));

                /*final int THUMBNAIL_SIZE = getThumbSize(bitmap);

                bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE,
                        THUMBNAIL_SIZE, false);*/

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos
                    .toByteArray()));

            return bitmap = rotateImage(bitmap, fil.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

   /* public static Bitmap rotateImages(Bitmap bmp, String imageUrl) {
        if (bmp != null) {
            ExifInterface ei;
            int orientation = 0;
            try {
                ei = new ExifInterface(imageUrl);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    matrix.postRotate(0);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }
            Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);
            return resizedBitmap;
        } else {
            return bmp;
        }
    }*/


    public static Bitmap decodeFile(File f, int sampling) {
        try {
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    new FileInputStream(f.getAbsolutePath()), null, o2);

            o2.inSampleSize = sampling;
            //o2.inTempStorage = new byte[48 * 1024];

            o2.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(
                    new FileInputStream(f.getAbsolutePath()), null, o2);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitmap = rotateImage(bitmap, f.getAbsolutePath());
            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap rotateImage(Bitmap bmp, String imageUrl) {
        if (bmp != null) {
            ExifInterface ei;
            int orientation = 0;
            try {
                ei = new ExifInterface(imageUrl);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    matrix.postRotate(0);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }
            Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);
            return resizedBitmap;
        } else {
            return bmp;
        }
    }



    public static int getThumbSize(Bitmap bitmap) {

        int THUMBNAIL_SIZE = 250;
        if (bitmap.getWidth() < 300) {
            THUMBNAIL_SIZE = 250;
        } else if (bitmap.getWidth() < 600) {
            THUMBNAIL_SIZE = 500;
        } else if (bitmap.getWidth() < 1000) {
            THUMBNAIL_SIZE = 750;
        } else if (bitmap.getWidth() < 2000) {
            THUMBNAIL_SIZE = 1500;
        } else if (bitmap.getWidth() < 4000) {
            THUMBNAIL_SIZE = 2000;
        } else if (bitmap.getWidth() > 4000) {
            THUMBNAIL_SIZE = 2000;
        }
        return THUMBNAIL_SIZE;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("Salir de la Aplicaci\u00f3n");
            // Setting Dialog Message
            alertDialog.setMessage("\u00bfQuieres salir de la aplicaci\u00f3n?");
            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.delete);
            // On pressing Settings button
            alertDialog.setPositiveButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // on pressing cancel button
            alertDialog.setNegativeButton("Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();

        }
        return true;
    }




}