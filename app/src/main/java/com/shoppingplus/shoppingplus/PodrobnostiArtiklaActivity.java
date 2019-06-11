package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class PodrobnostiArtiklaActivity extends AppCompatActivity {

    private TextView nazivArtikla;
    private TextView kolicinaArtikla;
    private TextView opisArtikla;
    private RadioGroup kosaricaZArtiklom;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podrobnosti_artikla);

        nazivArtikla = (TextView) findViewById(R.id.nazivArtikla_prikaz);
        kolicinaArtikla = (TextView) findViewById(R.id.kolicinaArtikla_prikaz);
        opisArtikla = (TextView) findViewById(R.id.dodatenOpisArtikla_prikaz);
        kosaricaZArtiklom = (RadioGroup) findViewById(R.id.radioButton_kosaricaZArtiklom_prikaz);

        // Recieve data
        Intent intent = getIntent();
        String Naziv = intent.getExtras().getString("Naziv_artikla");
        String Kolicina = intent.getExtras().getString("Kolicina_artikla");
        String Opis = intent.getExtras().getString("Opis_artikla");
        String Status = intent.getExtras().getString("Status_artikla");
        //final String id_artikla = intent.getExtras().getString("id_artikla");
        final String id_kartice = intent.getExtras().getString("id_kartice");

        firebaseAuth = FirebaseAuth.getInstance();

        // gumb, ki preusmeri nazaj na prejšno aktivnost - Seznam artiklov
        Button gumbSeznamArtiklov = findViewById(R.id.btnNazaj);
        gumbSeznamArtiklov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        if(item.getItemId()==R.id.editProfile) {
            Intent intent = new Intent(PodrobnostiArtiklaActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

}
