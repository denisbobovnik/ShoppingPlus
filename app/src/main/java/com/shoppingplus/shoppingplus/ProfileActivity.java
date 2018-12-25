package com.shoppingplus.shoppingplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvVerified;
    private Button btnDeleteAccount;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button btnChangeEmail;
    private Button btnChangePassword;
    private TextView emailValue;
    private EditText passwordOldValue;
    private EditText passwordNewValue;
    private EditText passwordNewValueRepeat;

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

        progressDialog = new ProgressDialog(this);

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

        btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        passwordOldValue = (EditText) findViewById(R.id.passwordOldValue);
        passwordNewValue = (EditText) findViewById(R.id.passwordNewValue);
        passwordNewValueRepeat = (EditText) findViewById(R.id.passwordNewValueRepeat);


        emailValue = (TextView) findViewById(R.id.emailValue);
        emailValue.setText(user.getEmail());
    }

    private void changeEmail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = emailValue.getText().toString().trim();

        if(email.isEmpty()) {
            emailValue.setError(getResources().getString(R.string.emailRequired));
            emailValue.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailValue.setError(getResources().getString(R.string.emailInvalid));
            emailValue.requestFocus();
            return;
        }

        if(email.equals(user.getEmail())) {
            emailValue.setError(getResources().getString(R.string.emailIsSame));
            emailValue.requestFocus();
            return;
        }

        firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                boolean check = !task.getResult().getProviders().isEmpty();
                if(check) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.userExists), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        progressDialog.setMessage(getResources().getString(R.string.changingEmail));
        progressDialog.show();

        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.hide();
                if(task.isSuccessful()) {
                    tvVerified.setText(getResources().getString(R.string.emailNotVerified));
                    user.sendEmailVerification();
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.changeEmailSuccess), Toast.LENGTH_SHORT).show();
                } else {
                    emailValue.setText(user.getEmail()); //če ni šlo, povrne vrednost v edittext
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.changeEmailFailed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword() {

    }

    private void deleteAccount() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
        dialog.setTitle(getResources().getString(R.string.youSure));
        dialog.setMessage(getResources().getString(R.string.deleteMessage));
        dialog.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage(getResources().getString(R.string.deletingAccount));
                progressDialog.show();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.hide();
                        if(task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.deleteSuccess), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.deleteFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = dialog.create();
        ad.show();
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
