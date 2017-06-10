package kotlincursor.annotation

import android.content.ContentValues
import android.database.Cursor

interface ColumnTypeAdapter<T> {
    fun fromCursor(cursor: Cursor, columnName: String): T
    fun toContentValues(values: ContentValues, columnName: String, value: T)
}