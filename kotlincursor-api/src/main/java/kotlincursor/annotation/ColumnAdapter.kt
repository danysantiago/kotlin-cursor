package kotlincursor.annotation

import kotlin.reflect.KClass

/**
 * Annotation that indicates the [ColumnTypeAdapter] to use when converting the annotated field to
 * [android.content.ContentValues] and from [android.database.Cursor].
 */
@Target(AnnotationTarget.FIELD)
annotation class ColumnAdapter(val value: KClass<out ColumnTypeAdapter<*>>)