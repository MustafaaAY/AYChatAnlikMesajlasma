package com.sohbet.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sohbet.app.Adaptor.MesajAdaptor;
import com.sohbet.app.Fragmanlar.APIServisArayuz;
import com.sohbet.app.Modelleme.Sohbet;
import com.sohbet.app.Modelleme.User;
import com.sohbet.app.Bildirimler.Alici;
import com.sohbet.app.Bildirimler.Data;
import com.sohbet.app.Bildirimler.Cevabim;
import com.sohbet.app.Bildirimler.Gonderici;
import com.sohbet.app.Bildirimler.BelirtecToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesajActivity extends AppCompatActivity {

    CircleImageView profil_image;
    TextView kullaniciadi;
     FirebaseUser fkullanici;
    DatabaseReference referans;
    ImageButton btn_gonder;
    EditText text_gonder;
    MesajAdaptor mesajAdaptor;
    List<Sohbet> mesajchat;
    RecyclerView recyclerView;
    Intent intent;
    ValueEventListener gorulmeyiDinleme;
    String kullaniciid;
    APIServisArayuz apiServisArayuz;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MesajActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiServisArayuz = Alici.getClient("https://fcm.googleapis.com/").create(APIServisArayuz.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profil_image = findViewById(R.id.profil_image);
        kullaniciadi = findViewById(R.id.kullaniciadi);
        btn_gonder = findViewById(R.id.btn_gonder);
        text_gonder = findViewById(R.id.text_gonder);

        intent = getIntent();
        kullaniciid = intent.getStringExtra("kullaniciid");
        fkullanici = FirebaseAuth.getInstance().getCurrentUser();
//gonder butonuna tıklandıgında mesajın gonderılmesı ve bos mesaj gonderıldıgınde uyarı vermesi kontrolü
        btn_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_gonder.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fkullanici.getUid(), kullaniciid, msg);
                } else {
                    Toast.makeText(MesajActivity.this, "Boş mesaj gönderemezsiniz!", Toast.LENGTH_SHORT).show();
                }
                text_gonder.setText("");
            }
        });


        referans = FirebaseDatabase.getInstance().getReference("Users").child(kullaniciid);

        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                kullaniciadi.setText(user.getKullaniciadi());
                if (user.getImageURL().equals("default")){
                    profil_image.setImageResource(R.mipmap.ic_launcher);
                } else {

                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profil_image);
                }

                readMesagges(fkullanici.getUid(), kullaniciid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(kullaniciid);
    }

    private void seenMessage(final String kullaniciid){
        referans = FirebaseDatabase.getInstance().getReference("Chats");
        gorulmeyiDinleme = referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sohbet sohbet = snapshot.getValue(Sohbet.class);
                    if (sohbet.getAlici().equals(fkullanici.getUid()) && sohbet.getGonderen().equals(kullaniciid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("gorulme", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String gonderen, final String alici, String mesaj){

        DatabaseReference referans = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gonderen", gonderen);
        hashMap.put("alici", alici);
        hashMap.put("mesaj", mesaj);
        hashMap.put("gorulme", false);

        referans.child("Chats").push().setValue(hashMap);


        // Sohbet fragmanina kullanici ekleme
         final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Sohbetlistesi")
                .child(fkullanici.getUid())
                .child(kullaniciid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(kullaniciid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Sohbetlistesi")
                .child(kullaniciid)
                .child(fkullanici.getUid());
        chatRefReceiver.child("id").setValue(fkullanici.getUid());

        final String msg = mesaj;

        referans = FirebaseDatabase.getInstance().getReference("Users").child(fkullanici.getUid());
        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(alici, user.getKullaniciadi(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String alici, final String kullaniciadi, final String mesaj){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(alici);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BelirtecToken belirtecToken = snapshot.getValue(BelirtecToken.class);
                    Data data = new Data(fkullanici.getUid(), R.drawable.ic_bildirim_ikon, kullaniciadi+": "+mesaj, "Yeni Mesaj",
                           kullaniciid);
                    Gonderici gonderici = new Gonderici(data, belirtecToken.getBelirtectoken());
                    apiServisArayuz.sendNotification(gonderici)
                            .enqueue(new Callback<Cevabim>() {
                                @Override
                                public void onResponse(Call<Cevabim> call, Response<Cevabim> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(MesajActivity.this, "Hata!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<Cevabim> call, Throwable t) {
                                }
                            });} }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String kullaniciid, final String imageurl){
        mesajchat = new ArrayList<>();

        referans = FirebaseDatabase.getInstance().getReference("Chats");
        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mesajchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sohbet sohbet = snapshot.getValue(Sohbet.class);
                    if (sohbet.getAlici().equals(myid) && sohbet.getGonderen().equals(kullaniciid) ||
                            sohbet.getAlici().equals(kullaniciid) && sohbet.getGonderen().equals(myid)){
                        mesajchat.add(sohbet);
                    }

                    mesajAdaptor = new MesajAdaptor(MesajActivity.this, mesajchat, imageurl);
                    recyclerView.setAdapter(mesajAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String kullaniciid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", kullaniciid);
        editor.apply();
    }

    private void durum(String durum){
        referans = FirebaseDatabase.getInstance().getReference("Users").child(fkullanici.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("durum", durum);

        referans.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        durum("Çevrimiçi");
        currentUser(kullaniciid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        referans.removeEventListener(gorulmeyiDinleme);
        durum("Çevrimdışı");
        currentUser("none");
    }
}
