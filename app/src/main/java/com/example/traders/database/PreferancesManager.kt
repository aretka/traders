package com.example.traders.database

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME_DESC, BY_NAME_ASC, BY_CHANGE_DESC, BY_CHANGE_ASC, DEFAULT }

data class FilterPreferences(val sortOrder: SortOrder, val isFavourite: Boolean)

@Singleton
class PreferancesManager @Inject constructor(@ApplicationContext context: Context) {
    private val datastore = context.createDataStore("user_preferences")

    val preferencesFlow = datastore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.DEFAULT.name
            )
            val isFavourite = preferences[PreferencesKeys.IS_FAVOURITE] ?: false
            FilterPreferences(sortOrder, isFavourite)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        datastore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateIsFavourite(isFavourite: Boolean) {
        datastore.edit { preferences ->
            preferences[PreferencesKeys.IS_FAVOURITE] = isFavourite
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val IS_FAVOURITE = preferencesKey<Boolean>("is_favourite")
    }
}