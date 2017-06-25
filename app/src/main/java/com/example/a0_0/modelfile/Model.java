package com.example.a0_0.modelfile;

import android.net.Uri;

/**
 * Created by 0_0 on 6/25/2017.
 */

public class Model
{
    private String name;
    private String url;

    public Model(){}

    public Model(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
