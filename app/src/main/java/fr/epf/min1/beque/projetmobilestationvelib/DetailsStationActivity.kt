package fr.epf.min1.beque.projetmobilestationvelib

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailsStationActivity : AppCompatActivity() {


    private var stationRCV: InfoStations? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_station)
        stationRCV = intent.getSerializableExtra("Info_station") as InfoStations?
        //val message = intent.getStringExtra(EXTRA_MESSAGE)
        val nameToFill = findViewById<TextView>(R.id.name_fill_tv).apply {
            text = (stationRCV?.name ?: String) as CharSequence?
        }
        val nbBikeToFill = findViewById<TextView>(R.id.nb_bike_fill_tv).apply {
            text = stationRCV?.numBikesAvailable.toString()
        }
    }

    //Afficher le menu souhaité sur le détail de nos stations, celui qui permet d'ajouter la station en favori
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_fav_stations, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.view_favoris_action -> {
                val station_id: Long = (stationRCV?.station_id ?: Long) as Long
                val name: String = (stationRCV?.name ?: String) as String
                val numBikesAvailable: Int = (stationRCV?.numBikesAvailable ?: Int) as Int
                val stationRCVfav = InfoStationsFav(station_id, name, numBikesAvailable)
                //val stationFav: InfoStationsFav = stationRCV
                val intent = Intent(this, ViewFavorisActivity::class.java).apply {
                    putExtra("Info_station", stationRCVfav)
                }
                startActivity(intent)
            }
            R.id.home_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}