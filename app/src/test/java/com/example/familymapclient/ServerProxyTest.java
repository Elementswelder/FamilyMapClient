package com.example.familymapclient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import Request.LoginRequest;
import Request.RegisterRequest;
import Response.UserRegisterResponse;


public class ServerProxyTest {

    ServerProxy proxy;
    RegisterRequest registerRequest;
    LoginRequest loginRequest;
    String authtoken;


    @Before
    public void setup() {
        proxy = new ServerProxy("localhost", "8080");
        registerRequest = new RegisterRequest("User", "pass", "test@email.com",
                "Bob", "Miller", "Male");
        authtoken = null;
    }

    @Test
    public void registerPass(){
       UserRegisterResponse resposne =  proxy.Register(registerRequest);
       assertNotNull(resposne.getAuthtoken());
       assertNotNull(resposne.getPersonsID());
       assertEquals("User", resposne.getUsername());


    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
