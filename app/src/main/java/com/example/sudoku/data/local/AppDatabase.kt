package com.example.sudoku.data.local

// ╔══════════════════════════════════════════════╗
// ║  AppDatabase.kt                              ║
// ║  Banco local (Room) com as tabelas do app    ║
// ║  Uma instância só para o app inteiro         ║
// ╚══════════════════════════════════════════════╝

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [Partida::class, Conquista::class, DesafioEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun partidaDAO(): PartidaDAO

    abstract fun conquistaDAO(): ConquistaDAO

    abstract fun desafioDAO(): DesafioDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val tempInstance = INSTANCE

            if (tempInstance != null) {
                // o banco existe
                return tempInstance
            } else {
                // criar o banco
                synchronized(this) {

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sudoku_database"
                    )
                        // Se a versão mudar, apaga e recria as tabelas (não precisa escrever migração)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}
