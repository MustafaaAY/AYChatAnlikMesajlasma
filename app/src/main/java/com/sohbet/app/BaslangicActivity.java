package com.sohbet.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaslangicActivity extends AppCompatActivity {

    Button giris, kayit;

    FirebaseUser firebaseKullanici;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();

        //Eger kullanici kaydi bossa kontrol et
        if (firebaseKullanici != null){
            Intent intent = new Intent(BaslangicActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baslangic);



        giris = findViewById(R.id.giris);
        kayit = findViewById(R.id.kayit);

        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaslangicActivity.this, GirisActivity.class));
            }
        });

        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaslangicActivity.this, KayitActivity.class));
            }
        });
    }
}
