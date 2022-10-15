package com.sohbet.app.Bildirimler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseminIdServisi extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseKullanici != null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference referans = FirebaseDatabase.getInstance().getReference("Tokens");
        BelirtecToken belirtecToken = new BelirtecToken(refreshToken);
        referans.child(firebaseKullanici.getUid()).setValue(belirtecToken);
    }
}
