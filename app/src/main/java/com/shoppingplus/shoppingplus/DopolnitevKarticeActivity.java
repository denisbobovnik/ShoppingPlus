package com.shoppingplus.shoppingplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DopolnitevKarticeActivity extends AppCompatActivity implements IPickResult, AdapterView.OnItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private StorageReference storageRef;
    private EditText etStevilkaKartice;
    private EditText etImeTrgovine;
    private Button btnDodajKartico;
    private ProgressDialog progressDialog;
    private ImageView ivNewScan, ivSlikaKartice;
    private boolean slikaNalozena;
    private Spinner spinStaticStores;
    private List<StaticnaTrgovina> staticneTrgovine;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dopolnitev_kartice);

        slikaNalozena = false;

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("kartice");

        progressDialog = new ProgressDialog(this);

        spinStaticStores = (Spinner) findViewById(R.id.spinStaticStores);
        nafilajSpinner();

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

        ivSlikaKartice = (ImageView)  findViewById(R.id.ivSlikaKartice);
        ivSlikaKartice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickSetup ps = new PickSetup()
                        .setTitle(getResources().getString(R.string.chooseSource))
                        .setCameraButtonText(getResources().getString(R.string.kamera))
                        .setGalleryButtonText(getResources().getString(R.string.galerija))
                        .setCancelText(getResources().getString(R.string.preklici));
                PickImageDialog.build(ps).show(DopolnitevKarticeActivity.this);
            }
        });

        Intent intent = new Intent(DopolnitevKarticeActivity.this, ScanActivity.class);
        startActivityForResult(intent, 0);
    }

    private void nafilajSpinner() {
        db.collection("staticne_trgovine")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<StaticnaTrgovina> trgovine = new ArrayList<StaticnaTrgovina>();
                            for (QueryDocumentSnapshot document : task.getResult())
                                trgovine.add(new StaticnaTrgovina((String) document.get("naziv_trgovine"), (String) document.getString("url_slike")));

                            String[] imenaTrgovin = new String[trgovine.size()];
                            for(int i=0; i<trgovine.size(); i++)
                                imenaTrgovin[i] = trgovine.get(i).getNaziv_trgovine();

                            nastaviGlobalno(trgovine);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DopolnitevKarticeActivity.this, android.R.layout.simple_spinner_item, imenaTrgovin);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinStaticStores.setAdapter(adapter);
                            spinStaticStores.setOnItemSelectedListener(DopolnitevKarticeActivity.this);
                        } else {
                            Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.staticNotFound), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void nastaviGlobalno(List<StaticnaTrgovina> trgovine) {
        staticneTrgovine = new ArrayList<StaticnaTrgovina>();
        for(StaticnaTrgovina s : trgovine)
            staticneTrgovine.add(s);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        slikaNalozena = true;

        if(staticneTrgovine.get(position).getNaziv_trgovine().equals("(po meri)"))
            etImeTrgovine.setText("");
        else
            etImeTrgovine.setText(staticneTrgovine.get(position).getNaziv_trgovine());

        StorageReference httpsReference = storage.getReferenceFromUrl(staticneTrgovine.get(position).getUrl_slike());
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivSlikaKartice.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.staticPhotoLoadFailure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void dodajKartico() {
        final String stevilka_kartice = etStevilkaKartice.getText().toString().trim();
        final String ime_trgovine = etImeTrgovine.getText().toString().trim();

        //potrditev veljavnosti vnosov
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

        //potrditev pravilnosti vnosov
        AlertDialog.Builder dialog = new AlertDialog.Builder(DopolnitevKarticeActivity.this);
        dialog.setTitle(getResources().getString(R.string.validityCheck));
        dialog.setMessage(getResources().getString(R.string.validityDetails));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                ivNewScan.setEnabled(false);
                btnDodajKartico.setEnabled(false);
                progressDialog.setMessage(getResources().getString(R.string.addingCard));
                progressDialog.show();

                final Kartica k = new Kartica(user.getUid(), ime_trgovine, stevilka_kartice, "https://firebasestorage.googleapis.com/v0/b/shoppingplus-5a575.appspot.com/o/default_slike%2Fdefault_card.png?alt=media&token=1084fdf6-1a49-42bf-82f4-0cad94d425c7");

                if(slikaNalozena) { //gre za nalaganje slike
                    ivSlikaKartice.setDrawingCacheEnabled(true);
                    ivSlikaKartice.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) ivSlikaKartice.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    String path = UUID.randomUUID().toString();
                    final StorageReference karticaReference = storageRef.child(path);

                    UploadTask uploadTask = karticaReference.putBytes(data);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return karticaReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                Kartica kartica = new Kartica(k);
                                kartica.setUrl_slike(downloadUri.toString());

                                CollectionReference kartice = db.collection("kartice");
                                kartice.add(kartica).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        etStevilkaKartice.setText("");
                                        etImeTrgovine.setText("");
                                        ivNewScan.setEnabled(true);
                                        btnDodajKartico.setEnabled(true);
                                        progressDialog.hide();
                                        startActivity(new Intent(DopolnitevKarticeActivity.this, KarticeActivity.class));
                                        slikaNalozena = false;
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
                                        slikaNalozena = false;
                                        Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.cardNotAdded), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.cardNotAdded), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else { //gre za default tip nalaganja
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
                            slikaNalozena = false;
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
                            slikaNalozena = false;
                            Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.cardNotAdded), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            ivSlikaKartice.setImageBitmap(compressImageToMax(r.getBitmap(), 1000000));
            slikaNalozena = true;
        } else {
            Toast.makeText(DopolnitevKarticeActivity.this, getResources().getString(R.string.chooseFailed), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap compressImageToMax(Bitmap image, int maxBytes){
        int oldSize = image.getByteCount();
        while (image != null && image.getByteCount() > maxBytes){
            image = Bitmap.createScaledBitmap(image, image.getWidth() / 2, image.getHeight() / 2, false);
            oldSize = image.getByteCount();
        }
        return image;
    }
}