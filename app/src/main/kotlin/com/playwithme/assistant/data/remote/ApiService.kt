package com.playwithme.assistant.data.remote

import com.playwithme.assistant.data.remote.dto.HeroRemoteDto
import retrofit2.http.GET

/**
 * Retrofit API service interface.
 *
 * Base URL is configured in [NetworkModule].
 * All endpoints are suspend functions for coroutine support.
 */
interface ApiService {

    /** Fetch all heroes from the remote server. */
    @GET("heroes")
    suspend fun getAllHeroes(): List<HeroRemoteDto>

    /** Fetch a single hero by ID. */
    @GET("heroes/{id}")
    suspend fun getHeroById(@retrofit2.http.Path("id") id: String): HeroRemoteDto
}
