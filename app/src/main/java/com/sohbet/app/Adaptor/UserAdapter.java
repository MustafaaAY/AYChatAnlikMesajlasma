package com.sohbet.app.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohbet.app.MesajActivity;
import com.sohbet.app.Modelleme.Sohbet;
import com.sohbet.app.Modelleme.User;
import com.sohbet.app.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mesajBaglami;
    private List<User> mesajKullanicilar;
    private boolean sohbette;

    String SonMesaj;

    public UserAdapter(Context mesajBaglami, List<User> mesajKullanicilar, boolean sohbette){
        this.mesajKullanicilar = mesajKullanicilar;
        this.mesajBaglami = mesajBaglami;
        this.sohbette = sohbette;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mesajBaglami).inflate(R.layout.kullanici_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mesajKullanicilar.get(position);
        holder.kullaniciadi.setText(user.getKullaniciadi());
        if (user.getImageURL().equals("default")){
            holder.profil_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mesajBaglami).load(user.getImageURL()).into(holder.profil_image);
        }

        if (sohbette){
            lastMessage(user.getId(), holder.son_mesaj);
        } else {
            holder.son_mesaj.setVisibility(View.GONE);
        }
//Eğer çevrimdışı ise yuvarlak sarı küçük reccylerview görünür olmasın çevrimiçi ise yeşil recyclerrview olsun.
        if (sohbette){
            if (user.getDurum().equals("Çevrimdışı")){
                holder.img_cevrimdisi.setVisibility(View.VISIBLE);
                holder.img_cevrimici.setVisibility(View.GONE);
            } else {
                holder.img_cevrimdisi.setVisibility(View.GONE);
                holder.img_cevrimici.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_cevrimici.setVisibility(View.GONE);
            holder.img_cevrimdisi.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mesajBaglami, MesajActivity.class);
                intent.putExtra("kullaniciid", user.getId());
                mesajBaglami.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesajKullanicilar.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView kullaniciadi;
        public ImageView profil_image;
        private ImageView img_cevrimici;
        private ImageView img_cevrimdisi;
        private TextView son_mesaj;

        public ViewHolder(View itemView) {
            super(itemView);

            kullaniciadi = itemView.findViewById(R.id.kullaniciadi);
            profil_image = itemView.findViewById(R.id.profil_image);
            img_cevrimici = itemView.findViewById(R.id.img_cevrimici);
            img_cevrimdisi = itemView.findViewById(R.id.img_cevrimdisi);
            son_mesaj = itemView.findViewById(R.id.son_mesaj);
        }
    }

    //Son Mesaj Kontrolu
    private void lastMessage(final String kullaniciid, final TextView son_mesaj){
        SonMesaj = "default";
        final FirebaseUser firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referans = FirebaseDatabase.getInstance().getReference("Chats");

        referans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sohbet sohbet = snapshot.getValue(Sohbet.class);
                   if (firebaseKullanici != null && sohbet != null) {
                        if (sohbet.getAlici().equals(firebaseKullanici.getUid()) && sohbet.getGonderen().equals(kullaniciid) ||
                                sohbet.getAlici().equals(kullaniciid) && sohbet.getGonderen().equals(firebaseKullanici.getUid())) {
                            SonMesaj = sohbet.getMesaj();
                        }
                    }
                }

                switch (SonMesaj){
                    case  "default":
                        son_mesaj.setText("Mesaj Yok");
                        break;

                    default:
                        son_mesaj.setText(SonMesaj);
                        break;
                }

                SonMesaj = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
