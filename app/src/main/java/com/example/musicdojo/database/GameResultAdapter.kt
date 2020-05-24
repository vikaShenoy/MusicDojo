package com.example.musicdojo.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicdojo.model.Game
import com.example.musicdojo.model.GameResult
import java.lang.ref.WeakReference

class GameResultAdapter(val ctx: Context) {
    var gameResults: MutableList<GameResult> = ArrayList<GameResult>()
    var database: GameResultDatabase? = null

    set (value) {
        field = value
        value?.let {
            LoadGameResultsTask(it, this).execute()
        }
    }
    init {
        LoadDatabaseTask(this).execute()
    }

    fun insert(gameResult: GameResult) {
        if (database != null) {
            gameResults.add(gameResult)
            NewGameResultTask(database!!, gameResult).execute()
        }
    }
}

class LoadDatabaseTask(adapter: GameResultAdapter) : AsyncTask<Unit, Unit, GameResultDatabase>() {
    private val adapter = WeakReference(adapter)

    override fun doInBackground(vararg p0: Unit?): GameResultDatabase? {
        var database: GameResultDatabase? = null
        adapter.get()?.let {
            database = Room.databaseBuilder(
                it.ctx.applicationContext,
                GameResultDatabase::class.java,
                "game_results"
            ).build()
        }
        return database
    }

    override fun onPostExecute(database: GameResultDatabase?) {
        adapter.get()?.let {
            it.database = database
        }
    }

}

class NewGameResultTask(
    private val database: GameResultDatabase,
    private val gameResult: GameResult
) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg p0: Unit?) {
        gameResult.id = database.gameResultDao().insert(gameResult)
    }
}

class LoadGameResultsTask(
    private val database: GameResultDatabase,
    private val adapter: GameResultAdapter
) : AsyncTask<Unit, Unit, MutableList<GameResult>>() {
    override fun doInBackground(vararg p0: Unit?): MutableList<GameResult> {
        return database.gameResultDao().getAll()
    }

    override fun onPostExecute(result: MutableList<GameResult>) {
        adapter.gameResults = result
    }

}