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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.ProviderQueryResult;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLog;
    private EditText etPasswordLog;
    private Button btnLoginLog;
    private TextView tvCreateAccLog;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView tvForgotPassword;

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
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        etEmailLog = (EditText) findViewById(R.id.etEmailLog);
        etPasswordLog = (EditText) findViewById(R.id.etPasswordLog);

        tvCreateAccLog = (TextView) findViewById(R.id.tvCreateAccLog);
        tvCreateAccLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(LoginActivity.this, ForgotActivity.class);
                LoginActivity.this.startActivity(forgotIntent);
            }
        });

        btnLoginLog = (Button) findViewById(R.id.btnLoginLog);
        btnLoginLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void userLogin() {
        final String email = etEmailLog.getText().toString().trim();
        final String password = etPasswordLog.getText().toString().trim();

        if(email.isEmpty()) {
            etEmailLog.setError(getResources().getString(R.string.emailRequired));
            etEmailLog.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailLog.setError(getResources().getString(R.string.emailInvalid));
            etEmailLog.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            etPasswordLog.setError(getResources().getString(R.string.passwordRequired));
            etPasswordLog.requestFocus();
            return;
        }

        if(password.length()<6) {
            etPasswordLog.setError(getResources().getString(R.string.passwordInvalid));
            etPasswordLog.requestFocus();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.loggingIn));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    finish();
                    progressDialog.hide();
                    etEmailLog.setText("");
                    etPasswordLog.setText("");
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, KarticeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //da ne ob kliku na nazaj pride nazaj na prijavo (uporabnik)
                    startActivity(intent);
                } else {
                    progressDialog.hide();
                    etEmailLog.setText("");
                    etPasswordLog.setText("");
                    firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            boolean check = !task.getResult().getProviders().isEmpty();
                            if(!check) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.userDoesntExist), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
