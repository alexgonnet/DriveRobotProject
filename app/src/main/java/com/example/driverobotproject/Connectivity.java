package com.example.driverobotproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.telecom.Call;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.textclassifier.ConversationActions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Connectivity extends AsyncTask<Data, Void, String> {

    String action;
    String luminosity;
    Timestamp timestamp;
    //static String surl = "http://cabani.net/ise/adddata.php";

    @Override
    protected String doInBackground(Data... params) {
        this.action = params[0].action;
        this.luminosity = params[0].luminosity;
        this.timestamp = params[0].timestamp;
        return executeRequete();
    }

    public String executeRequete(){
        HttpURLConnection urlConnection = null;
        String webcontent = null;
        try {
            String surl = "http://cabani.free.fr/ise/adddata.php";
            URL url = new URL(surl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            OutputStream out = urlConnection.getOutputStream();
            OutputStreamWriter outSW = new OutputStreamWriter(out);
            BufferedWriter w = new BufferedWriter(outSW);
            w.write("idproject=30&lux=100&timestamp=1488534460&action=avancer");
            w.flush();
            w.close();
            out.close();



            /*urlConnection.setChunkedStreamingMode(0);
            //urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.connect();

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
*/
/*
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("ID", "30")
                    .appendQueryParameter("Timestamp", String.valueOf((timestamp.getTime()/1000)))
                    .appendQueryParameter("Luminosité", luminosity)
                    .appendQueryParameter("Action", action);
            String query = builder.build().getEncodedQuery();

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            w.write(query);
            w.flush();
            w.close();
            out.close();*/
/*
            Map<String, String> parameters = new HashMap<>();
            parameters.put("ID", "30");
            parameters.put("Timestamp", String.valueOf((timestamp.getTime()/1000)));
            parameters.put("Luminosité", luminosity);
            parameters.put("Action", action);

            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
*/
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return webcontent;
    }

    private String generateString(InputStream in) {
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        try {
            String cur;
            while((cur = buffer.readLine()) != null){
                sb.append(cur + System.getProperty("line.separator"));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }
}
