package com.sohbet.app.Fragmanlar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sohbet.app.Adaptor.UserAdapter;
import com.sohbet.app.Modelleme.Sohbetlistesi;
import com.sohbet.app.Modelleme.User;
import com.sohbet.app.Bildirimler.BelirtecToken;
import com.sohbet.app.R;

import java.util.ArrayList;
import java.util.List;


public class SohbetlerFragmani extends Fragment {
    private RecyclerView recyclerView;

    private UserAdapter kullaniciAdaptor;
    private List<User> mesajKullanicilar;
    FirebaseUser fkullanici;
    DatabaseReference referans;
    private List<Sohbetlistesi> kullanicilarListesi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragman_sohbetler, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fkullanici = FirebaseAuth.getInstance().getCurrentUser();
        kullanicilarListesi = new ArrayList<>();
        referans = FirebaseDatabase.getInstance().getReference("Sohbetlistesi").child(fkullanici.getUid());
        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kullanicilarListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sohbetlistesi sohbetlistesi = snapshot.getValue(Sohbetlistesi.class);
                    kullanicilarListesi.add(sohbetlistesi);
                }
                chatList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }
    private void updateToken(String belirtectoken){
        DatabaseReference referans = FirebaseDatabase.getInstance().getReference("Tokens");
        BelirtecToken belirtecToken1 = new BelirtecToken(belirtectoken);
        referans.child(fkullanici.getUid()).setValue(belirtecToken1);
    }
    private void chatList() {
        mesajKullanicilar = new ArrayList<>();
        referans = FirebaseDatabase.getInstance().getReference("Users");
        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mesajKullanicilar.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Sohbetlistesi sohbetlistesi : kullanicilarListesi){
                        if (user.getId().equals(sohbetlistesi.getId())){
                            mesajKullanicilar.add(user);
                        }
                    }
                }
                kullaniciAdaptor = new UserAdapter(getContext(), mesajKullanicilar, true);
                recyclerView.setAdapter(kullaniciAdaptor);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
