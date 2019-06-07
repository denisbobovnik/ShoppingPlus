package com.shoppingplus.shoppingplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

public class PodrobnostiKarticeActivity extends AppCompatActivity {

    private ImageView imgSlika;
    private ImageView imgKoda; //za pridobitev tipa sifre
    private TextView tvSifra;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podrobnosti_kartice);

        imgSlika = (ImageView) findViewById(R.id.id_SlikaPodrobnostiKartice);
        imgKoda = (ImageView) findViewById(R.id.id_SlikaSifrePodrobnostiKartice); //slika od kode, sifre
        tvSifra = (TextView) findViewById(R.id.id_tvSifraPodrobnostiKartice);

        // Recieve data
        Intent intent = getIntent();
        String Slika = intent.getExtras().getString("Slika") ;
        String Koda = intent.getExtras().getString("Tip_sifre") ; //tip sifre
        String Sifra = intent.getExtras().getString("Sifra");

        // Setting values
        Picasso.get()
                .load(Slika)
                .into(imgSlika);

        tvSifra.setText(Sifra);

        firebaseAuth = FirebaseAuth.getInstance();

        //****************************************
        //za gumb ki preusmeri na seznam artiklov
        Button gumbSeznamArtiklov = findViewById(R.id.btnSeznamArtiklov);
        gumbSeznamArtiklov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PodrobnostiKarticeActivity.this, SeznamArtiklovActivity.class);
                startActivity(intent);
            }
        });
        //za gumb ki preusmeri na zemljevid
        Button gumbZemljevid = findViewById(R.id.btnZemljevid);
        gumbZemljevid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GoogleMapsSearch.class);
                startActivity(intent);
            }
        });
        //****************************************






        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(Sifra, pretvoriFormat(encodeFormat(Koda)),100,100);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imgKoda.setImageBitmap(bitmap);

        } catch (WriterException e) {

            e.printStackTrace();

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
            Intent intent = new Intent(PodrobnostiKarticeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else { //logout
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public int encodeFormat(String format) {
        switch (format) {
            case "CODE_128":
                return Barcode.CODE_128;
            case "CODE_39":
                return Barcode.CODE_39;
            case "CODE_93":
                return Barcode.CODE_93;
            case "CODABAR":
                return Barcode.CODABAR;
            case "DATA_MATRIX":
                return Barcode.DATA_MATRIX;
            case "EAN_13":
                return Barcode.EAN_13;
            case "EAN_8":
                return Barcode.EAN_8;
            case "ITF":
                return Barcode.ITF;
            case "QR_CODE":
                return Barcode.QR_CODE;
            case "UPC_A":
                return Barcode.UPC_A;
            case "UPC_E":
                return Barcode.UPC_E;
            case "PDF417":
                return Barcode.PDF417;
            case "AZTEC":
                return Barcode.AZTEC;
            default:
                return Barcode.QR_CODE;
        }
    }

    public BarcodeFormat pretvoriFormat(int vhodni) {
        switch (vhodni) {
            case Barcode.CODE_128:
                return BarcodeFormat.CODE_128;
            case Barcode.CODE_39:
                return BarcodeFormat.CODE_39;
            case Barcode.CODE_93:
                return BarcodeFormat.CODE_93;
            case Barcode.CODABAR:
                return BarcodeFormat.CODABAR;
            case Barcode.DATA_MATRIX:
                return BarcodeFormat.DATA_MATRIX;
            case Barcode.EAN_13:
                return BarcodeFormat.EAN_13;
            case Barcode.EAN_8:
                return BarcodeFormat.EAN_8;
            case Barcode.ITF:
                return BarcodeFormat.ITF;
            case Barcode.QR_CODE:
                return BarcodeFormat.QR_CODE;
            case Barcode.UPC_A:
                return BarcodeFormat.UPC_A;
            case Barcode.UPC_E:
                return BarcodeFormat.UPC_E;
            case Barcode.PDF417:
                return BarcodeFormat.PDF_417;
            case Barcode.AZTEC:
                return BarcodeFormat.AZTEC;
            default:
                return BarcodeFormat.QR_CODE;
        }
    }
}
