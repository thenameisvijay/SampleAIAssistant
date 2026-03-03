package com.vj.sampleaiassistant.data.local.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}