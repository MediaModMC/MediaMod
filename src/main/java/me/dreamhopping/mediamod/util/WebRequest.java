package me.dreamhopping.mediamod.util;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class WebRequest {
    public static final WebRequest instance = new WebRequest();

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public <T> T post(URL url, String body, Class<T> type) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return new Gson().fromJson(responseBody.string(), type);
        } else {
            return null;
        }
    }

    public <T> T post(URL url, Class<T> type) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return new Gson().fromJson(responseBody.string(), type);
        } else {
            return null;
        }
    }

    public <T> T get(URL url, Class<T> type) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return new Gson().fromJson(responseBody.string(), type);
        } else {
            return null;
        }
    }

    public <T> T get(URL url, Class<T> type, HashMap<String, String> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return new Gson().fromJson(responseBody.string(), type);
        } else {
            return null;
        }
    }

    public int post(URL url, HashMap<String, String> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(RequestBody.create(JSON, ""))
                .build();

        Response response = client.newCall(request).execute();
        return response.code();
    }

    public int put(URL url, HashMap<String, String> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(RequestBody.create(JSON, ""))
                .build();

        Response response = client.newCall(request).execute();
        return response.code();
    }
}
