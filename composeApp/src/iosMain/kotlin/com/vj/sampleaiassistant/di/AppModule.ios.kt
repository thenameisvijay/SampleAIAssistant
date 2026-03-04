package com.vj.sampleaiassistant.di

import com.vj.sampleaiassistant.data.database.IOSDatabaseDriverFactory
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}
