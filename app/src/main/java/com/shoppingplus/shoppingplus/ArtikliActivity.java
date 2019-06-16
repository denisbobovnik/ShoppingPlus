package com.shoppingplus.shoppingplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ArtikliActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    private EditText nazivArtikla;
    private EditText kolicinaArtikla;
    private EditText opisArtikla;
    /*private RadioGroup kosaricaZArtiklom;
    private RadioButton radioButton_izbira;*/
    private Button btnShraniArtikel;
    //private String id_artikla;
    private String id_kartice;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikli);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("artikli");

        progressDialog = new ProgressDialog(this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(ArtikliActivity.this, LoginActivity.class));
                }
            }
        };

        Intent intent = getIntent();
        //id_artikla = intent.getExtras().getString("id_artikla");
        id_kartice = intent.getExtras().getString("id_kartice");

        nazivArtikla = (EditText) findViewById(R.id.nazivArtikla);
        kolicinaArtikla = (EditText) findViewById(R.id.kolicinaArtikla);
        opisArtikla = (EditText) findViewById(R.id.opisArtikla);

        //kosaricaZArtiklom = (RadioGroup) findViewById(R.id.radioButton_kosaricaZArtiklom);
        btnShraniArtikel = (Button)findViewById(R.id.btnShraniArtikel);
        btnShraniArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int izbira_radioButton = kosaricaZArtiklom.getCheckedRadioButtonId();
                radioButton_izbira = (RadioButton) findViewById(izbira_radioButton);*/
                //Toast.makeText(ArtikliActivity.this, radioButton_izbira.getText().toString(), Toast.LENGTH_SHORT).show();
                dodajArtikel();
            }

            private void dodajArtikel() {

                final String naziv_artikla = nazivArtikla.getText().toString().trim();
                final String kolicina_artikla = kolicinaArtikla.getText().toString().trim();
                final String opis_artikla = opisArtikla.getText().toString().trim();
                //final String status_artikla = radioButton_izbira.getText().toString().trim();

                if (naziv_artikla.isEmpty()) {
                    nazivArtikla.setError("Naziv artikla je zahtevan");
                    nazivArtikla.requestFocus();
                    return;
                }

                if (kolicina_artikla.isEmpty()) {
                    kolicinaArtikla.setError("Količina je zahtevana");
                    kolicinaArtikla.requestFocus();
                    return;
                }

                if (opis_artikla.isEmpty()) {
                    kolicinaArtikla.setError("Opis je zahtevan");
                    kolicinaArtikla.requestFocus();
                    return;
                }

                /*if (status_artikla.isEmpty()) {
                    //kosaricaZArtiklom.setError("Status artikla je zahtevan");
                    kosaricaZArtiklom.requestFocus();
                    return;
                }*/

                AlertDialog.Builder dialog = new AlertDialog.Builder(ArtikliActivity.this);
                dialog.setTitle(getResources().getString(R.string.validityCheck));
                dialog.setMessage(getResources().getString(R.string.validityDetails));
                dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        btnShraniArtikel.setEnabled(false);
                        progressDialog.setMessage("Dodajanje artikla...");
                        progressDialog.show();

                        final Artikel a = new Artikel(naziv_artikla, kolicina_artikla, opis_artikla/*, status_artikla, id_artikla*/, id_kartice);

                        Artikel artikel_1 = new Artikel(a);
                        CollectionReference artikel = db.collection("artikli");
                        artikel.add(artikel_1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                nazivArtikla.setText("");
                                kolicinaArtikla.setText("");
                                opisArtikla.setText("");
                                btnShraniArtikel.setEnabled(true);
                                progressDialog.hide();
                                Intent intent = new Intent(ArtikliActivity.this, SeznamArtiklovActivity.class);
                                //intent.putExtra("id_artikla", id_artikla);
                                intent.putExtra("id_kartice", id_kartice);
                                startActivity(intent);
                                Toast.makeText(ArtikliActivity.this, "Artikel je bil uspešno dodan! ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                nazivArtikla.setText("");
                                kolicinaArtikla.setText("");
                                opisArtikla.setText("");
                                btnShraniArtikel.setEnabled(true);
                                progressDialog.hide();
                                Intent intent = new Intent(ArtikliActivity.this, SeznamArtiklovActivity.class);
                                //intent.putExtra("id_artikla", id_artikla);
                                intent.putExtra("id_kartice", id_kartice);
                                startActivity(intent);
                                Toast.makeText(ArtikliActivity.this, "Artikel ni bil dodan uspešno! ", Toast.LENGTH_SHORT).show();
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
        });
    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu, menu);
                return super.onCreateOptionsMenu(menu);
            }

            @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(ArtikliActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }


}
