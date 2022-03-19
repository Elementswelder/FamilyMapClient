package com.example.familymapclient;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Response.UserLoginResponse;
import Response.UserRegisterResponse;
//Do you get a userLogin Response here? What do you do with it?
//Setup the fragment, so the main activity launches the fragemnet?
//The Getevents and get people API
public class ServerProxy {
    //Login
    //Register
    //GetEvents
    //GetPeople

    public UserLoginResponse Login(LoginRequest request){

        try {
            URL url = new URL("http://localhost:8080/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream reqBody = http.getOutputStream();
            Gson gson = new Gson();
            String requestString = gson.toJson(request);

            writeString(requestString, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.i("ServerProxyLogin", "Logged in Successfully");
                return null;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserRegisterResponse Register(RegisterRequest register){

        try {
            URL url = new URL("http://localhost:8080/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream reqBody = http.getOutputStream();
            Gson gson = new Gson();
            String requestString = gson.toJson(register);

            writeString(requestString, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.i("ServerProxyLogin", "Registered Successfully");
                return null;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String readString(InputStream input) throws IOException{
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(input);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();

    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }


}
