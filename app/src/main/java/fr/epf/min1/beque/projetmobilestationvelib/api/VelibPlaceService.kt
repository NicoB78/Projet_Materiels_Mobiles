package fr.epf.min1.beque.projetmobilestationvelib.api

import retrofit2.http.GET

interface VelibPlaceService {
    @GET("station_status.json")
    //fun getStations(): Call<List<Station>>
    suspend fun getStations(): GetStationsPlaceResult
}
data class GetStationsPlaceResult(val lastUpdatedOther: Int, val ttl: Int, val data : StationsPlace)
data class StationsPlace(val stations : List<Place>)
data class Place(
    val station_id: Long,
    val numBikesAvailable: Int
)