package com.solutions.prantae.tuttest;

import android.content.Intent;
import android.app.Activity;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button signUpBtn, signInBtn;
    private EditText mobileET, passwordET;
    private static final int SIGN_UP = 0;
    private static final int SIGN_IN = 1;
    private SQLiteDatabase localDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call the function callInstamojo to start payment here
        signUpBtn = findViewById(R.id.signUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
        mobileET = findViewById(R.id.mobileET);
        passwordET = findViewById(R.id.passwordET);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        localDB = this.openOrCreateDatabase(Constants.PACKAGE_NAME, MODE_PRIVATE, null);
    }

    private void signUp()
    {

        final String mobileNumber = mobileET.getText().toString();
        final String password = passwordET.getText().toString();

        boolean isValid = validator(mobileNumber, password);

        if (!isValid)
        {
            return;
        }

        connectToDb(mobileNumber, "-1", MainActivity.SIGN_UP);
    }



    public void signIn()
    {
        final String mobileNumber = mobileET.getText().toString();
        final String password = passwordET.getText().toString();

        HasherSHA hasherSHA = new HasherSHA();

        String hashPass= "";
        try {
            hashPass = hasherSHA.main(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), hashPass, Toast.LENGTH_SHORT).show();

        boolean isValid = validator(mobileNumber, password);

        if (!isValid)
        {
            return;
        }

        connectToDb(mobileNumber, password, MainActivity.SIGN_IN);
    }

    private void positiveResponse(String mobileNumber, String password, int signUpOrSignIn)
    {
        if(signUpOrSignIn==MainActivity.SIGN_UP)
        {
            Intent intent = new Intent(this, SignUp.class);
            intent.putExtra("mobileNumber", mobileNumber);
            intent.putExtra("password", password);
            this.startActivity(intent);
        }
        else if (signUpOrSignIn==MainActivity.SIGN_IN)
        {
            Toast.makeText(MainActivity.this,
                    "User logged in", Toast.LENGTH_SHORT).show();
            LocalDatabaseHandler databaseHandler = new LocalDatabaseHandler(localDB);
            databaseHandler.setSession(mobileNumber, MainActivity.this);

            Intent intent = new Intent(this, Home.class);
            this.startActivity(intent);
        }
    }

    private void negativeResponse(int signUpOrSignIn)
    {
        if(signUpOrSignIn==MainActivity.SIGN_UP)
        {
            Toast.makeText(MainActivity.this,
                    "Account already exist", Toast.LENGTH_SHORT).show();
        }
        else if (signUpOrSignIn==MainActivity.SIGN_IN)
        {
            Toast.makeText(MainActivity.this,
                    "Login failed", Toast.LENGTH_SHORT ).show();
        }
    }

    private void connectToDb(final String mobileNumber, final String password,
                             final int signUpOrSignIn)
    {
        StringRequest signInRequest = new StringRequest(Request.Method.POST, Constants.signInUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("valid");
                            if (signUpOrSignIn==MainActivity.SIGN_UP)
                            {
                                if (status.equals("2") || status.equals("1"))
                                {
                                    negativeResponse(signUpOrSignIn);
                                }
                                else
                                {
                                    positiveResponse(mobileNumber, passwordET.getText().toString(),
                                            signUpOrSignIn);
                                }
                            }
                            else if (signUpOrSignIn==MainActivity.SIGN_IN)
                            {
                                if (status.equals("1"))
                                {
                                    positiveResponse(mobileNumber, password, signUpOrSignIn);
                                }
                                else
                                {
                                    negativeResponse(signUpOrSignIn);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,
                        "Unable to connect to server", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("number", mobileNumber);
                hashMap.put("password", password);
                return hashMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(signInRequest);

        return;
    }

    private boolean validator(String mobileNumber, String password)
    {
        if (mobileNumber.equals("") || password.equals(""))
        {
            Toast.makeText(this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (mobileNumber.length() != 10)
        {
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length() < 8)
        {
            Toast.makeText(this, "Pasword is too short", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
