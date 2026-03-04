package com.vj.sampleaiassistant.di

import com.vj.sampleaiassistant.data.database.AndroidDatabaseDriverFactory
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
}