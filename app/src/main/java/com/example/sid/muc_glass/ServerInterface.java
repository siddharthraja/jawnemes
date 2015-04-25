package com.example.sid.muc_glass;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class ServerInterface{

    String globalResp = null;
    public String conn(){
        globalResp = "nothing";
        new ServerPing().execute();
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
                URL serv = new URL("http://128.61.55.136:8888/srd/gc.php");
                URLConnection yc = serv.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
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