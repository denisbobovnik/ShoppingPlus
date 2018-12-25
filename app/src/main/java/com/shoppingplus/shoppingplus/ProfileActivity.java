package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvVerified;
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
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        tvVerified = (TextView) findViewById(R.id.tvVerified);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.isEmailVerified()) {
            tvVerified.setText(getResources().getString(R.string.emailVerified));
        } else {
            tvVerified.setText(getResources().getString(R.string.emailNotVerified));
            tvVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.verEmailSent), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.alreadyHere), Toast.LENGTH_SHORT).show();
        } else { //logout
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
