package com.agromall.clockin.di

import com.agromall.clockin.data.source.remote.ApiService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { provideHeaderInterceptor() }
    single { provideGson() }
    single { httpLoggingInterceptor() }

    single {
        provideOkhttpClient(get(), get())
    }

    single {
        provideUserApiService(get(), get())
    }

}


internal fun provideHeaderInterceptor(): Interceptor {

    return Interceptor { chain ->
        val request = chain.request()
        val newRequest: Request

        newRequest = request.newBuilder()
            //.addHeader("Accept", "application/vnd.github.v3+json")
            .build()
        chain.proceed(newRequest)
    }
}

internal fun provideOkhttpClient(interceptor: Interceptor,
                                 httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {


    val builder = OkHttpClient.Builder()
    builder.connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor(httpLoggingInterceptor)
    return builder.build()
}

internal fun provideGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    return gsonBuilder.create()
}

internal fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Timber.d(message) }
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
}

internal fun provideUserApiService(okHttpClient: OkHttpClient, gson: Gson): ApiService {
    val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
    //retrofitBuilder.baseUrl("http://private-516e2-kingsleyjohnnwankwo.apiary-mock.com/")
    retrofitBuilder.baseUrl("https://office.theagromall.com/api/")
    retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    retrofitBuilder.client(okHttpClient)
    retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson))
    return retrofitBuilder.build().create(ApiService::class.java)
}