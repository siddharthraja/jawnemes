package com.example.sid.muc_glass;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

class ServerInterface{

    String globalResp = null;

    public String conn(){
        globalResp = "nothing";
        ServerPing task = new ServerPing();
        task.execute();

        try
        {
            task.get();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        return globalResp;
    }

    private class ServerPing extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String resp = "nothing11";
            try {
                System.out.println("opening connection...");
                URL serv = new URL("http://128.61.63.194:5000/");
                HttpURLConnection urlConnection = (HttpURLConnection) serv.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine = "iii";
                System.out.println("something");

                while ((inputLine = in.readLine()) != null)
                    globalResp = inputLine;
                in.close();
                return null;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

    };



}