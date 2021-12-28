package com.olamachia.maptrackerweekeighttask.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var firstInstance: Retrofit? = null
    val instance: Retrofit

        // create a retrofit instance to call the end point and retrieve information
        // set the default converter
        // build the retrofit object
        get() {
            if (firstInstance == null)
                firstInstance = Retrofit.Builder()
                    .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build() // get a Retrofit object that can be reuseable

            return firstInstance!!
        }
}