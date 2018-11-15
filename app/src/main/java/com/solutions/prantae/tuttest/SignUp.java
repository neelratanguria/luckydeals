package com.solutions.prantae.tuttest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class SignUp extends AppCompatActivity {

    private Button signUpBtn, miscBtn;
    private String mobileNumber, password, email, name, rePassword;
    private EditText emailET, firstNameET, lastNameET, rePasswordET;
    private SQLiteDatabase localDB;
    private LocalDatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.signUp);
        emailET = findViewById(R.id.email);
        firstNameET = findViewById(R.id.firstname);
        lastNameET = findViewById(R.id.lastname);
        rePasswordET = findViewById(R.id.repassword);
        miscBtn = findViewById(R.id.misc);

        localDB = this.openOrCreateDatabase(Constants.PACKAGE_NAME, MODE_PRIVATE, null);

        localDB.execSQL("CREATE TABLE IF NOT EXISTS userinfo (name VARCHAR, email VARCHAR, mobilenumber VARCHAR, token VARCHAR) ");

        miscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this,
                        SecureTokenGenerator.nextToken(), Toast.LENGTH_SHORT).show();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        Intent intent = getIntent();
        mobileNumber = intent.getStringExtra("mobileNumber");
        password = intent.getStringExtra("password");


    }

    public void startSession()
    {
        Intent intent = new Intent(this, Home.class);
        this.startActivity(intent);
    }

    public boolean validator(String email, String name, String rePassword)
    {

        Matcher matcher = Constants.EMAIL_REGEX.matcher(email);

        if (email.isEmpty() || name.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this,
                    "Fields cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!rePassword.equals(password))
        {
            Toast.makeText(this,
                    "Password doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!matcher.find())
        {
            Toast.makeText(this,
                    "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (name.isEmpty())
        {
            Toast.makeText(this,
                    "First name connect be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        matcher = Constants.NAME_REGEX.matcher(name);

        return true;
    }


    private void signUp()
    {
        email = emailET.getText().toString();
        name = firstNameET.getText().toString() + lastNameET.getText().toString();
        rePassword = rePasswordET.getText().toString();

        databaseHandler = new LocalDatabaseHandler(localDB, name, email, mobileNumber);

        boolean isValid = validator(email, name, rePassword);

        if (!isValid)
        {
            return;
        }

        StringRequest signInRequest = new StringRequest(Request.Method.POST, Constants.signUpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("valid");
                            if (status.equals("1"))
                            {
                                Toast.makeText(SignUp.this,
                                        "User logged in", Toast.LENGTH_SHORT).show();
                                databaseHandler.setSession(mobileNumber, SignUp.this);
                            }
                            else
                            {
                                Toast.makeText(SignUp.this,
                                        "Login failed", Toast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUp.this,
                        "Unable to connect to server", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("number", mobileNumber);
                hashMap.put("password", password);
                hashMap.put("email", email);
                hashMap.put("name", name);
                return hashMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(signInRequest);
    }
}
