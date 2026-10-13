package com.andina.finanzas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andina.finanzas.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    fun observeActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE archived = 0 AND kind = :kind ORDER BY sortOrder ASC, id ASC")
    fun observeCategoriesByKind(kind: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    suspend fun getActiveCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun observeCategoryById(id: Long): Flow<CategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("UPDATE categories SET archived = 1 WHERE id = :id")
    suspend fun archiveCategory(id: Long)

    @Query("UPDATE categories SET archived = 0 WHERE id = :id")
    suspend fun unarchiveCategory(id: Long)
}
