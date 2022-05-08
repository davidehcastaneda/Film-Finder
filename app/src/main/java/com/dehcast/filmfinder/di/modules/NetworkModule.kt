package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.BuildConfig
import com.dehcast.filmfinder.apis.MovieDetailsApi
import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

const val loggingInterceptorAlias = "loggingInterceptor"
const val apiKeyInterceptorAlias = "apiKeyInterceptor"

@Module
class NetworkModule {

    //TODO get api key from remote config
    private val apiKey = "api_key"
    private val apiKeyValue = "7924ab0ac150aca39b037103e81ad773"

    @Singleton
    @Provides
    fun provideInterceptorLevel(): HttpLoggingInterceptor.Level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

    @Singleton
    @Provides
    @Named(loggingInterceptorAlias)
    fun provideLoggingInterceptor(loggingLevel: HttpLoggingInterceptor.Level): Interceptor =
        HttpLoggingInterceptor().setLevel(loggingLevel)

    @Singleton
    @Provides
    @Named(apiKeyInterceptorAlias)
    fun provideKeyInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val url = request.url.newBuilder().addQueryParameter(apiKey, apiKeyValue).build()
        chain.proceed(request.newBuilder().url(url).build())
    }

    @Singleton
    @Provides
    fun provideOkHttpBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        builder: OkHttpClient.Builder,
        @Named(loggingInterceptorAlias) apiLoggingInterceptor: Interceptor,
        @Named(apiKeyInterceptorAlias) apiKeyInterceptor: Interceptor,
    ): OkHttpClient =
        builder.addInterceptor(apiKeyInterceptor).addInterceptor(apiLoggingInterceptor).build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        builder: Retrofit.Builder,
        converter: Converter.Factory,
        client: OkHttpClient,
    ): Retrofit =
        builder.baseUrl(BuildConfig.API_URL).addConverterFactory(converter).client(client).build()

    @Provides
    fun provideMovieDetailsApi(retrofit: Retrofit): MovieDetailsApi =
        retrofit.create(MovieDetailsApi::class.java)

    @Provides
    fun provideMovieDiscoveryApi(retrofit: Retrofit): MovieDiscoveryApi =
        retrofit.create(MovieDiscoveryApi::class.java)

    @Provides
    fun provideMovieThumbnailConfigurationApi(retrofit: Retrofit): MovieThumbnailConfigurationApi =
        retrofit.create(MovieThumbnailConfigurationApi::class.java)
}