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

package org.mockito4kotlin.annotation

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.exceptions.base.MockitoException

class InjectMocksAnnotationTest {

    @Mock
    private lateinit var numbers: List<Number>
    @KMock
    private lateinit var knumbers: List<Number>
    @Spy
    private lateinit var keyStringMap: Map<Number, CharSequence>
    @Captor
    lateinit var captor: ArgumentCaptor<String>
    @InjectMocks
    private val classUnderTest = ClassunderTest()
    @InjectMocks
    private val classUnderTestWithNestedClass = ClassUnderTestWithNestedClass.NestedClass()

    @Test
    @DisplayName("should mock all properties of class 'ClassUnderTest' with @InjectMocks")
    fun testMockOfInjectMocks() {
        KMockitoAnnotations.initMocks(this)

        assertNotNull(classUnderTest.numbers)
        assertNotNull(classUnderTest.knumbers)
        assertTrue(Mockito.mockingDetails(classUnderTest.numbers).isMock)
        assertTrue(Mockito.mockingDetails(classUnderTest.knumbers).isMock)
        assertFalse(Mockito.mockingDetails(classUnderTest.numbers).isSpy)
        assertFalse(Mockito.mockingDetails(classUnderTest.knumbers).isSpy)
        assertThat(classUnderTest.numbers).hasSize(0)
        assertThat(classUnderTest.knumbers).hasSize(0)
        assertEquals(classUnderTest.numbers, numbers)
        assertEquals(classUnderTest.knumbers, knumbers)
    }

    @Test
    @DisplayName("should spy all properties of class 'ClassUnderTest' with @InjectMocks")
    fun testSpyOfInjectMocks() {
        KMockitoAnnotations.initMocks(this)

        assertNotNull(classUnderTest.keyStringMap2)
        assertTrue(Mockito.mockingDetails(classUnderTest.keyStringMap2).isMock)
        assertTrue(Mockito.mockingDetails(classUnderTest.keyStringMap2).isSpy)
        assertThat(classUnderTest.keyStringMap2).hasSize(0)
        assertEquals(classUnderTest.keyStringMap2, keyStringMap)
    }

    @Test
    @DisplayName("should throw an exception if properties which does not match the given mocks are not initialized")
    fun testNotMatchedMocksOfInjectMocks() {
        KMockitoAnnotations.initMocks(this)

        assertThatCode { classUnderTest.keyStringMap1 }
            .isInstanceOf(UninitializedPropertyAccessException::class.java)
            .hasMessageContaining("lateinit property")
            .hasMessageContaining("has not been initialized")
        assertThatCode { classUnderTest.doubleNumbers }
            .isInstanceOf(UninitializedPropertyAccessException::class.java)
            .hasMessageContaining("lateinit property")
            .hasMessageContaining("has not been initialized")
        assertThatCode { classUnderTest.captor }
            .isInstanceOf(UninitializedPropertyAccessException::class.java)
            .hasMessageContaining("lateinit property")
            .hasMessageContaining("has not been initialized")
    }

    @Test
    @DisplayName("should report that mock of properties of inner class 'ClassUnderTestWithInnerClass' with @InjectMocks is not supported")
    fun testMockOfInjectMocksOfInnerClass() {
        val result = assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(KMockitoAnnotations.initMocks(object : Any() {
                @InjectMocks
                private val classUnderTestWithInnerClass = ClassUnderTestWithInnerClass().InnerClass()
            }))
        }

        assertThat(result).hasMessageContaining("is an inner class and has internally no empty constructor")
    }

    @Test
    @DisplayName("should mock all properties of nested class 'ClassUnderTestWithNestedClass' with @InjectMocks")
    fun testMockOfInjectMocksOfNestedClass() {
        KMockitoAnnotations.initMocks(this)

        assertNotNull(classUnderTestWithNestedClass.numbers)
        assertNotNull(classUnderTestWithNestedClass.knumbers)
        assertTrue(Mockito.mockingDetails(classUnderTestWithNestedClass.numbers).isMock)
        assertTrue(Mockito.mockingDetails(classUnderTestWithNestedClass.knumbers).isMock)
        assertFalse(Mockito.mockingDetails(classUnderTestWithNestedClass.numbers).isSpy)
        assertFalse(Mockito.mockingDetails(classUnderTestWithNestedClass.knumbers).isSpy)
        assertThat(classUnderTestWithNestedClass.numbers).hasSize(0)
        assertThat(classUnderTestWithNestedClass.knumbers).hasSize(0)
        assertEquals(classUnderTestWithNestedClass.numbers, numbers)
        assertEquals(classUnderTestWithNestedClass.knumbers, knumbers)
    }

    @Test
    @DisplayName("should spy all properties of class 'ClassUnderTestWithNestedClass' with @InjectMocks")
    fun testSpyOfInjectMocksOfNestedClass() {
        KMockitoAnnotations.initMocks(this)

        assertNotNull(classUnderTestWithNestedClass.keyStringMap)
        assertTrue(Mockito.mockingDetails(classUnderTestWithNestedClass.keyStringMap).isMock)
        assertTrue(Mockito.mockingDetails(classUnderTestWithNestedClass.keyStringMap).isSpy)
        assertThat(classUnderTestWithNestedClass.keyStringMap).hasSize(0)
        assertEquals(classUnderTestWithNestedClass.keyStringMap, keyStringMap)
    }

    class ClassunderTest {
        internal lateinit var numbers: List<Number>
        internal lateinit var knumbers: List<Number>
        internal lateinit var keyStringMap1: Map<Double, String>
        internal lateinit var keyStringMap2: Map<Number, String>
        internal lateinit var doubleNumbers: List<Double>
        internal lateinit var captor: ArgumentCaptor<String>
    }

    class ClassUnderTestWithInnerClass {
        inner class InnerClass {
            internal lateinit var numbers: List<Number>
            internal lateinit var knumbers: List<Number>
            internal lateinit var keyStringMap: Map<Number, String>
        }
    }

    class ClassUnderTestWithNestedClass {
        class NestedClass {
            internal lateinit var numbers: List<Number>
            internal lateinit var knumbers: List<Number>
            internal lateinit var keyStringMap: Map<Number, String>
        }
    }


}


