package com.sohbet.app.Fragmanlar;

import com.sohbet.app.Bildirimler.Cevabim;
import com.sohbet.app.Bildirimler.Gonderici;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
//Bildirımleri göndermek için API Servis oluşturulumuştur.Firebase arayüzünden projeye uygun  anahtar  yapıştırılmıştır.
public interface APIServisArayuz {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqKSwClg:APA91bF2Xrh9X70P9EY7J54CYYEMcbtLIE9YbdnuQWgfyKFVI8Q3Z2qQ5pKCRncNjKupwumfsiihU7nP9vKLjawp0_99_eK2Nvdv8V_qxtrNp0iC52MMDPiU4_W2o9lZKU6UJceUDdBR"
            }
    )

    @POST("fcm/send")
    Call<Cevabim> sendNotification(@Body Gonderici body);
}
