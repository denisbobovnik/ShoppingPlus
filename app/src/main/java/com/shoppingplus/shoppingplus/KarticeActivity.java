package com.shoppingplus.shoppingplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class KarticeActivity extends AppCompatActivity {

    private TextView tvCreateAccLog;
    private TextView tvEmailKartice;
    private EditText etEmailKartice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartice);

        tvCreateAccLog = (TextView) findViewById(R.id.tvCreateAccLog);
        tvEmailKartice = (TextView) findViewById(R.id.tvEmailKartice);

        etEmailKartice = (EditText) findViewById(R.id.etEmailKartice);


    }
}
