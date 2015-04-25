package com.example.sid.muc_glass;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Conn2
{
    String urlText;
    Button goButton;
    TextView displayText;


    protected String makeConn()
    {

        urlText = "http://128.61.55.136:8888/srd/gc.php";

        //displayText = (TextView)findViewById(R.id.displayText);

        return getDataFromURL(urlText);

    }

    private String getDataFromURL(String url)
    {
        URLLookup task = new URLLookup(url);
        task.execute();
        return task.returnedString;
    }

    private boolean isLegal(String string)
    {
        return (string != null && string.length() != 0);
    }

    public class URLLookup extends AsyncTask<Void, Void, Void>
    {
		/*
		 * Start an Async task that looks up the URL
		 */

        String returnedString = "a";
        String URL;

        URLLookup(String URL)
        {
            this.URL = URL;
        }

        @Override
        protected void onPreExecute()
        {
            // Do nothing
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            returnedString = asyncTaskPerform(URL);

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Do nothing
            //displayString(returnedString);
        }
    }

    private String asyncTaskPerform(String URL)
    {
        InputStream iS;
        String responseString;

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(URL);
        try
        {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            iS = entity.getContent();
            responseString = convertStreamToString(iS);
        }
        catch (ClientProtocolException e)
        {
            responseString = "E: " + e;
        }
        catch (IOException e)
        {
            responseString = "E: " + e;
        }

        return responseString;
    }

    private String convertStreamToString(InputStream is)
    {
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try
        {
            String line;
            while((line = rd.readLine()) != null)
            {
                total.append(line);
            }
        }
        catch(Exception e)
        {
            // Do nothing
        }

        return total.toString();
    }



    private void displayString(String string)
    {
        displayText.setText(string);
    }
}
