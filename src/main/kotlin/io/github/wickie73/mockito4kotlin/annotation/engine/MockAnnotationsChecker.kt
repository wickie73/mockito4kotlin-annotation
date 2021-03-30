/*
 *
 * The MIT License
 *
 *   Copyright (c) 2017-2021 Wilhelm Schulenburg
 *   Copyright (c) 2007 Mockito contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package io.github.wickie73.mockito4kotlin.annotation.engine

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.exceptions.base.MockitoException
import io.github.wickie73.mockito4kotlin.annotation.*
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.*
import kotlin.reflect.jvm.javaField

internal object MockAnnotationsChecker { // TODO renamed to  *Verifier

    private val injectMocksExample = """
                             |Examples of correct usage of @InjectMocks:
                             |  @InjectMocks
                             |  val service = Service()
                             |  @InjectMocks
                             |  lateinit var service: Service
                             """.trimMargin()

    private val mockExample = """
                              |
                              |Examples of correct usage:
                              |  @Mock
                              |  var service: Service? = null
                              |  @Mock
                              |  lateinit var service: Service
                              """.trimMargin()

    private val cannotCreateInstanceText = """
                                           |You haven't provided the instance at field declaration so an instance have to be constructed.
                                           """.trimMargin()

    internal fun checkImmutableProperties(property: KProperty<*>) {
        inCaseOf {
            property !is KMutableProperty
        } throwMockitoException {
            """
            |Cannot mock field named '${property.name}', cause it is immutable!
            |$mockExample
            """.trimMargin()
        }
    }

    internal fun checkNumberOfMockAnnotations(property: KProperty<*>) {

        fun isMockito4KotlinAnnotationOr(orPredicate: Predicate<Annotation>): Predicate<Annotation> =
            { isMockito4KotlinAnnotation(it) || orPredicate(it) }

        fun isSpyWithInjectMocksAnnotation(annotations: List<Annotation>) =
            annotations.count { it is Spy || it is InjectMocks } == 2

        val annotations = property.allAnnotations().filter(isMockito4KotlinAnnotationOr { it is InjectMocks })
        val annotationClassNames = annotations.map { it.annotationClass.simpleName }

        inCaseOf {
            annotations.count() != 1 && !isSpyWithInjectMocksAnnotation(annotations)
        } throwMockitoException {
            """
            |Property '${property.name}' has multiple Mockito4Kotlin annotations:
            |$annotationClassNames
            """.trimMargin()
        }
    }


    internal fun checkPrivateOrInternalInnerClass(annotationClass: KClass<out Any>, property: KProperty<*>, anyWithMocks: Any) {
        inCaseOf {
            property.kClass?.isInner == true &&
                (property.kClass?.visibility == KVisibility.PRIVATE ||
                    property.kClass?.visibility == KVisibility.INTERNAL)
        } throwMockitoException {
            """
            |Unable to initialize @${annotationClass.simpleName} annotated field named '${property.name}' of type '${classNameOf(property)}'.
            |
            |@${annotationClass.simpleName} annotation can't initialize private/internal inner classes.
            |  inner class: '${classNameOf(property)}'
            |  outer class: '${anyWithMocks::class.simpleName}'
            |
            |You should augment the visibility of this inner class.
            """.trimMargin()
        }
    }

    internal fun checkPrivateOrInternalCompanionObjects(annotationClass: KClass<out Any>, property: KProperty<*>) {
        inCaseOf {
            property.kClass?.isCompanion == true
        } throwMockitoException {
            """
            |Unable to initialize @${annotationClass.simpleName} annotated field named '${property.name}' of type '${classNameOf(property)}'.
            |@${annotationClass.simpleName} annotation can't initialize companion objects.
            |You should avoid to mock/spy companion objects.
            """.trimMargin()
        }
    }

    internal fun checkSealedClass(annotationClass: KClass<out Any>, property: KProperty<*>) {
        inCaseOf {
            property.kClass?.isSealed == true
        } throwMockitoException {
            """
            |Unable to initialize @${annotationClass.simpleName} annotated field named '${property.name}' of type '${classNameOf(property)}'.
            |@${annotationClass.simpleName} annotation can't initialize sealed classes.
            |You should avoid to mock/spy sealed classes.
            """.trimMargin()
        }
    }


    internal fun checkDelegateProperty(annotationClass: KClass<out Any>, property: KProperty<*>) {
        val delegateClassName = property.javaField?.type?.kotlin?.qualifiedName ?: "unknown"
        inCaseOf {
            property.kClass != property.javaField?.type?.kotlin
        } throwMockitoException {
            """
            |Unable to initialize @${annotationClass.simpleName} annotated field named '${property.name}' of type '${classNameOf(property)}'.
            |It seems to be that '${classNameOf(property)}' delegates to '$delegateClassName'.
            |You should avoid to mock/spy delegated properties.
            """.trimMargin()
        }
    }

    internal fun checkIsArgumentCaptor(property: KProperty<*>) {
        inCaseOf {
            ArgumentCaptor::class != property.returnType.classifier
        } throwMockitoException {
            """
            |@Captor field must be of the type ${ArgumentCaptor::class.qualifiedName}."
            |Property: '${property.name}' has wrong type.
            |For info how to use @Captor annotations see examples in javadoc for ${Captor::class.simpleName} class.
            """.trimMargin()
        }
    }


    internal fun checkIsKArgumentCaptor(property: KProperty<*>) {
        inCaseOf {
            KArgumentCaptor::class != property.returnType.classifier
        } throwMockitoException {
            """
            |@KCaptor field must be of the type ${KArgumentCaptor::class.qualifiedName}."
            |Property: '${property.name}' has wrong type.
            |For info how to use @Captor annotations see examples in javadoc for ${KCaptor::class.simpleName} class.
            """.trimMargin()
        }
    }

    internal fun checkInjectMocksProperty(property: KProperty<*>) {

        fun isInterface(property: KProperty<*>) = property.kClass?.java?.isInterface ?: true

        fun hasNoArgsConstructor(property: KProperty<*>) =
            property.kClass?.constructors?.singleOrNull { it.parameters.all(KParameter::isOptional) } != null

        fun isInnerClassof(property: KProperty<*>) = property.kClass?.isInner ?: false

        inCaseOf {
            isInterface(property)
        } throwMockitoException {
            """
            |Type '${simpleClassNameOf(property)}' is an interface.
            |Cannot instantiate @InjectMocks field named '${property.name}'!
            |$cannotCreateInstanceText
            |
            |$injectMocksExample
            """.trimMargin()
        }

        inCaseOf {
            isInnerClassof(property)
        } throwMockitoException {
            """
            |Cannot instantiate @InjectMocks field named '${property.name}'!
            |$cannotCreateInstanceText
            |But '${simpleClassNameOf(property)}' is an inner class and has internally no empty constructor.
            |
            |$injectMocksExample
            """.trimMargin()
        }

        inCaseOf {
            !hasNoArgsConstructor(property)
        } throwMockitoException {
            """
            |Cannot instantiate @InjectMocks field named '${property.name}'!
            |$cannotCreateInstanceText
            |But '${simpleClassNameOf(property)}' has no empty constructor.
            |
            |$injectMocksExample
            """.trimMargin()
        }
    }


    internal fun checkExceptionAfterCreateInstanceOfInjectMocks(e: Exception, property: KProperty<*>) {
        val reason = if (e is InvocationTargetException) "constructor threw an exception" else e::class.simpleName
        inCaseOf { true } throwMockitoException {
            """
            |Cannot instantiate @InjectMocks field named '${property.name}'!
            |$cannotCreateInstanceText
            |But this failed due to: $reason
            |
            |$injectMocksExample
            """.trimMargin()
        }
    }

    internal fun checkInstanceOfInjectMocksIsNotNull(instance: Any?, property: KProperty<*>) {
        inCaseOf {
            instance == null
        } throwMockitoException {
            """
            |Cannot instantiate @InjectMocks field named '${property.name}'!
            |$cannotCreateInstanceText
            |
            |$injectMocksExample
            """.trimMargin()
        }
    }

    internal fun checkImmutableInjectMocksProperty(property: KProperty<*>) {
        inCaseOf {
            property !is KMutableProperty
        } throwMockitoException {
            """
            |Cannot instantiate @InjectMocks field named '${property.name}', cause it is immutable!
            |$cannotCreateInstanceText
            |
            |$injectMocksExample
            """.trimMargin()
        }
    }

    private fun classNameOf(property: KProperty<*>) = property.kClass?.qualifiedName ?: "unknown"

    private fun simpleClassNameOf(property: KProperty<*>) = property.kClass?.simpleName ?: "unknown"

    private fun inCaseOf(condition: () -> Boolean) = condition

    private infix fun (() -> Boolean).throwMockitoException(errorMessage: () -> String) =
        if (this()) throw MockitoException(errorMessage()) else Unit

}
