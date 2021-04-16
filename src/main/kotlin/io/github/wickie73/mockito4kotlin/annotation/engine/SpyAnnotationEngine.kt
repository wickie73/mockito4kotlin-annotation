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

import org.mockito.Mockito
import org.mockito.Spy
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyImmutableProperties
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyNumberOfMockAnnotations
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyPrivateOrInternalCompanionObjects
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyPrivateOrInternalInnerClass
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifySealedClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal class SpyAnnotationEngine : AbstractAnnotationEngine() {

    override fun process(anyInstanceWithMocks: Any, property: KProperty<*>) {
        property.isAccessible = true
        verifyImmutableProperties(property)
        verifyNumberOfMockAnnotations(property)
        verifyPrivateOrInternalInnerClass(Spy::class, property, anyInstanceWithMocks)
        verifyPrivateOrInternalCompanionObjects(Spy::class, property)
        verifySealedClass(Spy::class, property)

        val instanceValue = if (property.isLateinit) null else property.getter.call(anyInstanceWithMocks)
        assignObjectToProperty(property as KMutableProperty<*>, anyInstanceWithMocks, createSpy(instanceValue, property))
    }

    private fun createSpy(instanceValue: Any?, property: KProperty<*>): Any =
        if (instanceValue != null) Mockito.spy(instanceValue) else Mockito.spy(property.javaField?.type)
}
