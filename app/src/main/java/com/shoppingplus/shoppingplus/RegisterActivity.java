package com.shoppingplus.shoppingplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmailReg;
    private EditText etPasswordReg;
    private Button btnCreateAccountReg;
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
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        etEmailReg = (EditText) findViewById(R.id.etEmailReg);
        etPasswordReg = (EditText) findViewById(R.id.etPasswordReg);

        btnCreateAccountReg = (Button) findViewById(R.id.btnCreateAccountReg);
        btnCreateAccountReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }
    private void registerUser() {
        String email = etEmailReg.getText().toString().trim();
        String password = etPasswordReg.getText().toString().trim();

        if(email.isEmpty()) {
            etEmailReg.setError(getResources().getString(R.string.emailRequired));
            etEmailReg.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailReg.setError(getResources().getString(R.string.emailInvalid));
            etEmailReg.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            etPasswordReg.setError(getResources().getString(R.string.passwordRequired));
            etPasswordReg.requestFocus();
            return;
        }

        if(password.length()<6) {
            etPasswordReg.setError(getResources().getString(R.string.passwordInvalid));
            etPasswordReg.requestFocus();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressDialog.hide();
                    etEmailReg.setText("");
                    etPasswordReg.setText("");

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.sendEmailVerification();

                    finish();
                    startActivity(new Intent(RegisterActivity.this, KarticeActivity.class));
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationSuccess), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.hide();
                    etEmailReg.setText("");
                    etPasswordReg.setText("");
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.userExists), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationFailed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
