# Kotlin-Cursor

An annotation processor that generates extension functions to convert [Kotlin data classes][data-class]
to [ContentValues][content-values] and from [Cursor][cursor].

## Usage

Annotate your data class with `@KCursorData`

```kotlin
@KCursorData
data class Cat(val name: String)
```

then once the class is processed a file will be generated with the following extension functions:

```kotlin
fun Cat.toContentValues(): ContentValues { /* ... */ }
fun Cursor.toCat(): Cat { /* ... */ }
```

## Custom types 

The following types are supported by default:

 * `String`
 * `ByteArray`
 * `Double`
 * `Float`
 * `Int`
 * `Long`
 * `Short`
 * `Boolean`

Kotlin-Cursor also supports types that are also annotated with `@KCursorData`. For exmaple:
```kotlin
@KCursorData
data class Bed(val owner: Cat)
```

For other types, you need to use the `@ColumnAdapter` annotation and specify a class that implements
`ColumnTypeAdapter`. For example:

`Person.kt`

```kotlin
data class Person(
    val name: String,
    @ColumnAdapter(CatListAdapter::class)
    val cats: List<Cat>
)
```

`CatListColumnTypeAdapter.kt`:

```kotlin
class CatListAdapter : ColumnTypeAdapter<List<Cat>> {
    override fun fromCursor(cursor: Cursor, columnName: String): List<Cat> {
        val listOfCats = cursor.getString(cursor.getColumnIndexOrThrow("cat_list"))
        return listOfCats.split(',').map { Cat(it) }
    }

    override fun toContentValues(values: ContentValues, columnName: String, value: List<Cat>) {
        values.put("cat_list", value.map { it.name }.joinToString(separator = ","))
    }
}
```

## Setup

Add a Gradle dependency:

```groovy
compile 'com.github.danysantiago:kotlincursor-api:0.1.1'
kapt 'com.github.danysantiago:kotlincursor-compiler:0.1.1'
```
Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

## Thanks

To [Gabriel Ittner][gabriel]'s [auto-value-cursor][auto-cursor] for which this project is
based on.

 [content-values]: https://developer.android.com/reference/android/content/ContentValues.html
 [cursor]: https://developer.android.com/reference/android/database/Cursor.html
 [data-class]: https://kotlinlang.org/docs/reference/data-classes.html
 [gabriel]: https://github.com/gabrielittner
 [auto-cursor]: https://github.com/gabrielittner/auto-value-cursor
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/

