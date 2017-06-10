package kotlincursor.processor

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.TypeName
import kotlincursor.annotation.ColumnAdapter
import kotlincursor.annotation.ColumnName
import kotlincursor.annotation.KCursorData
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import kotlin.reflect.KClass


class ColumnProperty(
        element: VariableElement
) {
    val element: VariableElement = element
    val humanName: String = element.simpleName.toString()
    val columnName: String by lazy(this::initColumnName)
    val columnType: TypeName = TypeName.get(element.asType())
    val columnAdapter: ClassName? by lazy(this::initColumnAdapter)
    val isPrimitive: Boolean = element.asType().kind.isPrimitive
    val isNullable: Boolean by lazy(this::initIsNullable)
    val isSupportedType: Boolean = SUPPORTED_TYPES.contains(columnType)

    private fun initColumnName(): String {
        val value = getAnnotationValue(element, ColumnName::class, "value")
        return if (value != null && value is String) value else humanName
    }

    private fun initColumnAdapter(): ClassName? {
        val value = getAnnotationValue(element, ColumnAdapter::class, "value")
        return if (value != null && value is TypeMirror) TypeName.get(value) as ClassName else null
    }

    private fun getAnnotationValue(element: Element, annotation: KClass<out Annotation>, key: String): Any? {
        return element.annotationMirrors
                .find { it.annotationType.toString() == annotation.java.canonicalName }
                ?.elementValues?.entries?.find { it.key.simpleName.contentEquals(key) }
                ?.value?.value
    }

    private fun initIsNullable(): Boolean {
        return !isPrimitive && element.annotationMirrors
                .map { it.annotationType.asElement().simpleName.toString() }
                .none { it == kotlincursor.processor.ColumnProperty.NON_NULL_ANNOTATION_NAME
                        || it == kotlincursor.processor.ColumnProperty.NOT_NULL_ANNOTATION_NAME }
    }

    fun isColumnTypeCursorDataClass(typeUtils: Types): Boolean {
        return typeUtils.asElement(element.asType()).annotationMirrors
                .map { it.annotationType.toString() }
                .contains(KCursorData::class.java.canonicalName)
    }

    fun getCursorMethod(): String {
        if (!isSupportedType) {
            throw IllegalStateException("Called cursorMethod but Column is not a supported type")
        }

        when(columnType) {
            TypeName.get(String::class) -> return "this.getString(%L)"
            TypeName.get(ByteArray::class) -> return "this.getBlob(%L)"
            DOUBLE, TypeName.get(java.lang.Double::class) -> return "this.getDouble(%L)"
            FLOAT, TypeName.get(java.lang.Float::class) -> return "this.getFloat(%L)"
            INT, TypeName.get(java.lang.Integer::class) -> return "this.getInt(%L)"
            LONG, TypeName.get(java.lang.Long::class) -> return "this.getLong(%L)"
            SHORT, TypeName.get(java.lang.Short::class) -> return "this.getShort(%L)"
            BOOLEAN, TypeName.get(java.lang.Boolean::class) -> return "this.getInt(%L) == 1"
            else -> throw IllegalStateException("isSupportedType is true but type $columnType wasn't handled")
        }
    }

    companion object {
        const val NON_NULL_ANNOTATION_NAME = "NonNull"
        const val NOT_NULL_ANNOTATION_NAME = "NotNull"

        internal val SUPPORTED_TYPES = arrayOf(
                TypeName.get(String::class),
                TypeName.get(ByteArray::class),
                DOUBLE, TypeName.get(java.lang.Double::class),
                FLOAT, TypeName.get(java.lang.Float::class),
                INT, TypeName.get(java.lang.Integer::class),
                LONG, TypeName.get(java.lang.Long::class),
                SHORT, TypeName.get(java.lang.Short::class),
                BOOLEAN, TypeName.get(java.lang.Boolean::class)
        )
    }
}