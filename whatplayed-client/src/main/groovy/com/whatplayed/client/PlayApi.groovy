package com.whatplayed.client

import com.whatplayed.api.Play
import com.whatplayed.api.PlayRequest
import com.whatplayed.api.PlaylistResponse
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface PlayApi {

    @GET('/source/{sourceId}/plays')
    Call<PlaylistResponse> listPlays(@Path('sourceId') Long sourceId,
                                     @Query('rangeStartTime') LocalDateTime rangeStartTime,
                                     @Query('rangeEndTime') LocalDateTime rangeEndTime)

    @POST('/source/{sourceId}/plays')
    Call<Play> recordPlay(@Path('sourceId') Long sourceId, @Body PlayRequest playRequest)

    @GET('/source/{sourceId}/plays/latest')
    Call<Play> getLatestPlay(@Path('sourceId') Long sourceId)

}