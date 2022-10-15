package com.sohbet.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohbet.app.Fragmanlar.SohbetlerFragmani;
import com.sohbet.app.Fragmanlar.ProfilFragmani;
import com.sohbet.app.Fragmanlar.KullanicilarFragmani;
import com.sohbet.app.Modelleme.Sohbet;
import com.sohbet.app.Modelleme.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
/*FirebaseAuth kütüphanesi ile;

        Email ve Şifre kullanarak kullanıcı oluşturma,
        Email ve Şifre kullanarak giris olma (yetkilendirme),
        Oluşturulan kullanıcının Profil bilgilerini güncelleme ve kullanma

        FirebaseDatabase kütüphanesi ile;

        Veri tabanından veri okuma/ekleme/güncelleme/silme işlemleri,
        Veri tabanındaki değişiklikleri dinleme*/

/*Google Servisleri ile daha bütünleşmiş bir yapıya kavuşan bu yeni Firebase platformunu emulator üzerinde denemek istiyorsak,
 o emülator’de Google Servislerininde kurulu olması gerekmektedir.  (Uygulamayı telefonda denemek isteyenler için böyle
 bir durum söz konusu değil, çünkü telefonlarda bu servisler zaten yüklü  durumdadır.)*/

public class MainActivity extends AppCompatActivity {

    CircleImageView profil_image;
    TextView kullaniciadi;

    FirebaseUser firebaseKullanici;
    DatabaseReference referans;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profil_image = findViewById(R.id.profil_image);
        kullaniciadi = findViewById(R.id.kullaniciadi);

        firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();
        referans = FirebaseDatabase.getInstance().getReference("Users").child(firebaseKullanici.getUid());

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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        referans = FirebaseDatabase.getInstance().getReference("Chats");
        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
               //Okunmayan mesaj sayisi
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sohbet sohbet = snapshot.getValue(Sohbet.class);
                    if (sohbet.getAlici().equals(firebaseKullanici.getUid()) && !sohbet.isGorulme()){
                        unread++;
                    }
                }

                if (unread == 0){
                    viewPagerAdapter.addFragment(new SohbetlerFragmani(), "Sohbetler");
                } else {
                    viewPagerAdapter.addFragment(new SohbetlerFragmani(), "("+unread+") Sohbetler");
                }

                viewPagerAdapter.addFragment(new KullanicilarFragmani(), "Kullanıcılar");
                viewPagerAdapter.addFragment(new ProfilFragmani(), "Profil");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.cikis:
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(MainActivity.this, BaslangicActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }

        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }



    //Cevrimici Cevrimdisi kontrolu
    private void durum(String durum){
        referans = FirebaseDatabase.getInstance().getReference("Users").child(firebaseKullanici.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("durum", durum);

        referans.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        durum("Çevrimiçi");
    }

    @Override
    protected void onPause() {
        super.onPause();
        durum("Çevrimdışı");
    }
}
