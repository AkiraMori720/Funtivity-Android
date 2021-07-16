package com.brainyapps.funtivity.http;

import com.brainyapps.funtivity.model.NotificationModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @Headers({"Authorization: key=AAAAfoaJ3wk:APA91bH9EK9uwyXrFaGjxu09s-WJIkEt5l26yaQKqOaomDhKLhWvdefpDLsVg4AEaJxOd1c-76wq4aLharmvy8oG2UaEqptb64Vr3yiXsvggizwhz7ryctVPSApObfzi9KOGJHT_PUz5",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseModel> sendPushNotification(@Body NotificationModel.RequestNotificaton requestNotificaton);
}
