package com.home.amngomes.controller;

import com.home.amngomes.models.ExplorerFile;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StreamerApi {

    @POST("dir/")
    public Call<ArrayList<ExplorerFile>> getDirectories(@Body String dir);

    @POST("hash/")
    public Call<String> getHash(@Body String dir);

    @POST("disks/")
    public Call<ArrayList<String>> getDisks();
}
