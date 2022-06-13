package fr.epf.min1.beque.projetmobilestationvelib.DAO

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.epf.min1.beque.projetmobilestationvelib.InfoStationsFav

@Database(entities = [InfoStationsFav::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoStationDao(): InfoStationDAO
}