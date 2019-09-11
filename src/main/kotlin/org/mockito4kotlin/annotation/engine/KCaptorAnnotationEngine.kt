/*
 * The MIT License
 *
 *   Copyright (c) 2017-2019 Wilhelm Schulenburg
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
 */

package org.mockito4kotlin.annotation.engine

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import org.mockito.ArgumentCaptor
import org.mockito4kotlin.annotation.engine.MockAnnotationsChecker.checkImmutableProperties
import org.mockito4kotlin.annotation.engine.MockAnnotationsChecker.checkIsKArgumentCaptor
import org.mockito4kotlin.annotation.engine.MockAnnotationsChecker.checkNumberOfMockAnnotations
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.isAccessible

internal class KCaptorAnnotationEngine : AbstractAnnotationEngine() {

    override fun process(anyWithMocks: Any, property: KProperty<*>) {
        property.isAccessible = true
        checkImmutableProperties(property)
        checkNumberOfMockAnnotations(property)
        checkIsKArgumentCaptor(property)

        with(property as KMutableProperty<*>) {
            property.setter.call(anyWithMocks, createArgumentCaptor(property))
        }
    }

    private fun createArgumentCaptor(property: KProperty<*>): Any {
        val genericClass = genericClassOf(property.returnType)
        return KArgumentCaptor(ArgumentCaptor.forClass(genericClass.java), genericClass)
    }

    private fun genericClassOf(propertyReturnType: KType): KClass<out Any> =
        propertyReturnType.arguments.firstOrNull()?.type?.classifier as? KClass<out Any> ?: Any::class
}
