package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simaski on 11/07/16.
 */
public class JsonUsuarioParser {


    public List<UsuarioTwitter> leerFlujoJson(InputStream in) throws IOException {
        // Nueva instancia JsonReader
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            // Leer Array
            return leerArrayUsuarios(reader);
        } finally {
            reader.close();
        }

    }



    public List<UsuarioTwitter> leerArrayUsuarios(JsonReader reader) throws IOException {
        // Lista temporal
        ArrayList<UsuarioTwitter> usuario = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            // Leer objeto
            usuario.add(leerUsuario(reader));
        }
        reader.endArray();
        return usuario;
    }

    public UsuarioTwitter leerUsuario(JsonReader reader) throws IOException {
        // Variables locales
        String name = null;
        String profile_image_url = null;

        // Iniciar objeto
        reader.beginObject();

        /*
        Lectura de cada atributo
         */
        while (reader.hasNext()) {
            String nam = reader.nextName();
            switch (nam) {
                case "name":
                    name = reader.nextString();

                    break;
                case "profile_image_url":
                    profile_image_url = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new UsuarioTwitter(name, profile_image_url);
    }

}
