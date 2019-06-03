package com.shoppingplus.shoppingplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class KarticeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "KarticeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FloatingActionButton fabDodajKartico;

    //PETRA DODALA:************************
    private RecyclerView karticeRV;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Kartica> arrayKartica = new ArrayList<>() ;
    private RecyclerViewAdapter adapter;
    private DocumentSnapshot mLastQueriedDocument;
    private View mParentLayout;
    //*************************************

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartice);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(KarticeActivity.this, LoginActivity.class));
                }
            }
        };

        fabDodajKartico = findViewById(R.id.fabDodajKartico);
        fabDodajKartico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KarticeActivity.this, DopolnitevKarticeActivity.class);
                startActivity(intent);
            }
        });

        //PETRA DODALA***************************
        mParentLayout = findViewById(android.R.id.content);
        karticeRV = findViewById(R.id.recyclerview_kartice_id);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerView();
        getSeznamKartic();
        //*************************************


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(KarticeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    //PETRA DODALA:************************

   /* public void onRefresh() {
        getSeznamKartic();
        mSwipeRefreshLayout.setRefreshing(false);
    }*/

    private void initRecyclerView(){
        if(adapter == null){
            adapter = new RecyclerViewAdapter(this, arrayKartica);
        }
       // karticeRV.setLayoutManager(new LinearLayoutManager(this));
        karticeRV.setLayoutManager(new GridLayoutManager(this, 2));
        karticeRV.setAdapter(adapter);
    }

    private void getSeznamKartic(){

        arrayKartica.clear();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("kartice");

        notesCollectionRef.whereEqualTo("id_uporabnika", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int steviloKartic  = task.getResult().size();
                    System.out.print(steviloKartic);

                    Log.d(TAG, "Uspesno pridobili kartice");
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d(TAG, document.getId() + ", " + document.get("url_slike"));
                         //  document.getId();
                        Kartica kartica = new Kartica(document.get("id_uporabnika").toString(), document.get("naziv_trgovine").toString(), document.get("sifra_kartice").toString(), document.get("url_slike").toString(), document.get("tip_sifre").toString());
                        arrayKartica.add(kartica);
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
    public void onRefresh() {
        getSeznamKartic();
        mSwipeRefreshLayout.setRefreshing(false);
    }
    //**************************************

}