package kotlincursor.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KotlinFile
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

class KCursorDataClass(
        val messager: Messager,
        val elements: Elements,
        val types: Types

) {
    fun generateKotlinFile(classElement: TypeElement): KotlinFile {
        val packageName = elements.getPackageOf(classElement).qualifiedName.toString()
        val fileName = CLASS_FILE_PREFIX + classElement.simpleName.toString()

        val properties = classElement.enclosedElements
                .filter { it.kind == ElementKind.FIELD && it is VariableElement }
                .map { ColumnProperty(it as VariableElement) }

        return KotlinFile.builder(packageName, fileName)
                .addFun(generateToContentValuesMethod(classElement, properties))
                .addFun(generateFromCursorMethod(classElement, properties))
                .build()
    }

    private fun generateToContentValuesMethod(classElement: TypeElement, properties: List<ColumnProperty>): FunSpec {
        val methodBuilder = FunSpec.builder("toContentValues")
                .receiver(TypeName.get(classElement.asType()))
                .addStatement("val values = $CONTENT_VALUES()")
                .returns(CONTENT_VALUES)

        properties.forEach {
            val columnAdapter = it.columnAdapter
            if (columnAdapter != null) {
                if (it.isNullable) methodBuilder.addCode("if (${it.humanName} != null) ")
                methodBuilder.addStatement(
                        "%L().toContentValues(values, \"${it.columnName}\", ${it.humanName})",
                        columnAdapter)
            } else if (it.isSupportedType) {
                methodBuilder.addStatement(
                        "values.put(\"${it.columnName}\", ${it.humanName})")
            } else if (it.isColumnTypeCursorDataClass(types)) {
                if (it.isNullable) methodBuilder.addCode("if (${it.humanName} != null) ")
                methodBuilder.addStatement(
                        "values.putAll(${it.humanName}.toContentValues())")
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "Property \"${it.humanName}\" " +
                        "of type ${it.columnType} can't be converted to ContentValues.",
                        it.element)
            }
        }
        return methodBuilder.addStatement("return values").build()
    }

    private fun generateFromCursorMethod(classElement: TypeElement, properties: List<ColumnProperty>): FunSpec {
        val methodBuilder = FunSpec.builder("to${classElement.simpleName}")
                .receiver(CURSOR)
                .returns(TypeName.get(classElement.asType()))

        val names = properties.map { it.humanName }
        properties.forEach {
            val columnAdapter = it.columnAdapter
            if (columnAdapter != null) {
                methodBuilder.addStatement(
                        "val ${it.humanName} = %L().fromCursor(this, \"${it.columnName}\")",
                        columnAdapter)
            } else if (it.isSupportedType) {
                if (it.isNullable) {
                    methodBuilder.addCode(generateReadNullableProperty(it))
                } else {
                    methodBuilder.addCode(generateReadProperty(it))
                }
            } else if (it.isColumnTypeCursorDataClass(types)) {
                val columnTypeSimpleName = types.asElement(it.element.asType()).simpleName
                methodBuilder.addStatement(
                        "val ${it.humanName} = this.to$columnTypeSimpleName()"
                )
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "Property \"${it.humanName}\" " +
                        "of type ${it.columnType} can't be converted from Cursor.",
                        it.element)
            }
        }
        val constructorCallBlock = generateClassConstructorCall(classElement.simpleName.toString(), names)
        return methodBuilder.addStatement("return $constructorCallBlock").build()
    }

    private fun generateReadNullableProperty(property: ColumnProperty): CodeBlock {
        val columnIndexVariableName = "${property.humanName}Index"
        val getValueBlock = CodeBlock.of(property.getCursorMethod(), columnIndexVariableName)
        val nullCheckBlock = CodeBlock.of(
                "if ($columnIndexVariableName == -1 || this.isNull($columnIndexVariableName)) null else %L",
                getValueBlock)
        return CodeBlock.builder()
                .addStatement("val $columnIndexVariableName = this.getColumnIndex(\"${property.columnName}\")")
                .addStatement("val ${property.humanName} = %L", nullCheckBlock)
                .build()
    }

    private fun generateReadProperty(property: ColumnProperty): CodeBlock {
        val getColumnIndexOrThrowBlock = CodeBlock.of("this.getColumnIndexOrThrow(\"${property.columnName}\")")
        val getValueBlock = CodeBlock.of(property.getCursorMethod(), getColumnIndexOrThrowBlock)
        return CodeBlock.builder()
                .addStatement("val ${property.humanName} = %L", getValueBlock)
                .build()
    }

    private fun generateClassConstructorCall(className: String, names: List<String>): String {
        return "$className(${names.joinToString()})"
    }

    companion object {
        const val CLASS_FILE_PREFIX = "KCursor"

        val CONTENT_VALUES = ClassName.get("android.content", "ContentValues")
        val CURSOR = ClassName.get("android.database", "Cursor")
    }
}
