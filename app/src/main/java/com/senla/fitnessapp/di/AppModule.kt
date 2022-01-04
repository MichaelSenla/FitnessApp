package com.senla.fitnessapp.di

import android.content.Context
import android.content.SharedPreferences
import com.senla.fitnessapp.common.Constants.BASE_URL
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApi(): RetrofitService {
        val loggerInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggerInterceptor)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient.build())
            .build()
            .create(RetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun provideCompositeDisposable() = CompositeDisposable()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSQLiteRepository(@ApplicationContext context: Context): SQLiteRepository =
        SQLiteRepository(SQLiteHelper(context))

    @Singleton
    @Provides
    fun provideSQLite(@ApplicationContext context: Context) = SQLiteHelper(context)
}