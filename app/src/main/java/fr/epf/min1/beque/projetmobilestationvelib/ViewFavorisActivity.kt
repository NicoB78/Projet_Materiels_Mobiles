package fr.epf.min1.beque.projetmobilestationvelib

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import fr.epf.min1.beque.projetmobilestationvelib.DAO.AppDatabase
import fr.epf.min1.beque.projetmobilestationvelib.model.StationAdapter

class ViewFavorisActivity : AppCompatActivity() {

    private var stationAdapter : StationAdapter? = null
    val stationsFav: MutableList<InfoStationsFav> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_favoris)

        val stationRCV = intent.getSerializableExtra("Info_station") as InfoStationsFav

        /*val nameToFill = findViewById<TextView>(R.id.name_fill_tv).apply {
            text = stationRCV
        }*/

        //val truc: MutableList<InfoStationsFav> = dataBase(stationRCV as InfoStationsFav) as MutableList<InfoStationsFav>
        if (stationRCV != null && stationRCV.station_id >= 0) {
            stationsFav.add(stationRCV as InfoStationsFav)
            dataBaseAdd(stationRCV)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_stations_favoris_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dataInfoStationsFav: MutableList<InfoStationsFav> = dataBase(stationRCV as InfoStationsFav)
        stationAdapter?.notifyDataSetChanged()
        stationAdapter = StationAdapter(dataInfoStationsFav)
        recyclerView.adapter = stationAdapter

    }

    private fun dataBase(stationRCV: InfoStationsFav): MutableList<InfoStationsFav> {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        val favs: MutableList<InfoStationsFav> = db.infoStationDao().getAll() as MutableList<InfoStationsFav>
        return favs
    }

    private fun dataBaseAdd(stationRCV: InfoStationsFav) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        db.infoStationDao().insertFav(stationRCV)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_fav_stations, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.home_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}