package dany.example.data

import kotlincursor.annotation.ColumnAdapter
import kotlincursor.annotation.ColumnName
import kotlincursor.annotation.KCursorData

@KCursorData
data class DataObject(
        val aString: String,

        val aNullableString: String?,

        val aByteArray: ByteArray,

        val aNullableByteArray: ByteArray?,

        val aDouble: Double,

        val aNullableDouble: Double?,

        val aFloat: Float,

        val aNullableFloat: Float?,

        val anInt: Int,

        val anNullableInt: Int?,

        val anLong: Long,

        val anNullableLong: Long?,

        val anShort: Short,

        val anNullableShort: Short?,

        val anBoolean: Boolean,

        val anNullableBoolean: Boolean?,

        val aDataCursorProperty: Bar,

        val aNullableDataCursorProperty: Bar?,

        @ColumnAdapter(Foo.ColumnAdapter::class)
        val aColumnAdapterProperty: Foo,

        @ColumnAdapter(Foo.ColumnAdapter::class)
        val aNullableColumnAdapterProperty: Foo?,

        @ColumnName("my_named_int")
        val aColumnNamedInt: Int,

        @ColumnName("my_nullable_named_int")
        val aNullableColumnNamedInt: Int?,

        @ColumnName("my_named_cursor_data_class")
        val aNamedDataCursorProperty: Bar,

        @ColumnName("my_nullable_named_cursor_data_class")
        val aNullableNamedDataCursorProperty: Bar?,

        @ColumnName("my_named_column_adapter")
        @ColumnAdapter(Foo.ColumnAdapter::class)
        val aNamedColumnAdapterProperty: Foo,

        @ColumnName("my_nullable_named_column_adapter")
        @ColumnAdapter(Foo.ColumnAdapter::class)
        val aNullableNamedColumnAdapterProperty: Foo?
)
