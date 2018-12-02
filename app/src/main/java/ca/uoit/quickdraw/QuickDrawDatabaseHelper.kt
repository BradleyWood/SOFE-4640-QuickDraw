package ca.uoit.quickdraw

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class QuickDrawDatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {

    companion object {
        private var instance: QuickDrawDatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): QuickDrawDatabaseHelper {
            if (instance == null)
                instance = QuickDrawDatabaseHelper(ctx.applicationContext)

            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            "drawings", true,
            "object" to TEXT,
            "guess" to TEXT,
            "time" to INTEGER,
            "strokes" to TEXT,
            "width" to INTEGER,
            "height" to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}

val Context.database: QuickDrawDatabaseHelper
    get() = QuickDrawDatabaseHelper.getInstance(getApplicationContext())
