package io.github.arseniy.libraryapp.data.remote


import io.github.arseniy.libraryapp.data.remote.dto.OpenLibrarySearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {

    @GET("search.json")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponseDto
}
