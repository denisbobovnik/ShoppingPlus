package com.shoppingplus.shoppingplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class KarticeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "KarticeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FloatingActionButton fabDodajKartico;
    private RecyclerView karticeRV;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Kartica> arrayKartica = new ArrayList<>() ;
    private RecyclerViewAdapter adapter;
    private DocumentSnapshot mLastQueriedDocument;
    private View mParentLayout;
    private SharedPreferences pref;

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

        pref = this.getSharedPreferences("karticeSort", MODE_PRIVATE);

        fabDodajKartico = findViewById(R.id.fabDodajKartico);
        fabDodajKartico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KarticeActivity.this, DopolnitevKarticeActivity.class);
                startActivity(intent);
            }
        });

        mParentLayout = findViewById(android.R.id.content);
        karticeRV = findViewById(R.id.recyclerview_kartice_id);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerView();
        getSeznamKartic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(KarticeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.sort) {
            prikaziUrejanje();
            return true;
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void prikaziUrejanje(){
        String [] izbira = {"Od A do Ž", "Od Ž do A"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sortiraj");
        builder.setIcon(R.drawable.ic_action_sort);
        builder.setItems(izbira, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){ // Asc je izbran
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Uredi", "Od A do Ž"); // Uredi = ključ , Ascending = vrednost
                    editor.apply();
                    getSeznamKartic();
                }
                if(which == 1){ // Desc je izbran
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Uredi", "Od Ž do A");
                    editor.apply();
                    getSeznamKartic();
                }
            }
        });
        builder.create().show();
    }

    private void initRecyclerView(){
        if(adapter == null){
            adapter = new RecyclerViewAdapter(this, arrayKartica);
        }
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
                        Kartica kartica = new Kartica(document.get("id_uporabnika").toString(), document.get("naziv_trgovine").toString(), document.get("sifra_kartice").toString(), document.get("url_slike").toString(), document.get("tip_sifre").toString());
                        kartica.setId_kartice(document.getId());
                        arrayKartica.add(kartica);
                    }

                    String mSortSettings = pref.getString("Uredi", "Od A do Ž");
                    if (mSortSettings.equals("Od A do Ž")) {
                        Collections.sort(arrayKartica, Kartica.PO_NAZIVU_ASCENDING);

                    } else if (mSortSettings.equals("Od Ž do A")) {
                        Collections.sort(arrayKartica, Kartica.PO_NAZIVU_DESCENDING);
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
}