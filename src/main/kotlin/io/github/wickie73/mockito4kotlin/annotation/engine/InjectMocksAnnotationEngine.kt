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

import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyExceptionAfterCreateInstanceOfInjectMocks
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyImmutableInjectMocksProperty
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyInjectMocksProperty
import io.github.wickie73.mockito4kotlin.annotation.engine.MockAnnotationsVerifier.verifyInstanceOfInjectMocksIsNotNull
import io.github.wickie73.mockito4kotlin.annotation.kClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class InjectMocksAnnotationEngine : AbstractAnnotationEngine() {

    override fun process(anyInstanceWithMocks: Any, property: KProperty<*>) {
        property.isAccessible = true
        verifyInjectMocksProperty(property)

        val instanceToBeMocked = property.getter.call(anyInstanceWithMocks) ?: createAndAssignInstanceOf(property, anyInstanceWithMocks)
        val mockCandidates = instanceToBeMocked::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
        val mockPropertyMatcher = MockPropertyMatcher(mockedAssignedProperties, mockCandidates)
        mockCandidates.forEach { mockCandidate ->
            mockedAssignedProperties.properties().filter { mockProperty -> mockPropertyMatcher.match(mockProperty, mockCandidate) }
                .forEach { property -> mockCandidate.setter.call(instanceToBeMocked, mockedAssignedProperties[property]) }
        }
    }

    private fun createAndAssignInstanceOf(property: KProperty<*>, anyWithMocks: Any): Any {
        val instance = createInstanceOfInjectMocksProperty(property)
        verifyImmutableInjectMocksProperty(property)
        verifyInstanceOfInjectMocksIsNotNull(instance, property)
        assignInstanceToProperty(property as KMutableProperty<*>, anyWithMocks, instance as Any)
        return instance
    }

    private fun createInstanceOfInjectMocksProperty(property: KProperty<*>) =
        try {
            property.kClass?.createInstance()
        } catch (e: Exception) {
            verifyExceptionAfterCreateInstanceOfInjectMocks(e, property)
        }

    private fun assignInstanceToProperty(property: KMutableProperty<*>, anyWithMocks: Any, instance: Any) =
        property.setter.call(anyWithMocks, instance )

}
