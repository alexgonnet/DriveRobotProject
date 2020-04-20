package com.example.driverobotproject;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class Connectivity extends AsyncTask<Data, Void, String> {
    public static String idProject = "30";
    String surl;

    @Override
    protected String doInBackground(Data... params) {
        surl = "http://cabani.free.fr/ise/adddata.php?idproject="+idProject+"&lux="+params[0].luminosity+"&timestamp="+(params[0].timestamp.getTime()/1000)+"&action="+params[0].action;
        String response = "";

        try{
            URL url = new URL(surl);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            Log.d("Web","connect2");
            connection.connect();
            int code = connection.getResponseCode();
            Log.d("Web","code:"+code);
            if(code
                    == HttpURLConnection.HTTP_OK){
                InputStream inputStream = connection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                Log.d("Web",sb.toString());
                response = sb.toString();
                inputStream.close();
            }else{
                Log.d("Web","code no good");
            }
            connection.disconnect();
        }catch (Exception e){
            response = e.getMessage();
            Log.d("Web","error:"+e.toString());
            e.printStackTrace();
        }
        Log.d("Web","disconnect");
        return response;
    }
}
