package com.sohbet.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SifreSifirlamaActivity extends AppCompatActivity {
    EditText email_gonder;
    Button btn_sifirla;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifre_sifirlama);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Şifre Sıfırlama");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email_gonder = findViewById(R.id.email_gonder);
        btn_sifirla = findViewById(R.id.btn_sifirla);
        firebaseAuth = FirebaseAuth.getInstance();
        btn_sifirla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_gonder.getText().toString();
                if (email.equals("")){
                    Toast.makeText(SifreSifirlamaActivity.this, "Lütfen Tüm Alanları Doldurunuz!", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SifreSifirlamaActivity.this, "Emailinizi Kontrol Ediniz", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SifreSifirlamaActivity.this, GirisActivity.class));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SifreSifirlamaActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
