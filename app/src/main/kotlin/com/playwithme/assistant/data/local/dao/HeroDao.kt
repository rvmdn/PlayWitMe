package com.playwithme.assistant.data.local.dao

import androidx.room.*
import com.playwithme.assistant.data.local.entity.HeroEntity
import com.playwithme.assistant.domain.model.HeroRole
import kotlinx.coroutines.flow.Flow

/** DAO for Hero database operations. */
@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes ORDER BY name ASC")
    fun getAllHeroes(): Flow<List<HeroEntity>>

    @Query("SELECT * FROM heroes WHERE id = :id")
    suspend fun getHeroById(id: String): HeroEntity?

    @Query("SELECT * FROM heroes WHERE name LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchHeroes(query: String): Flow<List<HeroEntity>>

    @Query("SELECT * FROM heroes WHERE roles LIKE '%' || :role || '%' ORDER BY name ASC")
    fun getHeroesByRole(role: String): Flow<List<HeroEntity>>

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun getHeroCount(): Int

    @Query("SELECT * FROM heroes WHERE id LIKE 'scanned_%' ORDER BY name ASC")
    fun getScannedHeroes(): Flow<List<HeroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHero(hero: HeroEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroes(heroes: List<HeroEntity>)

    @Update
    suspend fun updateHero(hero: HeroEntity)

    @Delete
    suspend fun deleteHero(hero: HeroEntity)

    @Query("DELETE FROM heroes")
    suspend fun deleteAllHeroes()
}
