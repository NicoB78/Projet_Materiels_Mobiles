package fr.epf.min1.beque.projetmobilestationvelib

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.min1.beque.projetmobilestationvelib.DAO.AppDatabase
import fr.epf.min1.beque.projetmobilestationvelib.api.Carac
import fr.epf.min1.beque.projetmobilestationvelib.api.VelibPlaceService
import fr.epf.min1.beque.projetmobilestationvelib.api.VelibPositionService
import fr.epf.min1.beque.projetmobilestationvelib.databinding.ActivityMapsBinding
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.Serializable


data class InfoStations(
    val station_id: Long,
    val name: String,
    var numBikesAvailable: Int) : Serializable

@Entity
data class InfoStationsFav(
    @PrimaryKey val station_id: Long,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "numBikesAvailable") val numBikesAvailable: Int) : Serializable

private const val TAG = "MapsActivity"
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    val stations : MutableList<Carac> = mutableListOf()
    val infoStations : MutableList<InfoStations> = mutableListOf()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //Afficher le menu sur notre activité maps
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_stations, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.view_favoris_action -> {
                val intent = Intent(this, ViewFavorisActivity::class.java).apply {
                    putExtra("Info_station", InfoStationsFav(-1,"",0))
                }
                startActivity(intent)
            }
            R.id.synchro_api_action -> {
                if(checkForInternet(this)) {
                    synchroApi()
                } else {
                    informationSynchroButton()
                    Toast.makeText(this, "Hors connexion", Toast.LENGTH_LONG)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun informationSynchroButton() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.offline_title)
            .setMessage(R.string.offline_message)
            .setPositiveButton(R.string.ok){ _,_ ->
            }.show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(checkForInternet(this)) {
            synchroApi()
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
            //val favs: MutableList<InfoStationsFav> = db.infoStationDao().getAll()
            for (station in infoStations) {
                db.infoStationDao().update(
                    station.station_id,
                    station.numBikesAvailable
                )
            }
        } else {
            Toast.makeText(this, "Hors connexion", Toast.LENGTH_LONG)
        }
        //Zoom initial de l'application sur la premiere station du jeu de données en réalisant un zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(48.8, 2.2), 12f))

        //Set a listener for marker click
        mMap.setOnMarkerClickListener(this)
    }

    private fun synchroApi() {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val station = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(station)
            .build()

        val service = retrofit.create(VelibPositionService::class.java)
        val servicePlace = retrofit.create(VelibPlaceService::class.java)

        runBlocking {
            val result = service.getStations()
            val resultPlace = servicePlace.getStations()
            Log.d(TAG, "synchroApi: ${result}")
            val stationsRes = result.data.stations
            val stationsPlaceRes = resultPlace.data.stations
            stationsRes.map {
                stations.add(it)
                val(station_id, name) = it
                InfoStations(station_id, name, 0)


            }
                .map {
                    infoStations.add(it)
                }
            for (i in 0 until infoStations.size) {
                for (j in stationsPlaceRes.indices) {
                    if (infoStations[i].station_id == stationsPlaceRes[j].station_id) {
                        infoStations[i].numBikesAvailable = stationsPlaceRes[j].numBikesAvailable
                    }

                }
            }
            print(infoStations)
        }

        for(station in stations) {
            mMap.addMarker(MarkerOptions().position(LatLng(station.lat, station.lon)).title(station.name))
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        if(marker.title?.let { db.infoStationDao().getFav(it) } == true){
            val intent = Intent(this, DetailsStationActivityAlreadyFav::class.java).apply {
                if (marker != null) {
                    putExtra("Info_station", InfoStationsFav(getInfoStations(marker).station_id, getInfoStations(marker).name, getInfoStations(marker).numBikesAvailable))
                }
            }
            startActivity(intent)
        } else {
            val intent = Intent(this, DetailsStationActivity::class.java).apply {
                if (marker != null) {
                    putExtra("Info_station", getInfoStations(marker))
                }
            }
            startActivity(intent)
        }

        //startActivity(Intent(this, DetailsStationActivity::class.java))
        return false;
    }

    private fun getInfoStations(marker: Marker): InfoStations {
        for (station in infoStations) {
            if (marker.title == station.name) {
                return station
            }
        }
        return InfoStations(0, "Erreur", 0)
    }

    private fun checkForInternet(context : Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}