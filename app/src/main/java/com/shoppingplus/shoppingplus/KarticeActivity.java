package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class KarticeActivity extends AppCompatActivity {

    private TextView tvCreateAccLog;
    private TextView tvEmailKartice;
    private EditText etEmailKartice;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartice);

        tvCreateAccLog = (TextView) findViewById(R.id.tvCreateAccLog);
        tvEmailKartice = (TextView) findViewById(R.id.tvEmailKartice);

        etEmailKartice = (EditText) findViewById(R.id.etEmailKartice);




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(KarticeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout

        }
        return super.onOptionsItemSelected(item);
    }
}
