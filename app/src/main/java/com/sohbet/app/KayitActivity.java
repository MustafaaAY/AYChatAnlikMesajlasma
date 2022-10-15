package com.sohbet.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class KayitActivity extends AppCompatActivity {

    MaterialEditText kullaniciadi, email, sifre;
    Button btn_kayit;

    FirebaseAuth auth;
    DatabaseReference referans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kayıt Ol");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        kullaniciadi = findViewById(R.id.kullaniciadi);
        email = findViewById(R.id.email);
        sifre = findViewById(R.id.sifre);
        btn_kayit = findViewById(R.id.btn_kayit);
//Firabase kimlik doğrulama refaransını oluşturuyoruz
        auth = FirebaseAuth.getInstance();
////Kullanıcı Adı ve Şifre alanlarının girilip girilimediği kontrol ediliyor
        //kayıt ol butonuna tıklanınca yapılması gerekenler
        btn_kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_kullaniciadi = kullaniciadi.getText().toString();
                String txt_email = email.getText().toString();
                String txt_sifre = sifre.getText().toString();

                if (TextUtils.isEmpty(txt_kullaniciadi) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_sifre)){
                    Toast.makeText(KayitActivity.this, "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show();
                } else if (txt_sifre.length() < 6 ){
                    Toast.makeText(KayitActivity.this, "Şifre en az 6 karakterden oluşmalıdır!", Toast.LENGTH_SHORT).show();
                } else {
                    kayit(txt_kullaniciadi, txt_email, txt_sifre);
                }
            }
        });
    }

    private void kayit(final String kullaniciadi, String email, String sifre){

        auth.createUserWithEmailAndPassword(email, sifre)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Oturum açık olup olmadığını anlamak için firebaseKullanici değişkenine değer döndürüyoruz
                            FirebaseUser firebaseKullanici = auth.getCurrentUser();
                            assert firebaseKullanici != null;
                            String kullaniciid = firebaseKullanici.getUid();

                            referans = FirebaseDatabase.getInstance().getReference("Users").child(kullaniciid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", kullaniciid);
                            hashMap.put("kullaniciadi", kullaniciadi);
                            hashMap.put("imageURL", "default");
                            hashMap.put("durum", "Çevrimdışı");
                            hashMap.put("ara", kullaniciadi.toLowerCase());

                            referans.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //  //Kayıt başarılı olursa main activity  sayfasına gidiyoruz
                                        Intent intent = new Intent(KayitActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        Toast.makeText(KayitActivity.this, "Başarılı şekilde kaydoldunuz", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(KayitActivity.this, MainActivity.class));
                                    }
                                }
                            });
                        } else {
                            //// Eğer Hata ile karşılaşırsak hata mesajını bastırıyoruz
                            Toast.makeText(KayitActivity.this, "Bu email ve parola ile kaydolamazsınız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }







}
