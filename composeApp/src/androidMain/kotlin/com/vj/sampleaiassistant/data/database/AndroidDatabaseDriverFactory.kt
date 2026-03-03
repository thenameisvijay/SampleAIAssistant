package com.vj.sampleaiassistant.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.vj.AIChatDatabase
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory

/**
 * Created by Vijay on 03/03/2026.
 * https://github.com/thenameisvijay
 */
class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AIChatDatabase.Schema, context, "chat.db")
    }
}