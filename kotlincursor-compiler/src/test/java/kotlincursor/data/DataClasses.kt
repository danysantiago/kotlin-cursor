package kotlincursor.data

import android.content.ContentValues
import android.database.Cursor
import kotlincursor.annotation.ColumnAdapter
import kotlincursor.annotation.ColumnName
import kotlincursor.annotation.ColumnTypeAdapter
import kotlincursor.annotation.KCursorData

@KCursorData
data class SimpleData(val a: Int) {
    class ColumnAdapter : ColumnTypeAdapter<SimpleData> {
        override fun fromCursor(cursor: Cursor, columnName: String): SimpleData {
            return SimpleData(cursor.getInt(cursor.getColumnIndexOrThrow(columnName)))
        }

        override fun toContentValues(values: ContentValues, columnName: String, value: SimpleData) {
            values.put(columnName, value.a)
        }
    }
}

@KCursorData
data class SimpleNamedData(@ColumnName("my_name") val a: Int)

@KCursorData
data class SupportedTypesData(
        val aString: String,
        val aByteArray: ByteArray,
        val aDouble: Double,
        val aFloat: Float,
        val anInt: Int,
        val anLong: Long,
        val anShort: Short,
        val anBoolean: Boolean
)

@KCursorData
data class NullableSupportedTypesData(
        val aString: String?,
        val aByteArray: ByteArray?,
        val aDouble: Double?,
        val aFloat: Float?,
        val anInt: Int?,
        val anLong: Long?,
        val anShort: Short?,
        val anBoolean: Boolean?
)

@KCursorData
data class NestedCursorData(val a: SimpleData)

@KCursorData
data class NullableNestedCursorData(val a: SimpleData?)

@KCursorData
data class AdapterData(@ColumnAdapter(SimpleData.ColumnAdapter::class) val a: SimpleData)

@KCursorData
data class NullableAdapterData(@ColumnAdapter(SimpleData.ColumnAdapter::class) val a: SimpleData?)

@KCursorData
data class InvalidData(val invalidProperty: List<String>)