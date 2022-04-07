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
import Response.EventRRResponse;
import Response.PersonRRResponse;
import Response.UserLoginResponse;
import Response.UserRegisterResponse;
//Do you get a userLogin Response here? What do you do with it?
//DO we need our tests done by the login passoff?
public class ServerProxy {

    String host;
    String port;

    public ServerProxy(String host, String port){
        this.host = host;
        this.port = port;
    }
    //Login
    //Register
    //GetEvents
    //GetPeople

    public UserLoginResponse Login(LoginRequest request){

        try {
            URL url = new URL("http://" + host + ":" + port + "/user/login");
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
                InputStream reqqBody = http.getInputStream();
                String parseRequest = readString(reqqBody);
                UserLoginResponse response = gson.fromJson(parseRequest, UserLoginResponse.class);

                Log.i("ServerProxyLogin", "Logged in Successfully");
                return response;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
                return gson.fromJson(respData, UserLoginResponse.class);
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
            URL url = new URL("http://" + host + ":" + port + "/user/register");
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
                InputStream reqBodyReg = http.getInputStream();
                String parseRequest = readString(reqBodyReg);
                UserRegisterResponse response = gson.fromJson(parseRequest, UserRegisterResponse.class);
                //Log.i("ServerProxyLogin", "Registered Successfully");
                return response;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
                UserRegisterResponse response = gson.fromJson(respData, UserRegisterResponse.class);
                return response;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public EventRRResponse Event(String authtoken){

        try {
            URL url = new URL("http://" + host + ":" + port + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authtoken);
            http.addRequestProperty("Accept", "application/json");

            http.connect();
            Gson gson = new Gson();
            /*OutputStream reqBody = http.getOutputStream();
            Gson gson = new Gson();
            String requestString = gson.toJson(request);

            writeString(requestString, reqBody);

            reqBody.close(); */

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream reqqBody = http.getInputStream();
                String parseRequest = readString(reqqBody);
                EventRRResponse response = gson.fromJson(parseRequest, EventRRResponse.class);

                Log.i("ServerProxyLogin", "Obtained Events Successfully");
                return response;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
                return gson.fromJson(respData, EventRRResponse.class);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PersonRRResponse Person(String authtoken){

        try {
            URL url = new URL("http://" + host + ":" + port + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authtoken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            /*OutputStream reqBody = http.getOutputStream();
            Gson gson = new Gson();
            String requestString = gson.toJson(request);

            writeString(requestString, reqBody);

            reqBody.close(); */

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream reqqBody = http.getInputStream();
                String parseRequest = readString(reqqBody);
                PersonRRResponse response = gson.fromJson(parseRequest, PersonRRResponse.class);

                Log.i("Person", "Got people Successfully");
                return response;
            }
            else {
                Log.i("ServerProxyLogin", http.getResponseMessage());

                InputStream resbody = http.getErrorStream();

                String respData = readString(resbody);

                Log.i("ServerProxyLogin", respData);
                return gson.fromJson(respData, PersonRRResponse.class);
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
