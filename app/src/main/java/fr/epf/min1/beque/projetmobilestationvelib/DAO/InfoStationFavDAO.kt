package fr.epf.min1.beque.projetmobilestationvelib.DAO

import androidx.room.*
import fr.epf.min1.beque.projetmobilestationvelib.InfoStationsFav

@Dao
interface InfoStationDAO {
    @Query("SELECT * FROM InfoStationsFav")
    fun getAll(): MutableList<InfoStationsFav>

    @Query("UPDATE InfoStationsFav SET numBikesAvailable=(:numBikesAvailable) WHERE station_id=(:station_id)")
    fun update(station_id: Long, numBikesAvailable: Int)

    @Query("SELECT * FROM InfoStationsFav WHERE name=(:name)")
    fun getFav(name: String): Boolean

    @Insert
    fun insertFav(infoStationsFav: InfoStationsFav)

    @Delete
    fun delete(infoStation: InfoStationsFav)
}

