package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class SeznamArtiklovActivity extends AppCompatActivity /*implements SwipeRefreshLayout.OnRefreshListener */{

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final String TAG = "SeznamArtiklovActivity";
    private Button dodajNovArtikel;

    private RecyclerView artikliRecyclerView;
    private ArrayList<Artikel> arrayArtikel = new ArrayList<Artikel>() ;
    private RecyclerViewAdapterArtikel adapter;
    private View mParentLayout;
    //private SwipeRefreshLayout mSwipeRefreshLayout;

    String id_kartice;
    //String id_artikla;

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


  //      mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout2);
   //     mSwipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();
        //id_artikla = intent.getExtras().getString("id_artikla");
        id_kartice = intent.getExtras().getString("id_kartice");

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
        initRecyclerView();
        getSeznamArtiklov();

    }

    private void initRecyclerView(){
        if(adapter == null){
            adapter = new RecyclerViewAdapterArtikel(this, arrayArtikel);
        }

        artikliRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        artikliRecyclerView.setAdapter(adapter);
    }

    private void getSeznamArtiklov(){

        arrayArtikel.clear();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("artikli");
        notesCollectionRef.whereEqualTo("id_kartice", id_kartice).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int steviloArtiklov  = task.getResult().size(); //
                    //System.out.print(steviloArtiklov);

                    //Log.d(TAG, "Uspesno ste pridobili artikle");
                    for(QueryDocumentSnapshot document: task.getResult()){
                        //Log.d(TAG, document.getId() + ", " + document.get("id_kartice"));  ???
                        Artikel artikel = new Artikel(document.get("naziv_artikla").toString(), document.get("kolicina_artikla").toString(), document.get("opis_artikla").toString(), document.get("status_artikla").toString()/*,id_artikla, */,id_kartice);
                        //artikel.setId_artikla(document.getId());
                        arrayArtikel.add(artikel);
                    }

                    adapter.notifyDataSetChanged();
                }
                else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(SeznamArtiklovActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onRefresh() {
        getSeznamArtiklov();
        mSwipeRefreshLayout.setRefreshing(false);
    }*/
}
