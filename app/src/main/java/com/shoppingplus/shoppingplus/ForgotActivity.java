package com.shoppingplus.shoppingplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.ProviderQueryResult;

public class ForgotActivity extends AppCompatActivity {

    private TextView tvDetailsForgot;
    private EditText etEmailForgot;
    private Button btnSendEmailForgot;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, KarticeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        tvDetailsForgot = (TextView) findViewById(R.id.tvDetailsForgot);
        etEmailForgot = (EditText) findViewById(R.id.etEmailForgot);

        btnSendEmailForgot = (Button) findViewById(R.id.btnSendEmailForgot);
        btnSendEmailForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posljiEmailPozabljenoGeslo();
            }
        });
    }

    private void posljiEmailPozabljenoGeslo() {
        final String email = etEmailForgot.getText().toString().trim();

        if(email.isEmpty()) {
            etEmailForgot.setError(getResources().getString(R.string.emailRequired));
            etEmailForgot.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailForgot.setError(getResources().getString(R.string.emailInvalid));
            etEmailForgot.requestFocus();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.sendingEmail));
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    progressDialog.hide();
                    etEmailForgot.setText("");
                    finish();
                    startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                    Toast.makeText(ForgotActivity.this, getResources().getString(R.string.emailSuccess), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.hide();
                    etEmailForgot.setText("");
                    firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            boolean check = !task.getResult().getProviders().isEmpty();
                            if(!check) {
                                Toast.makeText(ForgotActivity.this, getResources().getString(R.string.userDoesntExist), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotActivity.this, getResources().getString(R.string.emailFailed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
