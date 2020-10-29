package me.dreamhopping.mediamod.util;

import com.google.gson.Gson;
import me.dreamhopping.mediamod.MediaMod;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class WebRequest {
    public static final WebRequest instance = new WebRequest();

    private final OkHttpClient client;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public WebRequest() {
        OkHttpClient.Builder builder = configureToIgnoreCertificate(new OkHttpClient.Builder());
        client = builder.build();
    }

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
                .put(RequestBody.create(JSON, ""))
                .build();

        Response response = client.newCall(request).execute();
        return response.code();
    }

    private OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        MediaMod.INSTANCE.logger.warn("Ignore Ssl Certificate");

        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.warn("Exception while configuring IgnoreSslCertificate" + e, e);
        }
        return builder;
    }

}
