package com.suplz.adidas.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.suplz.adidas.data.local.CalendarReader
import com.suplz.adidas.data.remote.api.GisGlobalRoutingApi
import com.suplz.adidas.data.remote.api.GisPublicTransportApi
import com.suplz.adidas.data.remote.api.TimelineApi
import com.suplz.adidas.data.repository.CalendarRepositoryImpl
import com.suplz.adidas.data.repository.DirectionsRepositoryImpl
import com.suplz.adidas.data.repository.TimelineRepositoryImpl
import com.suplz.adidas.domain.repository.CalendarRepository
import com.suplz.adidas.domain.repository.DirectionsRepository
import com.suplz.adidas.domain.repository.TimelineRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.time.Duration
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindCalendarRepository(
        impl: CalendarRepositoryImpl
    ): CalendarRepository

    @Binds
    @Singleton
    fun bindTimelineRepository(
        impl: TimelineRepositoryImpl
    ): TimelineRepository

    @Binds
    @Singleton
    fun bindDirectionsRepository(impl: DirectionsRepositoryImpl): DirectionsRepository

    companion object {

        // ВАЖНО: URL должен заканчиваться на "/"
        private const val BASE_URL = "http://10.101.118.158:8352/"
        private const val GIS_ROUTING_BASE_URL = "https://routing.api.2gis.com/"
        private const val GIS_MASSTRANSIT_BASE_URL = "https://routing.api.2gis.com/"

        // --- NETWORK PROVIDERS ---

        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(
            json: Json
        ): Converter.Factory {
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            // Этот Interceptor будет логировать все детали запросов и ответов в Logcat.
            // Уровень BODY показывает заголовки и тело.
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @Provides
        @Singleton
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            // Устанавливаем таймауты. 60 секунд - хороший старт для медленного бэка.
            val timeout = Duration.ofSeconds(60)

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Подключаем логгер
                // Устанавливаем увеличенные таймауты для всех операций
                .connectTimeout(timeout) // Таймаут на подключение
                .readTimeout(timeout)    // Таймаут на чтение ответа
                .writeTimeout(timeout)   // Таймаут на отправку запроса
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            converterFactory: Converter.Factory,
            okHttpClient: OkHttpClient // <-- Hilt предоставит клиент, созданный выше
        ): Retrofit {
            // Этот Retrofit-клиент предназначен для вашего бэкенда
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // <-- Используем наш настроенный клиент
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun provideTimelineApi(
            retrofit: Retrofit // <-- Hilt предоставит Retrofit-клиент, созданный выше
        ): TimelineApi {
            return retrofit.create()
        }

        // --- LOCAL DATA SOURCE PROVIDERS ---

        @Provides
        @Singleton
        fun provideCalendarReader(
            @ApplicationContext context: Context
        ): CalendarReader {
            return CalendarReader(context)
        }

        // --- GIS API PROVIDERS ---
        // Эти провайдеры остаются без изменений, так как API 2GIS быстрое
        // и не требует специальных таймаутов.

        @Provides @Singleton @Named("GisRoutingRetrofit")
        fun provideGisRoutingRetrofit(factory: Converter.Factory): Retrofit {
            return Retrofit.Builder().baseUrl(GIS_ROUTING_BASE_URL).addConverterFactory(factory).build()
        }

        @Provides @Singleton @Named("GisMasstransitRetrofit")
        fun provideGisMasstransitRetrofit(factory: Converter.Factory): Retrofit {
            return Retrofit.Builder().baseUrl(GIS_MASSTRANSIT_BASE_URL).addConverterFactory(factory).build()
        }

        @Provides @Singleton
        fun provideGisGlobalRoutingApi(@Named("GisRoutingRetrofit") retrofit: Retrofit): GisGlobalRoutingApi {
            return retrofit.create(GisGlobalRoutingApi::class.java)
        }

        @Provides @Singleton
        fun provideGisPublicTransportApi(@Named("GisMasstransitRetrofit") retrofit: Retrofit): GisPublicTransportApi {
            return retrofit.create(GisPublicTransportApi::class.java)
        }
    }
}