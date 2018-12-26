package com.shoppingplus.shoppingplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLog;
    private EditText etPasswordLog;
    private Button btnLoginLog;
    private TextView tvCreateAccLog;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView tvForgotPassword;
    private SignInButton sibGoogle;
    LoginButton loginButton;
    private final static int RC_SIGN_IN = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private CallbackManager mCallbackManager;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(LoginActivity.this, KarticeActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.ivFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginCancelled), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
            }
        });

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

        sibGoogle = (SignInButton) findViewById(R.id.sibGoogle);
        sibGoogle.setSize(SignInButton.SIZE_STANDARD);
        sibGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    //Google prijava
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuth.fetchProvidersForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(check) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.userExists), Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseAuthWithGoogle(account);
                        }
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.googleAuthError), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //email in geslo prijava
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
