package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLog;
    private EditText etPasswordLog;
    private Button btnLoginLog;
    private TextView tvCreateAccLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailLog = (EditText) findViewById(R.id.etEmailLog);
        etPasswordLog = (EditText) findViewById(R.id.etPasswordLog);

        btnLoginLog = (Button) findViewById(R.id.btnLoginLog);
        tvCreateAccLog = (TextView) findViewById(R.id.tvCreateAccLog);

        tvCreateAccLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


    }
}
