/*
 *
 * The MIT License
 *
 *   Copyright (c) 2017 Wilhelm Schulenburg
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

import io.github.wickie73.mockito4kotlin.annotation.*
import io.github.wickie73.mockito4kotlin.annotation.MockPropertyCollector
import org.mockito.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class AnnotationEngineManager( private val anyInstanceWithMocks : Any) {
    private val mockedAssignedProperties = MockPropertyCollector()

    fun process() : AutoCloseable {
        val properties = anyInstanceWithMocks::class.memberProperties

        processAnnotations(anyInstanceWithMocks, properties, ::isMockito4KotlinAnnotation)
        registerInlinedMockDeclarations(anyInstanceWithMocks, properties)
        processAnnotations(anyInstanceWithMocks, properties) { it is InjectMocks }

        return AutoCloseable {
            mockedAssignedProperties.reset()
        }
    }

    private fun processAnnotations(anyInstanceWithMocks: Any, properties: Collection<KProperty<*>>, predicate: Predicate<Annotation>) {
        properties.filter { it.allAnnotations().any(predicate) }
            .forEach { property ->
                AnnotationEngineFactory.create(asAnnotation(property, predicate)).apply {
                    initWith(mockedAssignedProperties)
                    process(anyInstanceWithMocks, property)
                }
            }
    }

    private fun registerInlinedMockDeclarations(anyInstanceWithMocks: Any, properties: Collection<KProperty<*>>) {

        fun isValueMocked(value: Any): Boolean {
            val mockingDetails = Mockito.mockingDetails(value)
            return mockingDetails.isMock || mockingDetails.isSpy
        }

        properties.forEach { property ->
            property.isAccessible = true
            property.getter.call(anyInstanceWithMocks)?.let { value ->
                if (isValueMocked(value)) {
                    mockedAssignedProperties.register(property, value)
                }
            }
        }
    }

    private fun asAnnotation(property: KProperty<*>, predicate: Predicate<Annotation>) =
        property.allAnnotations().find(predicate)
}

/**
 * Returns true if the given annotation is a Mockito4Kotlin annotation like:
 * [Mock], [KMock], [Spy], [Captor] and [KCaptor].
 *
 * @param it given annotation
 */
internal fun isMockito4KotlinAnnotation(it: Annotation) = it is Mock || it is Spy || it is Captor || it is KCaptor || it is KMock

internal typealias Predicate<T> = (T) -> Boolean
