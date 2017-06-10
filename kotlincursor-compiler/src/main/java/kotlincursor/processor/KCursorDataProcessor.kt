package kotlincursor.processor

import com.google.auto.service.AutoService
import kotlincursor.annotation.KCursorData
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.StandardLocation


@AutoService(Processor::class)
class KCursorDataProcessor : AbstractProcessor() {

    val messager: Messager by lazy { processingEnv.messager }
    val elements: Elements by lazy { processingEnv.elementUtils }
    val types: Types by lazy { processingEnv.typeUtils }
    val filer: Filer by lazy { processingEnv.filer }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        messager.printMessage(Diagnostic.Kind.OTHER, "Initialized KotlinCursorProcessor")
    }

    override fun getSupportedAnnotationTypes() = setOf(KCursorData::class.java.canonicalName)

    override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val cursorDataClass = KCursorDataClass(messager, elements, types)

        roundEnv.getElementsAnnotatedWith(KCursorData::class.java).forEach {
            if (it is TypeElement) {
                val kotlinFile = cursorDataClass.generateKotlinFile(it)
                val file = filer.createResource(StandardLocation.SOURCE_OUTPUT,
                        kotlinFile.packageName, "${kotlinFile.fileName}.kt", it)
                file.openWriter().use {
                    kotlinFile.writeTo(it)
                }
            } else {
                messager.printMessage(Diagnostic.Kind.WARNING, "Found element annotated with " +
                        "${KCursorData::class} but its not a data class!", it)
            }
        }

        return true
    }
}
