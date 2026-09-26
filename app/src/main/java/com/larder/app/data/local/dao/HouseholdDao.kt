package com.larder.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.larder.app.domain.model.Household
import com.larder.app.domain.model.HouseholdMember
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseholdDao {

    @Query("SELECT * FROM households WHERE id = :id")
    suspend fun getHouseholdById(id: String): Household?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHousehold(household: Household)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: HouseholdMember)

    @Query("SELECT * FROM household_members WHERE householdId = :householdId")
    fun getMembers(householdId: String): Flow<List<HouseholdMember>>

    @Query("DELETE FROM households WHERE id = :householdId")
    suspend fun deleteHousehold(householdId: String)
}
