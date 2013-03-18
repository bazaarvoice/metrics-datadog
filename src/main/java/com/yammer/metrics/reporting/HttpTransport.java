package com.yammer.metrics.reporting;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class HttpTransport implements Transport {
    private final HttpClient client;
    private final String seriesUrl;

    public HttpTransport(String host, String apiKey) {
        this.client = newHttpClient();
        this.seriesUrl = String.format("https://%s/api/v1/series?api_key=%s", host, apiKey);
    }

    // allow other url paths (useful for testing with postbin etc)
    public HttpTransport(URL hostUrl, String apiKey, String applicationKey) {
        this.client = newHttpClient();
        if (applicationKey != null) {
            this.seriesUrl = String.format("%s?api_key=%s&application_key=%s", hostUrl, apiKey, applicationKey);
        } else {
            this.seriesUrl = String.format("%s?api_key=%s", hostUrl, apiKey);
        }
    }

    private static HttpClient newHttpClient() {
        HttpParams params = new SyncBasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 30000);

        return new DefaultHttpClient(new PoolingClientConnectionManager(), params);
    }

    public HttpRequest prepare() throws IOException {
        HttpPost post = new HttpPost(seriesUrl);
        return new HttpRequest(client, post);
    }

    public static class HttpRequest implements Transport.Request {
        private final HttpPost postRequest;
        private final ByteArrayOutputStream out;
        private final HttpClient requestClient;

        public HttpRequest(HttpClient requestClient, HttpPost postRequest) throws IOException {
            this.requestClient = requestClient;
            this.postRequest = postRequest;
            this.postRequest.addHeader("Content-Type", "application/json");
            this.out = new ByteArrayOutputStream();
        }

        public OutputStream getBodyWriter() {
            return out;
        }

        public void send() throws Exception {
            try {
                out.flush();
                out.close();
                postRequest.setEntity(new ByteArrayEntity(out.toByteArray()));
                HttpResponse response = requestClient.execute(postRequest);
                EntityUtils.consumeQuietly(response.getEntity());
            } finally {
                postRequest.reset();    // We don't reuse the postRequest between metrics pushes - but this
            }
        }
    }
}