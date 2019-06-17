package com.shoppingplus.shoppingplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
    private EditText etPasswordRepeatReg;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(RegisterActivity.this, KarticeActivity.class));
                }
            }
        };

        etEmailReg = (EditText) findViewById(R.id.etEmailReg);
        etPasswordReg = (EditText) findViewById(R.id.etPasswordReg);
        etPasswordRepeatReg = (EditText) findViewById(R.id.etPasswordRepeatReg);

        btnCreateAccountReg = (Button) findViewById(R.id.btnCreateAccountReg);
        btnCreateAccountReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }
    private void registerUser() {
        final String email = etEmailReg.getText().toString().trim();
        final String password = etPasswordReg.getText().toString().trim();
        final String passwordNewRepeat = etPasswordRepeatReg.getText().toString().trim();

        //potrditev veljavnosti vnosov
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
        if(passwordNewRepeat.isEmpty()) {
            etPasswordRepeatReg.setError(getResources().getString(R.string.passwordRequired));
            etPasswordRepeatReg.requestFocus();
            return;
        }
        if(password.length()<6) {
            etPasswordReg.setError(getResources().getString(R.string.passwordInvalid));
            etPasswordReg.requestFocus();
            return;
        }
        if(passwordNewRepeat.length()<6) {
            etPasswordRepeatReg.setError(getResources().getString(R.string.passwordInvalid));
            etPasswordRepeatReg.requestFocus();
            return;
        }
        if(!password.equals(passwordNewRepeat)) {
            etPasswordRepeatReg.setError(getResources().getString(R.string.passwordMissmatch));
            etPasswordRepeatReg.requestFocus();
            return;
        }

        //potrditev pravilnosti vnosov
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
        dialog.setTitle(getResources().getString(R.string.validityCheck));
        dialog.setMessage(getResources().getString(R.string.validityDetails));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage(getResources().getString(R.string.registering));
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.hide();
                            etEmailReg.setText("");
                            etPasswordReg.setText("");
                            etPasswordRepeatReg.setText("");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.sendEmailVerification();

                            finish();
                            startActivity(new Intent(RegisterActivity.this, KarticeActivity.class));
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationSuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.hide();
                            etEmailReg.setText("");
                            etPasswordReg.setText("");
                            etPasswordRepeatReg.setText("");
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.userExists), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationFailed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = dialog.create();
        ad.show();
    }
}