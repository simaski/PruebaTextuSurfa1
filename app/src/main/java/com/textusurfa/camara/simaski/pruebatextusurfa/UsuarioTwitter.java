package com.textusurfa.camara.simaski.pruebatextusurfa;

/**
 * Created by simaski on 11/07/16.
 */
public class UsuarioTwitter {
    private String name;
    private String profile_image_url;

    public UsuarioTwitter(String name, String profile_image_url){
        this.name = name;
        this.profile_image_url = profile_image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

}


