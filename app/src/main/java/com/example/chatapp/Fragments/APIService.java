package com.example.chatapp.Fragments;

import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(

            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAArKgSH1U:APA91bEPpTcEMy59zvPpzUJHSDxNCnnTtceEihuHEbodl5nlAKjU6JvVbQwaaIcm6d3OzaRPYGuXzTCnRrImuriwFFyjkSY_Dufe8agbcxbKnAngnxM9MucZa5pvH7rnUIriN59oiDKB"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
