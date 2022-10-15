package com.sohbet.app.Fragmanlar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sohbet.app.Adaptor.UserAdapter;
import com.sohbet.app.Modelleme.User;
import com.sohbet.app.R;

import java.util.ArrayList;
import java.util.List;

public class KullanicilarFragmani extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter kullaniciAdaptor;
    private List<User> mesajKullanicilar;
    EditText kullanicilari_ara;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragman_kullanicilar, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mesajKullanicilar = new ArrayList<>();
        readUsers();
        kullanicilari_ara = view.findViewById(R.id.kullanicilari_ara);
        kullanicilari_ara.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;    }

    private void searchUsers(String s) {
        final FirebaseUser fkullanici = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("ara")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mesajKullanicilar.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fkullanici != null;
                    if (!user.getId().equals(fkullanici.getUid())){
                        mesajKullanicilar.add(user);
                    }
                }
                kullaniciAdaptor = new UserAdapter(getContext(), mesajKullanicilar, false);
                recyclerView.setAdapter(kullaniciAdaptor);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readUsers() {
        final FirebaseUser firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referans = FirebaseDatabase.getInstance().getReference("Users");//Users

        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (kullanicilari_ara.getText().toString().equals("")) {
                    mesajKullanicilar.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getId().equals(firebaseKullanici.getUid())) {
                            mesajKullanicilar.add(user);
                        }
                    }
                    kullaniciAdaptor = new UserAdapter(getContext(), mesajKullanicilar, false);
                    recyclerView.setAdapter(kullaniciAdaptor);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
