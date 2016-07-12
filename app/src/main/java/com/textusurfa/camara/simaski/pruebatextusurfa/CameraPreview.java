package com.textusurfa.camara.simaski.pruebatextusurfa;

/**
 * Created by simaski on 03/06/16.
 */
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

@SuppressLint("NewApi")
public class CameraPreview extends TextureView implements
        TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private TextureView mTextureView;
    public CameraPreview(Context context , Camera camera) {
        super(context);
        mCamera = camera;
        // TODO Auto-generated constructor stub
    }


    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
//        mCamera = Camera.open();
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

}