package kotlincursor.annotation

/**
 * Annotation that indicates that a different name is to be used when converting the annotated field
 * to [android.content.ContentValues] and from [android.database.Cursor].
 */
@Target(AnnotationTarget.FIELD)
annotation class ColumnName(val value: String)