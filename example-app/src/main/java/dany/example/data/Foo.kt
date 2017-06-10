package dany.example.data

import android.content.ContentValues
import android.database.Cursor
import kotlincursor.annotation.ColumnTypeAdapter

data class Foo(
        val someOther: Int
) {
    class ColumnAdapter : ColumnTypeAdapter<Foo> {
        override fun fromCursor(cursor: Cursor, columnName: String): Foo {
            return Foo(cursor.getInt(cursor.getColumnIndexOrThrow("some_other_value")))
        }

        override fun toContentValues(values: ContentValues, columnName: String, value: Foo) {
            values.put("some_other_value", value.someOther)
        }

    }
}