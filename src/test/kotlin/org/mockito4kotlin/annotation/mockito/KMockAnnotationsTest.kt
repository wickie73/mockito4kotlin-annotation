/*
 *
 * The MIT License
 *
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

package org.mockito4kotlin.annotation.mockito

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.Mockito
import org.mockito4kotlin.annotation.KMock
import org.mockito4kotlin.annotation.KMockitoAnnotations

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.AnnotationsTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class KMockAnnotationsTest {

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    annotation class NotAMock

    @KMock
    internal lateinit var list: MutableList<*>

    @KMock
    internal var map: MutableMap<Int, String> = mutableMapOf()

    @NotAMock
    internal var notAMock: Set<*>? = null

    @KMock
    internal lateinit var listTwo: MutableList<*>

    @BeforeEach
    fun setUp() {
        KMockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("should init mocks")
    fun shouldInitMocks() {
        list.clear()
        map.clear()
        listTwo.clear()

        verify(list).clear()
        verify(map).clear()
        verify(listTwo).clear()
    }

    @Test
    @DisplayName("should not init mocks")
    fun shouldNotInitMocks() {
        assertNull(notAMock)
    }

    @Test
    @DisplayName("should look for annotated Mocks in SuperClasses")
    fun shouldLookForAnnotatedMocksInSuperClasses() {
        val sub = Sub()

        KMockitoAnnotations.initMocks(sub)

        assertNotNull(sub.mock)
        assertNotNull(sub.baseMock)
        assertNotNull(sub.superBaseMock)
    }

    @KMock(answer = Answers.RETURNS_MOCKS, name = "i have a name")
    internal var namedAndReturningMocks: Methods4MockTests? = null

    @KMock(answer = Answers.RETURNS_DEFAULTS)
    internal var returningDefaults: Methods4MockTests? = null

    @KMock(extraInterfaces = [List::class])
    internal var hasExtraInterfaces: Methods4MockTests? = null

    @KMock
    internal var noExtraConfig: Methods4MockTests? = null

    @KMock(stubOnly = true)
    internal var stubOnly: Methods4MockTests? = null

    @Test
    @DisplayName("should init Mocks with given Settings")
    fun shouldInitMocksWithGivenSettings() {
        assertNotNull(namedAndReturningMocks)
        assertEquals("i have a name", namedAndReturningMocks.toString())
        assertNotNull(namedAndReturningMocks?.iMethodsReturningMethod())

        assertNotNull(returningDefaults)
        assertEquals("returningDefaults", returningDefaults.toString())
        assertEquals(0, returningDefaults?.intReturningMethod())

        assertTrue(hasExtraInterfaces is List<*>)
        assertTrue(Mockito.mockingDetails(stubOnly).mockCreationSettings.isStubOnly)

        assertNotNull(noExtraConfig)
        assertEquals(0, noExtraConfig?.intReturningMethod())
    }

    internal open inner class SuperBase {
        @KMock
        var superBaseMock: Methods4MockTests? = null
    }

    internal open inner class Base : SuperBase() {
        @KMock
        var baseMock: Methods4MockTests? = null
    }

    internal inner class Sub : Base() {
        @KMock
        var mock: Methods4MockTests? = null
    }
}
