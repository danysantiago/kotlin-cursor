package kotlincursor.test

import javax.annotation.processing.Messager
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.tools.Diagnostic


class TestMessager : Messager {

    override fun printMessage(kind: Diagnostic.Kind, msg: CharSequence) {
        printMessage(kind, msg, null)
    }

    override fun printMessage(kind: Diagnostic.Kind, msg: CharSequence, e: Element?) {
        printMessage(kind, msg, e, null)
    }

    override fun printMessage(kind: Diagnostic.Kind, msg: CharSequence, e: Element?, a: AnnotationMirror?) {
        printMessage(kind, msg, e, a, null)
    }

    override fun printMessage(kind: Diagnostic.Kind, msg: CharSequence, e: Element?, a: AnnotationMirror?, v: AnnotationValue?) {
        if (kind == Diagnostic.Kind.ERROR) {
            throw ErrorMsgException(msg.toString(), e)
        } else {
            System.out.println(msg)
        }
    }

    class ErrorMsgException(msg: String, val element: Element?) : RuntimeException(msg)
}
