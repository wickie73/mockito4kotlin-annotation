/*
 * The MIT License
 *
 *   Copyright (c) 2017-2018 Wilhelm Schulenburg
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

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mockingDetails
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito4kotlin.annotation.Mock
import org.mockito4kotlin.annotation.MockAnnotations
import org.mockito4kotlin.annotation.Spy

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.DefaultMockingDetailsTest] and
 * ensures that [MockAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class DefaultMockingDetailsTest {

    @Mock
    private lateinit var foo: Foo
    @Mock
    private lateinit var bar: Bar
    @Mock
    private lateinit var mock: Methods4MockTests
    @Spy
    private lateinit var gork: Gork

    @BeforeEach
    fun setUp() {
        MockAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("should provide original mock")
    fun testOriginalMock() {
        //expect
        assertEquals(mockingDetails(foo).mock, foo)
        assertEquals(mockingDetails(null).mock, null)
    }

    @Test
    @DisplayName("should know spy")
    fun testKnownSpy() {
        assertTrue(mockingDetails(gork).isMock)
        assertTrue(mockingDetails(gork).isSpy)
    }

    @Test
    @DisplayName("should know spy")
    fun testKnownMock() {
        assertTrue(mockingDetails(foo).isMock)
        assertFalse(mockingDetails(foo).isSpy)
    }

    @Test
    @DisplayName("should handle non mocks")
    fun testNonMocks() {
        assertFalse(mockingDetails("non mock").isSpy)
        assertFalse(mockingDetails("non mock").isMock)

        assertFalse(mockingDetails(null).isSpy)
        assertFalse(mockingDetails(null).isMock)
    }

    @Test
    @DisplayName("should check that a spy is also a mock")
    fun testSpyIsAlsoAMock() {
        assertEquals(true, mockingDetails(gork).isMock)
    }

    @Test
    @DisplayName("provides_invocations")
    fun testProvideInvocation() {
        //when
        mock.simpleMethod(10)
        mock.otherMethod()

        //then
        assertEquals(0, mockingDetails(foo).invocations.size)
        assertEquals("[mock.simpleMethod(10);, mock.otherMethod();]", mockingDetails(mock).invocations.toString())
    }

    @Test
    @DisplayName("manipulating invocations is safe")
    fun testManipulatingInvocations() {
        mock.simpleMethod()

        //when we manipulate the invocations
        mockingDetails(mock).invocations.clear()

        //then we didn't actually changed the invocations
        assertEquals(1, mockingDetails(mock).invocations.size)
    }

    @Test
    @DisplayName("provides mock creation settings")
    fun testMockCreationSettings() {
        //smoke test some creation settings
        assertEquals(Foo::class.java, mockingDetails(foo).mockCreationSettings.typeToMock)
        assertEquals(Bar::class.java, mockingDetails(bar).mockCreationSettings.typeToMock)
        assertEquals(0, mockingDetails(mock).mockCreationSettings.extraInterfaces.size)
    }

    @Test
    @DisplayName("fails when getting creation settings for incorrect input")
    fun testMockWithNullWithMockCreationSettings() {
        val result = assertThrows(NotAMockException::class.java, {
            mockingDetails(null).mockCreationSettings
        })

        assertNotNull(result.message)
    }

    @Test
    @DisplayName("fails when getting invocations when null")
    fun testMockWithNullWithInvocations() {
        //when
        val result = assertThrows(NotAMockException::class.java, {
            mockingDetails(null).invocations
        })

        //then
        assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is null!", result.message)
    }

    @Test
    @DisplayName("fails when getting invocations when not mock")
    fun testMockWithAnyWithInvocations() {
        //when
        val result = assertThrows(NotAMockException::class.java, {
            mockingDetails(Any()).invocations
        })

        //then
        assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", result.message)

    }

    @Test
    @DisplayName("fails when getting stubbings from non mock")
    fun testMockWithAnyWithStubbing() {
        //when
        val result = assertThrows(NotAMockException::class.java, {
            mockingDetails(Any()).getStubbings()
        })

        //then
        assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", result.message)
    }

    @Test
    @DisplayName("mock with no stubbings")
    fun testMockWithNoStubbings() {
        assertTrue(mockingDetails(mock).stubbings.isEmpty())
    }

    @Test
    @DisplayName("provides stubbings of mock in declaration order")
    fun testMockInDeclarationOrder() {
        whenever(mock.simpleMethod(1)).thenReturn("1")
        whenever(mock.otherMethod()).thenReturn("2")

        //when
        val stubbings = mockingDetails(mock).stubbings

        //then
        assertEquals(2, stubbings.size)
        assertEquals("[mock.simpleMethod(1); stubbed with: [Returns: 1], mock.otherMethod(); stubbed with: [Returns: 2]]", stubbings.toString())
    }

    @Test
    @DisplayName("manipulating stubbings xplicitly is safe")
    fun testManipulatingStubbingsExplicitlyIsSafe() {
        whenever(mock.simpleMethod(1)).thenReturn("1")

        //when somebody manipulates stubbings directly
        mockingDetails(mock).stubbings.clear()

        //then it does not affect stubbings of the mock
        assertEquals(1, mockingDetails(mock).stubbings.size)
    }

    @Test
    @DisplayName("prints invocations")
    fun testPrintsInvocations() {
        //given
        given(mock.simpleMethod("different arg")).willReturn("foo")
        mock.simpleMethod("arg")

        //when
        val log = Mockito.mockingDetails(mock).printInvocations()

        //then
        assertThat(log).containsIgnoringCase("unused")
        assertThat(log).containsIgnoringCase("mock.simpleMethod(\"arg\")")
        assertThat(log).containsIgnoringCase("mock.simpleMethod(\"different arg\")")
    }

    @Test
    @DisplayName("fails when printin invocations from non mock")
    fun testPrintingInvocationsOfNonMock() {
        //when
        val result = assertThrows(NotAMockException::class.java, {
            mockingDetails(Any()).printInvocations()
        })

        //then
        assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", result.message)

    }


    open inner class Foo
    interface Bar
    interface Gork
}
