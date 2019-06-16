package com.shoppingplus.shoppingplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class SeznamArtiklovActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final String TAG = "SeznamArtiklovActivity";
    private Button dodajNovArtikel;

    private RecyclerView artikliRecyclerView;
    private ArrayList<Artikel> arrayArtikel = new ArrayList<Artikel>() ;
    private RecyclerViewAdapterArtikel adapter;
    private View mParentLayout;

    String id_kartice;
    //String id_artikla;

    // del za sortiranje ----------------------------------------------------------------------------------------------------------------------------------
    SharedPreferences pref;
    // ----------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seznam_artiklov);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(SeznamArtiklovActivity.this, LoginActivity.class));
                }
            }
        };


        Intent intent = getIntent();
        //id_artikla = intent.getExtras().getString("id_artikla");
        id_kartice = intent.getExtras().getString("id_kartice");

        // sortiranje -----------------------------------------------------------------------------------------------------------------------------------------
        pref = this.getSharedPreferences("artikli_" + id_kartice, MODE_PRIVATE);
        // ----------------------------------------------------------------------------------------------------------------------------------------------------

        dodajNovArtikel = findViewById(R.id.btnDodajArtikel);
        dodajNovArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeznamArtiklovActivity.this, ArtikliActivity.class);
                //intent.putExtra("id_artikla", id_artikla);
                intent.putExtra("id_kartice", id_kartice);
                startActivity(intent);
            }
        });

        mParentLayout = findViewById(android.R.id.content);
        artikliRecyclerView = findViewById(R.id.recyclerview_seznamArtiklov_id);
        //initRecyclerView();
        getSeznamArtiklov();
    }

    private void initRecyclerView(){
        if(adapter == null){
            adapter = new RecyclerViewAdapterArtikel(this, arrayArtikel);
        }

        artikliRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        artikliRecyclerView.setAdapter(adapter);
    }

    private void getSeznamArtiklov() {

        arrayArtikel.clear();

            FirebaseUser user = firebaseAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference notesCollectionRef = db.collection("artikli");
            notesCollectionRef.whereEqualTo("id_kartice", id_kartice).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Artikel artikel = new Artikel(document.get("naziv_artikla").toString(), document.get("kolicina_artikla").toString(), document.get("opis_artikla").toString()/*, document.get("status_artikla").toString(),id_artikla, */, id_kartice);
                            artikel.setId_artikla(document.getId());
                            arrayArtikel.add(artikel);
                        }

                        String mSortSettings = pref.getString("Uredi", "Od A do Ž");
                        if (mSortSettings.equals("Od A do Ž")) {
                            Collections.sort(arrayArtikel, Artikel.PO_NASLOVU_ASCENDING);

                        } else if (mSortSettings.equals("Od Ž do A")) {
                            Collections.sort(arrayArtikel, Artikel.PO_NASLOVU_DESCENDING);
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        makeSnackBarMessage("Query Failed. Check Logs.");
                    }
                }
            });

            initRecyclerView();

    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(SeznamArtiklovActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }*/

    // sortiranje -------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(SeznamArtiklovActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.sort) {
            prikaziUrejanje();
            return true;
        }
        else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void prikaziUrejanje(){
        String [] izbira = {"Od A do Ž", "Od Ž do A"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Urejanje ");
        builder.setIcon(R.drawable.ic_action_sort);
        builder.setItems(izbira, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){ // Asc je izbran
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Uredi", "Od A do Ž"); // Uredi = ključ , Ascending = vrednost
                    editor.apply();
                    getSeznamArtiklov();
                }
                if(which == 1){ // Desc je izbran
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Uredi", "Od Ž do A");
                    editor.apply();
                    getSeznamArtiklov();
                }
            }
        });
        builder.create().show();

    }


}
