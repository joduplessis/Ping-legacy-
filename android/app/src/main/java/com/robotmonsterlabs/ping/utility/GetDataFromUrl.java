package com.robotmonsterlabs.ping.utility;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class GetDataFromUrl {

    public void getDataFromUrl() {
        // constructor
    }

    public String getData(String url) throws IOException {

        // Echo log
        Log.i("PIING", "Getting data for "+url);

        // new okhttp client
        OkHttpClient client = new OkHttpClient();

        // build our request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // get our reponse
        Response response = client.newCall(request).execute();

        // return the string
        return response.body().string();

    }

}
