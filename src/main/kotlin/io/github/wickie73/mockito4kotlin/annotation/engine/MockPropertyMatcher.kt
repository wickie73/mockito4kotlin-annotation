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

import org.mockito.Mockito.mockingDetails
import org.mockito.mock.MockName
import io.github.wickie73.mockito4kotlin.annotation.MockPropertyCollector
import io.github.wickie73.mockito4kotlin.annotation.hasTypeArguments
import io.github.wickie73.mockito4kotlin.annotation.type
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal class MockPropertyMatcher(mockPropertyCollector: MockPropertyCollector, mockCandidates: Collection<KProperty<*>>) {
    private val mockTypeToNameMap = mockPropertyCollector.properties().groupBy({ it.type }, { it.name })
    private val mockCandidateTypeToNameMap = mockCandidates.groupBy({ it.type }, { it.name })
    private val mockName: (KProperty<*>) -> MockName = {
        mockingDetails(mockPropertyCollector[it]).mockCreationSettings.mockName
    }

    internal fun match(mockProperty: KProperty<*>, mockCandidate: KProperty<*>) =
        matchTypes(mockProperty, mockCandidate) && matchNames(mockProperty, mockCandidate)

    private fun matchTypes(mockProperty: KProperty<*>, mockCandidate: KProperty<*>) =
        matchClassifier(mockProperty, mockCandidate) && matchTypeArguments(mockCandidate, mockProperty)

    private fun matchClassifier(mockProperty: KProperty<*>, mockCandidate: KProperty<*>) =
        mockProperty.type.classifier == mockCandidate.type.classifier

    private fun matchTypeArguments(mockCandidate: KProperty<*>, mockProperty: KProperty<*>): Boolean {
        var result = true
        if (mockCandidate.hasTypeArguments() && mockProperty.hasTypeArguments()) {
            result = when (mockCandidate.type.arguments.first()) {
                KTypeProjection.STAR -> true
                mockProperty.type.arguments.first() -> true
                else -> false
            }
        }
        return result
    }

    private fun matchNames(mockProperty: KProperty<*>, mockCandidate: KProperty<*>): Boolean {
        var result = true
        if (hasMockPropertyAmbiguousType(mockProperty) || hasCandidateAmbiguousType(mockCandidate)) {
            result = mockNameof(mockProperty) == mockCandidate.name
        }
        return result
    }

    private fun mockNameof(property: KProperty<*>) =
        if (mockName(property).isDefault) property.name else mockName(property).toString()

    private fun hasMockPropertyAmbiguousType(mockProperty: KProperty<*>) =
        isAmbiguousType(mockTypeToNameMap, mockProperty)

    private fun hasCandidateAmbiguousType(mockCandidate: KProperty<*>) =
        isAmbiguousType(mockCandidateTypeToNameMap, mockCandidate)

    private fun isAmbiguousType(typeToNameMap: Map<KType, List<String>>, property: KProperty<*>) =
        typeToNameMap[property.type]?.size ?: 0 > 1
}
