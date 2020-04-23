package com.example.driverobotproject;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequeteHttp extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings){
        return null;
    }

    public String executeRequete(){
        HttpURLConnection urlConnection = null;
        String content = new String();
        try{
            URL url = new URL("http://cabani.net/ise/listdata.php?idproject=24");
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            content = generateString(inputStream);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            return content;
        }
    }

    private String generateString(InputStream inputStream){
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        try{
            String current;
            while ((current = buffer.readLine()) != null){
                builder.append(current + System.getProperty(""));
            }
            inputStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
