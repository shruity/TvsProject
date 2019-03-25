package com.tvsproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {
    Context context;
    TextInputLayout inputUserName, inputPassword;
    TextInputEditText etUserName, etPassword;
    Button btLogin;
    ProgressDialog dialog;

    OkHttpClient client = new OkHttpClient();
    private static final String url = "http://tvsfit.mytvs.in/reporting/vrm/api/test_new/int/gettabledata.php ";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        inputUserName = findViewById(R.id.inputUserName);
        inputPassword = findViewById(R.id.inputPassword);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);

        etUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                etUserName.clearFocus();
                etPassword.requestFocus();
                return false;
            }
        });

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (validate()) {
                    new LoginTask().execute();
                }
                return false;
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new LoginTask().execute();
                }
            }
        });
    }

    public Boolean validate() {
        if (etUserName.getText().toString().trim().length() == 0) {
            inputUserName.setError("Enter username");
            return false;
        } else if (etPassword.getText().toString().trim().length() == 0) {
            inputPassword.setError("Enter password");
            return false;
        }
        return true;
    }

    private JSONObject getLoginJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", etUserName.getText().toString().trim());
            jsonObject.put("password", etPassword.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String postdata(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(body);
        Request request = builder.build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }


    private class LoginTask extends AsyncTask<Void, Void, String> {

        LoginTask() {
            dialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Logging in");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject jsonObject = getLoginJson();
            String response = null;
            try {
                response = postdata(url, jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing())
                dialog.dismiss();

            try {
                if (s != null) {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.has("TABLE_DATA")) {
                        Intent intent = new Intent(context, ListActivity.class);
                        intent.putExtra("data", s);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Enter correct details", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onBackPressed(){
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            View view1 = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(view1,"Press once again to exit!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            View view=snackbar.getView();
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        if (dialog.isShowing())
            dialog.dismiss();
        super.onPause();

    }
}
