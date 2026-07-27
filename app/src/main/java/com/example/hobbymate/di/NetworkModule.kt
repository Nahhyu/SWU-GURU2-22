package com.example.hobbymate.di

import com.example.hobbymate.BuildConfig
import com.example.hobbymate.data.remote.analysis.VideoAnalysisApi
import com.example.hobbymate.data.remote.youtube.YouTubeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(75, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
        )
        .build()

    @Provides
    @Singleton
    fun provideYouTubeApi(client: OkHttpClient): YouTubeApi =
        retrofit("https://www.googleapis.com/", client).create(YouTubeApi::class.java)

    @Provides
    @Singleton
    fun provideVideoAnalysisApi(client: OkHttpClient): VideoAnalysisApi =
        retrofit(BuildConfig.OPENAI_PROXY_BASE_URL, client)
            .create(VideoAnalysisApi::class.java)

    private fun retrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
