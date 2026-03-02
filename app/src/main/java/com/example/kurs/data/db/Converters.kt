package com.example.kurs.data.db

import androidx.room.TypeConverter
import com.example.kurs.data.db.entity.AchievementType
import com.example.kurs.data.db.entity.BlockLevel
import com.example.kurs.data.db.entity.QuestionType

class Converters {
    @TypeConverter
    fun fromBlockLevel(value: BlockLevel): String = value.name
    @TypeConverter
    fun toBlockLevel(value: String): BlockLevel = BlockLevel.valueOf(value)

    @TypeConverter
    fun fromQuestionType(value: QuestionType): String = value.name
    @TypeConverter
    fun toQuestionType(value: String): QuestionType = QuestionType.valueOf(value)

    @TypeConverter
    fun fromAchievementType(value: AchievementType): String = value.name
    @TypeConverter
    fun toAchievementType(value: String): AchievementType = AchievementType.valueOf(value)
}