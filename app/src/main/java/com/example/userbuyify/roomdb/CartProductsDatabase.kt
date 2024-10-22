package com.example.userbuyify.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CartProductTable::class], version = 2, exportSchema = false)
abstract class CartProductsDatabase : RoomDatabase(){

    abstract fun cartProductsDao() : CartProductDao

    companion object{

        @Volatile
        var INSTANCE : CartProductsDatabase ?= null

        fun getDatabaseInstance(context: Context) : CartProductsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val roomDb = Room.databaseBuilder(context, CartProductsDatabase::class.java, "CartProductsDatabase")
                    .allowMainThreadQueries() // Consider removing this for better performance
                    .fallbackToDestructiveMigration() // This will drop and recreate the database if version changes
                    .build()
                INSTANCE = roomDb
                return roomDb
            }
        }

    }
}