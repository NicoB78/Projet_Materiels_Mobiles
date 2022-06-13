package fr.epf.min1.beque.projetmobilestationvelib.api

import retrofit2.http.GET

interface VelibPositionService {
    @GET("station_information.json")
    //fun getStations(): Call<List<Station>>
    suspend fun getStations(): GetStationsResult
}

data class GetStationsResult (val lastUpdatedOther: Int, val ttl: Int, val data : Stations)
data class Stations(val stations : List<Carac>)
data class Carac(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val stationCode: String,
)