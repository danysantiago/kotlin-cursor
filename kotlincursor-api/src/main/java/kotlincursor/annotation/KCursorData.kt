package kotlincursor.annotation

/**
 * Annotation that indicates that a class can be converted to [android.content.ContentValues] and
 * from [android.database.Cursor].
 *
 * For example, annotating a data class as follows:
 *
 * ```
 * @KCursorData
 * data class Cat(name: String)
 * ```
 *
 * will generate a Kotlin file containing extension functions for performing the conversion.
 *
 * ```
 * fun Cat.toContentValues(): ContentValues {
 *   val values = android.content.ContentValues()
 *   values.put("name", name)
 *   return values
 * }
 *
 * fun Cursor.toCat(): Cat {
 *   val name = this.getInt(this.getColumnIndexOrThrow("name"))
 *   return Cat(name)
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
annotation class KCursorData