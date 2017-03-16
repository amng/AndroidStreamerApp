package com.home.amngomes.controller;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private String serverIp = "http://192.168.1.70:8080";

    private StreamerApi service;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(StreamerApi.class);
    }

    private RetrofitClient(String serverIp) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(StreamerApi.class);
    }

    public StreamerApi getService(){
        return service;
    }

    public static RetrofitClient getInstance(){
        return instance == null ? instance = new RetrofitClient() : instance;
    }

    public String getFilePath(String dir, String file){
        Log.e("server file: ", serverIp + "/file/" + Base64.encodeToString((dir + "/" + file)
                .getBytes(), Base64.URL_SAFE).replaceAll("\n", ""));
        return serverIp + "/file/" + Base64.encodeToString((dir + "/" + file)
                .getBytes(), Base64.URL_SAFE).replaceAll("\n", "");
    }

    public String getImagePath(long songId){
        return serverIp + "/songs/"+songId+"/image";
    }

    public String getVideoPath(long songId){
        return serverIp + "/songs/"+songId+"/image";
    }

    public String getSongPath(long songId){
        return serverIp + "/songs/"+songId+"/song";
    }

    public Boolean updateServerIp(String serverIp){
        this.serverIp = serverIp;
        try {
            instance = new RetrofitClient(serverIp);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getIp(){
        return serverIp;
    }


}
