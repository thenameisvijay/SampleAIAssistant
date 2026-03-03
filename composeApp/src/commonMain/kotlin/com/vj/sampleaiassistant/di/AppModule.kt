package com.vj.sampleaiassistant.di

import com.vj.sampleaiassistant.data.datasource.LocalMessageDataSource
import com.vj.sampleaiassistant.data.remote.AIChatApiService
import com.vj.sampleaiassistant.data.datasource.RemoteMessageDataSource
import com.vj.sampleaiassistant.data.repository.ChatRepositoryImpl
import com.vj.sampleaiassistant.domain.repository.ChatRepository
import com.vj.sampleaiassistant.domain.usecase.ObserveMessagesUseCase
import com.vj.sampleaiassistant.domain.usecase.SendMessageUseCase
import com.vj.sampleaiassistant.presentation.chat.ChatViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
    singleOf(::AIChatApiService)
}

val repositoryModule = module {
    singleOf(::LocalMessageDataSource)
    singleOf(::RemoteMessageDataSource)
    single<ChatRepository> { ChatRepositoryImpl(remoteDataSource = get(), localDataSource = get()) }
}

val useCaseModule = module {
    singleOf(::SendMessageUseCase)
    singleOf(::ObserveMessagesUseCase)
}

val viewModelModule = module {
    factoryOf(::ChatViewModel)
}

val dispatcherModule = module {
    single { Dispatchers.IO }
}

val appModules = listOf(
    dispatcherModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}