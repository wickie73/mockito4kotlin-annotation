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

import io.github.wickie73.mockito4kotlin.annotation.allAnnotations
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyDelegateProperty
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyImmutableProperties
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyNumberOfMockAnnotations
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyPrivateOrInternalCompanionObjects
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyPrivateOrInternalInnerClass
import org.mockito.Mock
import org.mockito.MockSettings
import org.mockito.Mockito
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal class MockAnnotationEngine : AbstractAnnotationEngine() {

    override fun process(anyInstanceWithMocks: Any, property: KProperty<*>) {
        property.isAccessible = true
        verifyImmutableProperties(property)
        verifyNumberOfMockAnnotations(property)
        verifyPrivateOrInternalInnerClass(Mock::class, property, anyInstanceWithMocks)
        verifyPrivateOrInternalCompanionObjects(Mock::class, property)
        verifyDelegateProperty(Mock::class, property)

        assignObjectToProperty(property as KMutableProperty<*>, anyInstanceWithMocks, createMock(property))
    }

    private fun createMock(property: KMutableProperty<*>): Any = Mockito.mock(property.javaField?.type, toMockSettings(property))

    private fun toMockSettings(property: KProperty<*>): MockSettings {
        val mockSettings: MockSettings = Mockito.withSettings()
        with(toMockAnnotation(property)) {
            when {
                extraInterfaces.isNotEmpty() -> mockSettings.extraInterfaces(*extraInterfaces.map { it.java }.toTypedArray())
            }
            if (stubOnly) mockSettings.stubOnly()
            if (name.isNotEmpty()) mockSettings.name(name) else mockSettings.name(property.name)
            if (serializable) mockSettings.serializable()
            if (lenient) mockSettings.lenient()
            mockSettings.defaultAnswer(answer)
        }
        return mockSettings
    }


    // property.annotations.size= 0
    // property.annotations.find { it is Mock }
    // ??   -> check
    private fun toMockAnnotation(property: KProperty<*>): Mock = property.allAnnotations().find { it is Mock } as Mock
}
