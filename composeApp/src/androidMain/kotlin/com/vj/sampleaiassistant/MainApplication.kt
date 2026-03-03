package com.vj.sampleaiassistant

import android.app.Application
import com.vj.sampleaiassistant.data.database.AndroidDatabaseDriverFactory
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory
import com.vj.sampleaiassistant.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                module {
                    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
                }
            )
        }
    }
}