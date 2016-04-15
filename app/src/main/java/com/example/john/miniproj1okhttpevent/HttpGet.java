package com.example.john.miniproj1okhttpevent;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import de.greenrobot.event.EventBus;

/**
 * Created by John on 15/4/2016.
 */
public class HttpGet {
    private String url;

    public HttpGet(String url) {
        this.url = url;
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {

                try {

                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder().url(url).build();
                    Response response=client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Reader in=response.body().charStream();
                        BufferedReader reader=new BufferedReader(in);
                        Toilet toilet=
                                new Gson().fromJson(reader, Toilet.class);

                        reader.close();

                        EventBus.getDefault().post(new ToiletEvent(toilet));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }.start();
    }
}