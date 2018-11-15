package com.solutions.prantae.tuttest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;


public class LocalDatabaseHandler{

    private SQLiteDatabase localDB;
    private Cursor c;
    private String name, email, mobileNumber;

    public LocalDatabaseHandler(SQLiteDatabase localDB)
    {
        this.localDB = localDB;
        try
        {
            localDB.execSQL("CREATE TABLE IF NOT EXISTS userinfo (name VARCHAR, email VARCHAR, mobilenumber VARCHAR, token VARCHAR)");
            c = localDB.rawQuery("SELECT * FROM userinfo", null);

        }
        catch (Exception e)
        {

        }
    }

    public LocalDatabaseHandler(SQLiteDatabase localDB, String name, String email, String mobileNumber)
    {
        this.localDB = localDB;
        c = localDB.rawQuery("SELECT * FROM userinfo", null);
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getName()
    {
        String result="";
        int nameIndex = c.getColumnIndex("name");
        if (c.moveToFirst()) {
            result = c.getString(nameIndex);
        }
        return result;
    }

    public String getMobileNumber()
    {
        Cursor c = localDB.rawQuery("SELECT * FROM userinfo", null);
        String result="";
        int mobileNumberIndex = c.getColumnIndex("mobilenumber");
        if (c.moveToFirst()) {
            result = c.getString(mobileNumberIndex);
        }
        return result;
    }

    public String getEmail()
    {
        Cursor c = localDB.rawQuery("SELECT * FROM userinfo", null);
        String result="";
        int emailIndex = c.getColumnIndex("email");
        if (c.moveToFirst()) {
            result = c.getString(emailIndex);
        }
        return result;
    }

    public String getToken()
    {
        Cursor c = localDB.rawQuery("SELECT * FROM userinfo", null);
        String result="";
        int emailIndex = c.getColumnIndex("token");
        if (c.moveToFirst()) {
            result = c.getString(emailIndex);
        }
        return result;
    }


    public void setSession(final String mobileNumber, final Context context)
    {
        this.mobileNumber = mobileNumber;
        SecureTokenGenerator secureTokenGenerator = new SecureTokenGenerator();

        final String token = secureTokenGenerator.nextToken();

        StringRequest signInRequest = new StringRequest(Request.Method.POST, Constants.SESSION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String status = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            status = jsonObject.getString("valid");
                            if (status.equals("1"))
                            {
                                saveSession(token);
                                Intent intent = new Intent(context, Home.class);
                                context.startActivity(intent);
                            }
                            else if (status.equals("2"))
                            {
                                setSession(mobileNumber, context);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("number", mobileNumber);
                hashMap.put("token", token);
                return hashMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(signInRequest);
    }

    private void saveSession(String token)
    {
        localDB.execSQL("DELETE FROM userinfo");

        SQLiteStatement statement;
        if (!(name==null) && !(email==null))
        {
            String sql = "INSERT INTO userinfo (name, email, mobilenumber, token) VALUES (?, ?, ?, ?)";
            statement = localDB.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindString(2, email);
            statement.bindString(3, mobileNumber);
            statement.bindString(4, token);
        }
        else
        {
            String sql = "INSERT INTO userinfo (name, email, mobilenumber, token) VALUES (?, ?, ?, ?)";
            statement = localDB.compileStatement(sql);
            statement.bindString(1, "");
            statement.bindString(2, "");
            statement.bindString(3, mobileNumber);
            statement.bindString(4, token);
        }

        statement.execute();
    }

    public void checkSession()
    {
        Cursor c = localDB.rawQuery("SELECT * FROM userinfo", null);
        String result="";
        int emailIndex = c.getColumnIndex("token");
        if (c.moveToFirst()) {
            result = c.getString(emailIndex);
        }
    }

}
