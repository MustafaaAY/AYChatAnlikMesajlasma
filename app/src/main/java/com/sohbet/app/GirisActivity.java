package com.sohbet.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class GirisActivity extends AppCompatActivity {

    MaterialEditText email, sifre;
    Button btn_giris;

    FirebaseAuth auth;
    TextView unutulan_sifre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Giriş");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//Oturum açık olup olmadığını anlamak için auth değişkenine değer döndürüyoruz
        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        sifre = findViewById(R.id.sifre);
        btn_giris = findViewById(R.id.btn_giris);
        unutulan_sifre = findViewById(R.id.unutulan_sifre);

        unutulan_sifre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GirisActivity.this, SifreSifirlamaActivity.class));
            }
        });
////Kullanıcı Adı ve Şifre alanlarının girilip girilimediği kontrol ediliyor
        btn_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_sifre = sifre.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_sifre)){
                    //Tüm alanların kontrolü
                    Toast.makeText(GirisActivity.this, "Tüm alanların doldurulması gereklidir!", Toast.LENGTH_SHORT).show();
                } else {

                    auth.signInWithEmailAndPassword(txt_email, txt_sifre)
                            //// addOnCompleteListener ile işlemin başarılı olup olmadığını kontrol ediyoruz
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        //Bağlantı başarılı olursa MainActivity sayfasına yönlendiriyor
                                        Intent intent = new Intent(GirisActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //Eğer boş alan bıraktıysa ya da veritabanında mevcut değilse hatayı yazdırıyoruz
                                        Toast.makeText(GirisActivity.this, "Kimlik Doğrulama Hatası!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
