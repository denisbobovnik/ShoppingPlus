package com.shoppingplus.shoppingplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class DopolnitevKarticeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText etStevilkaKartice;
    private EditText etImeTrgovine;
    private Button btnDodajKartico;
    private ProgressDialog progressDialog;
    private ImageView ivNewScan;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dopolnitev_kartice);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(DopolnitevKarticeActivity.this, LoginActivity.class));
                }
            }
        };

        etStevilkaKartice = (EditText) findViewById(R.id.etStevilkaKartice);
        etImeTrgovine = (EditText) findViewById(R.id.etImeTrgovine);

        btnDodajKartico = (Button) findViewById(R.id.btnDodajKartico);
        btnDodajKartico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajKartico();
            }
        });

        ivNewScan = (ImageView) findViewById(R.id.ivNewScan);
        ivNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DopolnitevKarticeActivity.this, ScanActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Intent intent = new Intent(DopolnitevKarticeActivity.this, ScanActivity.class);
        startActivityForResult(intent, 0);
    }

    private void dodajKartico() {
        String stevilka_kartice = etStevilkaKartice.getText().toString().trim();
        String ime_trgovine = etImeTrgovine.getText().toString().trim();

        if(stevilka_kartice.isEmpty()) {
            etStevilkaKartice.setError(getResources().getString(R.string.stevilkaKarticeRequired));
            etStevilkaKartice.requestFocus();
            return;
        }

        if(ime_trgovine.isEmpty()) {
            etImeTrgovine.setError(getResources().getString(R.string.imeTrgovineRequired));
            etImeTrgovine.requestFocus();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        ivNewScan.setEnabled(false);
        btnDodajKartico.setEnabled(false);
        progressDialog.setMessage(getResources().getString(R.string.addingCard));
        progressDialog.show();

        Kartica k = new Kartica(user.getUid(), ime_trgovine, stevilka_kartice);
        k.setUrl_slike("https://firebasestorage.googleapis.com/v0/b/shoppingplus-5a575.appspot.com/o/kartice%2Fdefault_card.png?alt=media&token=5ea99819-8be6-4c61-845b-05879c1646c9");

        if(false) {
            final String url_slike;
            //uporabnik je naložo lastno sliko, naložimo in prepišemo url v kartici
        }

        CollectionReference kartice = db.collection("kartice");
        kartice.add(k).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                etStevilkaKartice.setText("");
                etImeTrgovine.setText("");
                ivNewScan.setEnabled(true);
                btnDodajKartico.setEnabled(true);
                progressDialog.hide();
                startActivity(new Intent(DopolnitevKarticeActivity.this, KarticeActivity.class));
                Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.cardAdded), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                etStevilkaKartice.setText("");
                etImeTrgovine.setText("");
                ivNewScan.setEnabled(true);
                btnDodajKartico.setEnabled(true);
                progressDialog.hide();
                startActivity(new Intent(DopolnitevKarticeActivity.this, KarticeActivity.class));
                Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.cardNotAdded), Toast.LENGTH_SHORT).show();
            }
        });

        /*
        UploadTask uploadTask = reference.putBytes(data, metadata);
        uploadTask.addOnSuccessListener(DopolnitevKarticeActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        k.setUrl_slike(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //progressDialog.hide();

                        etStevilkaKartice.setText("");
                        etImeTrgovine.setText("");

                        Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.didntGetURL), Toast.LENGTH_SHORT).show();

                        finish();

                        Intent intent = new Intent(DopolnitevKarticeActivity.this, KarticeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //da ne ob kliku na nazaj pride nazaj na DopolnitevKarticeActiviry (uporabnik)
                        startActivity(intent);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.hide();

                etStevilkaKartice.setText("");
                etImeTrgovine.setText("");

                Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.photoUploadFailure), Toast.LENGTH_SHORT).show();

                finish();

                Intent intent = new Intent(DopolnitevKarticeActivity.this, KarticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //da ne ob kliku na nazaj pride nazaj na DopolnitevKarticeActiviry (uporabnik)
                startActivity(intent);
            }
        });
        */
        //naredit upload slike, če uporabnik ne uproabi naj naloži na cloud storage default sliko, z urljem skupaj vnese v firebase in zakljuci ter gre nazaj na senzam..tam prikazes slik
        //seznam se mora posodobiti ob vračilu - https://firebase.google.com/docs/database/unity/retrieve-data
        //ce uporabnik ne izbere slike oz nalozi, pol nalozzi default - https://firebase.google.com/docs/storage/android/start
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0) {
            if(resultCode== CommonStatusCodes.SUCCESS) {
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("crtnaKoda");
                    etStevilkaKartice.setText("");
                    etStevilkaKartice.setText(barcode.displayValue);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
            Intent intent = new Intent(DopolnitevKarticeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
