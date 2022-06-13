package fr.epf.min1.beque.projetmobilestationvelib.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min1.beque.projetmobilestationvelib.DetailsStationActivityAlreadyFav
import fr.epf.min1.beque.projetmobilestationvelib.InfoStationsFav
import fr.epf.min1.beque.projetmobilestationvelib.R

class StationAdapter(val infoStations: /*Mutable*/List<InfoStationsFav>) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {
    class StationViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val stationView = inflater.inflate(R.layout.adapter_station, parent, false)
        return StationViewHolder(stationView)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = infoStations[position]

        holder.itemView.setOnClickListener(){
            val context = it.context
            //Intention
            val intent = Intent(context, DetailsStationActivityAlreadyFav::class.java)
            intent.putExtra("Info_station", station)
            context.startActivity(intent)
        }

        val stationTextView = holder.itemView.findViewById<TextView>(R.id.adapter_station_tv)
        stationTextView.text = station.name

        val stationImageView =
            holder.itemView.findViewById<ImageView>(R.id.adapter_station_imageview)
        stationImageView.setImageResource(R.drawable.ic_baseline_directions_bike_24)
    }

    override fun getItemCount() = infoStations.size
}