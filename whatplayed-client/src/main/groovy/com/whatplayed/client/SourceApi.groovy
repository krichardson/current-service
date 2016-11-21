package com.whatplayed.client

import com.whatplayed.api.Source
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SourceApi {

    @GET('/source')
    Call<List<Source>> listSources()

    @GET('/source/{sourceId}')
    Call<Source> getSource(@Path('sourceId') Long sourceId)

}
