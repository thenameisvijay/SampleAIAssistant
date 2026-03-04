package com.vj.sampleaiassistant.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.vj.AIChatDatabase
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory

/**
 * Created by Vijay on 03/03/2026.
 * https://github.com/thenameisvijay
 */
class IOSDatabaseDriverFactory: DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = NativeSqliteDriver(
            AIChatDatabase.Schema, "chat.db"
        )
}