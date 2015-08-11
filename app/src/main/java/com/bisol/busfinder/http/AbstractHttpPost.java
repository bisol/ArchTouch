package com.bisol.busfinder.http;

import android.os.AsyncTask;
import android.util.Log;

import com.bisol.busfinder.BusFinder;
import com.bisol.busfinder.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Base for HTTP POST services.
 * Assumes the request body has a single variable parameter denoted by '_P_'. This is replaced by
 * the first parameter of parameter of execute(String...).
 */
public abstract class AbstractHttpPost extends AsyncTask<String, Void, String> {
    protected final String LOG_TAG = getClass().getSimpleName();
    protected final BusFinder owner;

    protected AbstractHttpPost(BusFinder owner){
        this.owner = owner;
    }

    /** Provides the service URL */
    protected abstract String getUrl();

    /** Provides the request body template */
    protected abstract String getBodyTemplate();


    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        HttpsURLConnection conn = null;
        try {
            String bodyParameter = params[0];
            String requestBody = getBodyTemplate().replace("_P_", bodyParameter);

            conn = openHttpConnection(requestBody);
            is = conn.getInputStream();
            return readStream(is);
        } catch (Exception e) {
            String errorMessage = owner.getString(R.string.bus_finder_service_error);
            Log.e(LOG_TAG, errorMessage, e);
            return null;
        } finally {
            // Clean up resources
            if(conn != null){
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(LOG_TAG, owner.getString(R.string.bus_finder_resource_warning), e);
                }
            }
        }
    }

    /** Creates and executes an HTTP connection */
    private HttpsURLConnection openHttpConnection(String requestBody) throws IOException {
        URL url = new URL(getUrl());
        Log.d(LOG_TAG, "connecting to " + url.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Length", "" + requestBody.getBytes("utf8").length);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty(BusFinder.SERVICE_APPGLU_ENVIRONMENT_HEADER_KEY, BusFinder.SERVICE_APPGLU_ENVIRONMENT_HEADER_VALUE);

        Log.d(LOG_TAG, "sending request: " + requestBody);
        Writer writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(requestBody);
        writer.close();
        conn.connect();
        return conn;
    }

    /** Convert the InputStream into a string */
    public String readStream(InputStream stream) throws IOException {
        return new Scanner(stream).useDelimiter("\\A").next();
    }

}