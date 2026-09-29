package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.models.CompatibilityStatus

class Converters {
    @TypeConverter
    fun fromCompatibilityStatus(status: CompatibilityStatus): String {
        return status.name
    }

    @TypeConverter
    fun toCompatibilityStatus(value: String): CompatibilityStatus {
        return try {
            CompatibilityStatus.valueOf(value)
        } catch (_: Exception) {
            CompatibilityStatus.UNKNOWN
        }
    }
}
