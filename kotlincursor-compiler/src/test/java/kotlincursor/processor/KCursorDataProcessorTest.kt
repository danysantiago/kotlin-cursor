package kotlincursor.processor

import com.google.common.truth.Truth.assertThat
import com.google.testing.compile.CompilationRule
import kotlincursor.data.AccompaniedData
import kotlincursor.data.AdapterData
import kotlincursor.data.InvalidData
import kotlincursor.data.NestedCursorData
import kotlincursor.data.NullableAdapterData
import kotlincursor.data.NullableNestedCursorData
import kotlincursor.data.NullableSupportedTypesData
import kotlincursor.data.SimpleData
import kotlincursor.data.SimpleNamedData
import kotlincursor.data.SupportedTypesData
import kotlincursor.test.TestMessager
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.properties.Delegates


class KCursorDataProcessorTest {

    @get:Rule val rule = CompilationRule()

    var elements by Delegates.notNull<Elements>()
    var types by Delegates.notNull<Types>()
    var messager by Delegates.notNull<Messager>()

    @Before
    fun setup() {
        elements = rule.elements
        types = rule.types
        messager = TestMessager()
    }

    @Test
    fun simple() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun SimpleData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.put("a", a)
                |  return values
                |}
                |
                |fun Cursor.toSimpleData(): SimpleData {
                |  val a = this.getInt(this.getColumnIndexOrThrow("a"))
                |  return SimpleData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(SimpleData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun columnName() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun SimpleNamedData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.put("my_name", a)
                |  return values
                |}
                |
                |fun Cursor.toSimpleNamedData(): SimpleNamedData {
                |  val a = this.getInt(this.getColumnIndexOrThrow("my_name"))
                |  return SimpleNamedData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(SimpleNamedData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun supportedTypes() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun SupportedTypesData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.put("aString", aString)
                |  values.put("aByteArray", aByteArray)
                |  values.put("aDouble", aDouble)
                |  values.put("aFloat", aFloat)
                |  values.put("anInt", anInt)
                |  values.put("anLong", anLong)
                |  values.put("anShort", anShort)
                |  values.put("anBoolean", anBoolean)
                |  return values
                |}
                |
                |fun Cursor.toSupportedTypesData(): SupportedTypesData {
                |  val aString = this.getString(this.getColumnIndexOrThrow("aString"))
                |  val aByteArray = this.getBlob(this.getColumnIndexOrThrow("aByteArray"))
                |  val aDouble = this.getDouble(this.getColumnIndexOrThrow("aDouble"))
                |  val aFloat = this.getFloat(this.getColumnIndexOrThrow("aFloat"))
                |  val anInt = this.getInt(this.getColumnIndexOrThrow("anInt"))
                |  val anLong = this.getLong(this.getColumnIndexOrThrow("anLong"))
                |  val anShort = this.getShort(this.getColumnIndexOrThrow("anShort"))
                |  val anBoolean = this.getInt(this.getColumnIndexOrThrow("anBoolean")) == 1
                |  return SupportedTypesData(aString, aByteArray, aDouble, aFloat, anInt, anLong, anShort, anBoolean)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(SupportedTypesData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun nullableSupportedTypes() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun NullableSupportedTypesData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.put("aString", aString)
                |  values.put("aByteArray", aByteArray)
                |  values.put("aDouble", aDouble)
                |  values.put("aFloat", aFloat)
                |  values.put("anInt", anInt)
                |  values.put("anLong", anLong)
                |  values.put("anShort", anShort)
                |  values.put("anBoolean", anBoolean)
                |  return values
                |}
                |
                |fun Cursor.toNullableSupportedTypesData(): NullableSupportedTypesData {
                |  val aStringIndex = this.getColumnIndex("aString")
                |  val aString = if (aStringIndex == -1 || this.isNull(aStringIndex)) null else this.getString(aStringIndex)
                |  val aByteArrayIndex = this.getColumnIndex("aByteArray")
                |  val aByteArray = if (aByteArrayIndex == -1 || this.isNull(aByteArrayIndex)) null else this.getBlob(aByteArrayIndex)
                |  val aDoubleIndex = this.getColumnIndex("aDouble")
                |  val aDouble = if (aDoubleIndex == -1 || this.isNull(aDoubleIndex)) null else this.getDouble(aDoubleIndex)
                |  val aFloatIndex = this.getColumnIndex("aFloat")
                |  val aFloat = if (aFloatIndex == -1 || this.isNull(aFloatIndex)) null else this.getFloat(aFloatIndex)
                |  val anIntIndex = this.getColumnIndex("anInt")
                |  val anInt = if (anIntIndex == -1 || this.isNull(anIntIndex)) null else this.getInt(anIntIndex)
                |  val anLongIndex = this.getColumnIndex("anLong")
                |  val anLong = if (anLongIndex == -1 || this.isNull(anLongIndex)) null else this.getLong(anLongIndex)
                |  val anShortIndex = this.getColumnIndex("anShort")
                |  val anShort = if (anShortIndex == -1 || this.isNull(anShortIndex)) null else this.getShort(anShortIndex)
                |  val anBooleanIndex = this.getColumnIndex("anBoolean")
                |  val anBoolean = if (anBooleanIndex == -1 || this.isNull(anBooleanIndex)) null else this.getInt(anBooleanIndex) == 1
                |  return NullableSupportedTypesData(aString, aByteArray, aDouble, aFloat, anInt, anLong, anShort, anBoolean)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(NullableSupportedTypesData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun nestedKCursorData() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun NestedCursorData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.putAll(a.toContentValues())
                |  return values
                |}
                |
                |fun Cursor.toNestedCursorData(): NestedCursorData {
                |  val a = this.toSimpleData()
                |  return NestedCursorData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(NestedCursorData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun nullableNestedKCursorData() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun NullableNestedCursorData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  if (a != null) values.putAll(a.toContentValues())
                |  return values
                |}
                |
                |fun Cursor.toNullableNestedCursorData(): NullableNestedCursorData {
                |  val a = this.toSimpleData()
                |  return NullableNestedCursorData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(NullableNestedCursorData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun columnAdapter() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun AdapterData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  kotlincursor.data.SimpleData.ColumnAdapter().toContentValues(values, "a", a)
                |  return values
                |}
                |
                |fun Cursor.toAdapterData(): AdapterData {
                |  val a = kotlincursor.data.SimpleData.ColumnAdapter().fromCursor(this, "a")
                |  return AdapterData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(AdapterData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun nullableColumnAdapter() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun NullableAdapterData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  if (a != null) kotlincursor.data.SimpleData.ColumnAdapter().toContentValues(values, "a", a)
                |  return values
                |}
                |
                |fun Cursor.toNullableAdapterData(): NullableAdapterData {
                |  val a = kotlincursor.data.SimpleData.ColumnAdapter().fromCursor(this, "a")
                |  return NullableAdapterData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(NullableAdapterData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }

    @Test
    fun invalidProperty() {
        val classElement = elements.getTypeElement(InvalidData::class.java.canonicalName)
        try {
            KCursorDataClass(messager, elements, types).generateKotlinFile(classElement)
            fail("Generating a Kotlin Class for $classElement should have thrown an exception.")
        } catch (e: TestMessager.ErrorMsgException) {
            assertThat(e.element!!.simpleName.toString()).isEqualTo("invalidProperty")
        }
    }

    @Test
    fun companionObject() {
        val expected = """
                |package kotlincursor.data
                |
                |import android.content.ContentValues
                |import android.database.Cursor
                |
                |fun AccompaniedData.toContentValues(): ContentValues {
                |  val values = android.content.ContentValues()
                |  values.put("a", a)
                |  return values
                |}
                |
                |fun Cursor.toAccompaniedData(): AccompaniedData {
                |  val a = this.getInt(this.getColumnIndexOrThrow("a"))
                |  return AccompaniedData(a)
                |}
                |""".trimMargin()

        val actual = KCursorDataClass(messager, elements, types).generateKotlinFile(
                elements.getTypeElement(AccompaniedData::class.java.canonicalName))

        assertThat(actual.toString()).isEqualTo(expected)
    }
}