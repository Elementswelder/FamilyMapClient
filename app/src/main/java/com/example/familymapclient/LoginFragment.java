package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;
import Response.EventRRResponse;
import Response.PersonRRResponse;
import Response.UserLoginResponse;
import Response.UserRegisterResponse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Listener listener;
    private String mParam1;
    private String mParam2;
    private static String MESSAGE = "message";
    Button loginButton;
    Button registerButton;
    RadioGroup radioGroup;
    RadioButton maleButton, femaleButton, selectButton;
    EditText server, port, username, password, firstName, lastName, email, gender;
    String serverInput, portInput, usernameInput, passwordInput, firstNameInput, lastNameInput, emailInput, genderText;
    boolean clicked = false;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            serverInput = server.getText().toString();
            portInput = port.getText().toString();
            usernameInput = username.getText().toString();
            passwordInput = password.getText().toString();
            firstNameInput = firstName.getText().toString();
            lastNameInput = lastName.getText().toString();
            emailInput = email.getText().toString();
            checkButtonActivate();


        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public interface Listener {
        void notifyDone();
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //Setup all the IDs and Link them to the LoginFragment vars
        registerButton = view.findViewById(R.id.registerButton);
        loginButton = view.findViewById(R.id.signInButton);
        server = view.findViewById(R.id.serverHostField);
        port = view.findViewById(R.id.serverPortField);
        username = view.findViewById(R.id.usernameField);
        password = view.findViewById(R.id.passwordField);
        firstName = view.findViewById(R.id.firstNameField);
        lastName = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailField);
        radioGroup = view.findViewById(R.id.genders);
        maleButton = view.findViewById(R.id.maleGender);
        femaleButton = view.findViewById(R.id.femaleGender);
        radioGroup = view.findViewById(R.id.genders);

        //server.setText("10.0.2.2");
        server.setText("192.168.2.57");
        port.setText("8080");
        username.setText("sheil");
        password.setText("parker");




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                clicked = true;
                selectButton = (RadioButton)view.findViewById(i);
                checkButtonActivate();
            }
        });



        //Setuptext Watcher for TextChange
        server.addTextChangedListener(textWatcher);
        port.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();
                        String newMessage = bundle.getString(MESSAGE, "");
                        if (newMessage.contains("Error")){
                            Toast.makeText(getActivity(), "Failed to Login", Toast.LENGTH_LONG).show();
                        } else {
                            listener.notifyDone();
                        }
                       // totalSizeTextView.setText(getString(R.string.downloadSizeLabel, totalSize));
                    }
                };

                LoginTask loginTask = new LoginTask(uiThreadMessageHandler, usernameInput, passwordInput, serverInput, portInput, firstNameInput, lastNameInput);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(loginTask);
                if (listener != null){
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectButton.getText().equals("Male")) {
                    genderText = "Male";
                }
                else {
                    genderText = "Female";
                }
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();
                        String newMessage = bundle.getString(MESSAGE, "");
                        if (newMessage.contains("Error")){
                            Toast.makeText(getActivity(), "Failed to Register", Toast.LENGTH_LONG).show();
                        } else {
                            listener.notifyDone();
                        }
                        // totalSizeTextView.setText(getString(R.string.downloadSizeLabel, totalSize));
                    }
                };
                RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler, usernameInput, passwordInput, emailInput, firstNameInput, lastNameInput, genderText,
                        serverInput, portInput);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(registerTask);
                if (listener != null){
               //     listener.n
                }
            }
        });
        return view;
    }



    private static class LoginTask implements Runnable {
        private String username;
        private String password;
        private String host;
        private String port;
        private String firstName;
        private String lastName;
        private final Handler messageHandler;


        public LoginTask(Handler messageHandler, String username, String password, String host, String port, String first, String last){
            this.firstName = first;
            this.lastName = last;
            this.messageHandler = messageHandler;
            this.username = username;
            this.password = password;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            LoginRequest loginRequest = new LoginRequest(username, password);
            ServerProxy serverProxy = new ServerProxy(host, port);
            UserLoginResponse userLoginResponse = serverProxy.Login(loginRequest);

            if (!userLoginResponse.isSuccess()) {
                sendMessage(userLoginResponse.getMessage());
            } else {
                DataCache cache = DataCache.getInstance();
                sendMessage(firstName, lastName);
                PersonRRResponse people = serverProxy.Person(userLoginResponse.getAuthtoken());
                EventRRResponse events = serverProxy.Event(userLoginResponse.getAuthtoken());
                cache.loadData(events.getData(), people.getPersonData(), userLoginResponse.getAuthtoken(),userLoginResponse.getPersonID(), userLoginResponse.getUsername(),
                        firstName, lastName);

            }
        }
        private void sendMessage(String theMessage){
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE, theMessage);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);

        }

        private void sendMessage(String first, String last){
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE, first + " " + last);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }

    }

    private class RegisterTask implements Runnable {
        private String username;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private String gender;
        private String host;
        private String port;
        private final Handler messageHandler;

        public RegisterTask(Handler messageHandler, String username, String password, String email, String firstName, String lastName, String gender, String host, String port) {
            this.messageHandler = messageHandler;
            this.username = username;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email, firstName, lastName, gender);
            ServerProxy serverProxy = new ServerProxy(host, port);
            UserRegisterResponse response = serverProxy.Register(registerRequest);

             if (!response.isSuccess()) {
                 sendMessage(response.getMessage());
            } else {
                DataCache cache = DataCache.getInstance();
                sendMessage(firstName, lastName);
                PersonRRResponse people = serverProxy.Person(response.getAuthtoken());
                EventRRResponse events = serverProxy.Event(response.getAuthtoken());
                cache.loadData(events.getData(), people.getPersonData(), response.getAuthtoken(), response.getPersonsID(), response.getUsername(),
                firstName, lastName);

            }
        }

        private void sendMessage(String theMessage){
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE, theMessage);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);

        }

        private void sendMessage(String first, String last){
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE, first + " " + last);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
    private void checkButtonActivate(){
        registerButton.setEnabled(!serverInput.isEmpty() && !portInput.isEmpty() && !usernameInput.isEmpty() && !passwordInput.isEmpty() && !firstNameInput.isEmpty() &&
                !lastNameInput.isEmpty() && !emailInput.isEmpty() && clicked);

        loginButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() && !portInput.isEmpty() && !serverInput.isEmpty());
    }

}