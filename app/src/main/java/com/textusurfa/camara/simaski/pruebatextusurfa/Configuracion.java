package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by simaski on 04/06/16.
 */
public class Configuracion extends Activity {

    //public EditText etDatos;
    public TextInputLayout etDatos;
    public EditText etTiempo;
    public EditText etMarca;
    public FloatingActionButton btGuardar;
    public int bandera= 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.configuracion_layout);

        etDatos = (TextInputLayout) findViewById(R.id.til1);
        etDatos.setCounterEnabled(true);
        etDatos.setCounterMaxLength(116);
        etTiempo = (EditText) findViewById(R.id.etTiempo);
        btGuardar = (FloatingActionButton) findViewById(R.id.btGuardar);
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();

                if (etDatos.getEditText().getText().length() != 0 && etTiempo.getText().length() != 0) {

                    bandera  = Integer.parseInt(etTiempo.getText().toString());
                    if(bandera > 0 && bandera <= 10) {


                        Bundle b = new Bundle();
                        b.putString("datos", etDatos.getEditText().getText().toString());
                        b.putString("tiempo", etTiempo.getText().toString());
                                                    Intent i = new Intent(Configuracion.this, Main.class);
                            i.putExtras(b);
                            startActivity(i);
                            Configuracion.this.finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "El tiempo debe ser mayor a 0 y menor o igual a 10", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void attemptLogin() {

        // Reset errors.
        etDatos.setError(null);
        etTiempo.setError(null);

        // Store values at the time of the login attempt.
        String usuario = etDatos.getEditText().getText().toString();
        String password = etTiempo.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(usuario)) {
            etDatos.setError(getString(R.string.error_campo_requirido));
            focusView = etDatos;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            etTiempo.setError(getString(R.string.error_campo_requirido));
            focusView = etTiempo;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            Intent i = new Intent(Configuracion.this,Main.class);
            startActivity(i);
            Configuracion.this.finish();

        }
        return true;
    }



}
