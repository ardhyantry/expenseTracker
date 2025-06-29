package com.bebas.expensetracker.model

import androidx.room.*
import com.bebas.expensetracker.model.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): User?

    @Update
    fun update(user: User)
}
