package fr.epf.min1.beque.projetmobilestationvelib

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fr.epf.min1.beque.projetmobilestationvelib.DAO.AppDatabase

class DetailsStationActivityAlreadyFav : AppCompatActivity() {

    private var stationRCV: InfoStationsFav? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_station)
        stationRCV = intent.getSerializableExtra("Info_station") as InfoStationsFav?
        //val message = intent.getStringExtra(EXTRA_MESSAGE)
        val nameToFill = findViewById<TextView>(R.id.name_fill_tv).apply {
            text = (stationRCV?.name ?: String) as CharSequence?
        }
        val nbBikeToFill = findViewById<TextView>(R.id.nb_bike_fill_tv).apply {
            text = stationRCV?.numBikesAvailable.toString()
        }
    }

    //Afficher le menu souhaité sur le détail de nos stations, celui qui ne permet pas d'ajouter la station en favori
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.already_fav_stations, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.home_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.del_fav_action -> {
                stationRCV?.let { dataBase(it) }
                val intent = Intent(this, ViewFavorisActivity::class.java).apply {
                    putExtra("Info_station", InfoStationsFav(-1,"",0))
                }
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dataBase(stationRCV: InfoStationsFav) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        db.infoStationDao().delete(stationRCV)
    }
}