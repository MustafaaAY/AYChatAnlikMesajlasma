package com.sohbet.app.Adaptor;

import android.content.Context;
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
import com.sohbet.app.Modelleme.Sohbet;
import com.sohbet.app.R;

import java.util.List;

public class MesajAdaptor extends RecyclerView.Adapter<MesajAdaptor.ViewHolder> {

    public static  final int MESAJ_SOLA_YAZ = 0;
    public static  final int MESAJ_SAGA_YAZ = 1;

    private Context mesajBaglami;
    private List<Sohbet> mesajSohbet;
    private String imageurl;

    FirebaseUser fkullanici;

    public MesajAdaptor(Context mesajBaglami, List<Sohbet> mesajSohbet, String imageurl){
        this.mesajSohbet = mesajSohbet;
        this.mesajBaglami = mesajBaglami;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MesajAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESAJ_SAGA_YAZ) {
            View view = LayoutInflater.from(mesajBaglami).inflate(R.layout.sohbet_item_sag, parent, false);
            return new MesajAdaptor.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mesajBaglami).inflate(R.layout.sohbet_item_sol, parent, false);
            return new MesajAdaptor.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MesajAdaptor.ViewHolder holder, int position) {

        Sohbet sohbet = mesajSohbet.get(position);

        holder.mesaj_goster.setText(sohbet.getMesaj());

        if (imageurl.equals("default")){
            holder.profil_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mesajBaglami).load(imageurl).into(holder.profil_image);
        }

        if (position == mesajSohbet.size()-1){
            if (sohbet.isGorulme()){
                holder.txt_iletildi.setText("Görüldü");
            } else {
                holder.txt_iletildi.setText("İletildi");
            }
        } else {
            holder.txt_iletildi.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mesajSohbet.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mesaj_goster;
        public ImageView profil_image;
        public TextView txt_iletildi;

        public ViewHolder(View itemView) {
            super(itemView);

            mesaj_goster = itemView.findViewById(R.id.mesaj_goster);
            profil_image = itemView.findViewById(R.id.profil_image);
            txt_iletildi = itemView.findViewById(R.id.txt_iletildi);
        }
    }
//Gonderen ya da alicini hangi tarafta mesaja yazacağını kontrol eder.
    @Override
    public int getItemViewType(int position) {
        fkullanici = FirebaseAuth.getInstance().getCurrentUser();
        if (mesajSohbet.get(position).getGonderen().equals(fkullanici.getUid())){
            return MESAJ_SAGA_YAZ;
        } else {
            return MESAJ_SOLA_YAZ;
        }
    }
}