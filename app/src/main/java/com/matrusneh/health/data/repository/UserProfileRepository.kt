package com.matrusneh.health.data.repository

import com.matrusneh.health.data.db.dao.UserProfileDao
import com.matrusneh.health.data.db.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getProfile(): Flow<UserProfileEntity?> = userProfileDao.getProfile()

    suspend fun saveProfile(gestationalWeek: Int, name: String = "") {
        userProfileDao.upsert(
            UserProfileEntity(
                gestationalWeek = gestationalWeek,
                name = name
            )
        )
    }

    suspend fun profileExists(): Boolean = userProfileDao.exists() > 0
}
