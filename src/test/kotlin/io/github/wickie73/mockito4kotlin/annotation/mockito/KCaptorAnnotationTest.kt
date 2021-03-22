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

package io.github.wickie73.mockito4kotlin.annotation.mockito

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.exceptions.base.MockitoException
import io.github.wickie73.mockito4kotlin.annotation.KCaptor
import io.github.wickie73.mockito4kotlin.annotation.KMockitoAnnotations
import java.util.*

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.CaptorAnnotationTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class KCaptorAnnotationTest {

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    annotation class NotAMock

    @KCaptor
    internal var finalCaptor = KArgumentCaptor(ArgumentCaptor.forClass(String::class.java), String::class)

    @KCaptor
    internal var finalKcaptor = argumentCaptor<String>()

    @KCaptor
    internal var genericsCaptor: KArgumentCaptor<List<List<String>>>? = null

    @KCaptor
    internal var nonGenericCaptorIsAllowed: KArgumentCaptor<*>? = null

    @Mock
    internal var mockInterface: MockInterface? = null

    @NotAMock
    internal var notAMock: Set<*>? = null

    interface MockInterface {
        fun testMe(simple: String?, genericList: List<List<String>>?)
    }

    @BeforeEach
    fun setUp() {
        KMockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("test normal usages")
    fun testNormalUsage() {
        // check if assigned correctly
        assertNotNull(finalCaptor)
        assertNotNull(genericsCaptor)
        assertNotNull(nonGenericCaptorIsAllowed)
        assertNull(notAMock)

        // use captors in the field to be sure they are cool
        val argForFinalCaptor = "Hello"
        val argForGenericsCaptor = ArrayList<List<String>>()

        mockInterface!!.testMe(argForFinalCaptor, argForGenericsCaptor)

        verify(mockInterface)!!.testMe(finalCaptor.capture(), genericsCaptor!!.capture())

        assertEquals(argForFinalCaptor, finalCaptor.firstValue)
        assertEquals(argForGenericsCaptor, genericsCaptor!!.firstValue)
    }

    @Test
    @DisplayName("test normal usages")
    fun testNormalUsageWithArgumentCaptorMethod() {
        // check if assigned correctly
        assertNotNull(finalKcaptor)
        assertNotNull(genericsCaptor)
        assertNotNull(nonGenericCaptorIsAllowed)
        assertNull(notAMock)

        // use captors in the field to be sure they are cool
        val argForFinalCaptor = "Hello"
        val argForGenericsCaptor = ArrayList<List<String>>()

        mockInterface!!.testMe(argForFinalCaptor, argForGenericsCaptor)

        verify(mockInterface)!!.testMe(finalKcaptor.capture(), genericsCaptor!!.capture())

        assertEquals(argForFinalCaptor, finalKcaptor.firstValue)
        assertEquals(argForGenericsCaptor, genericsCaptor!!.firstValue)
    }

    class WrongType {
        @KCaptor
        internal var wrongType: List<Map<*, *>>? = null
    }

    @Test
    @DisplayName("Should scream when wrong type for kcaptor")
    fun testWithWrongType() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(WrongType())
        })

        assertThat(result)
            .hasMessageContaining("@KCaptor field must be of the type ${KArgumentCaptor::class.qualifiedName}")
            .hasMessageContaining("Property")
            .hasMessageContaining("wrong type")
    }

    class ToManyAnnotations {
        @KCaptor
        @Mock
        internal var missingGenericsField: KArgumentCaptor<List<*>>? = null
    }

    @Test
    @DisplayName("Should scream when more than one mockito annotation")
    fun testWhenMoreThanOneMockitoAnnotation() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(ToManyAnnotations())
        })

        assertThat(result)
            .hasMessageContaining("missingGenericsField")
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
    }

    @Test
    @DisplayName("Should look for annotated captors in super classes")
    fun testWithAnnotatedKCaptorsInSuperClasses() {
        val sub = Sub()

        KMockitoAnnotations.initMocks(sub)

        assertNotNull(sub.captor)
        assertNotNull(sub.baseCaptor)
        assertNotNull(sub.superBaseCaptor)
    }


    internal open inner class SuperBase {
        @KCaptor
        var superBaseCaptor: KArgumentCaptor<Methods4MockTests>? = null
    }

    internal open inner class Base : SuperBase() {
        @KCaptor
        var baseCaptor: KArgumentCaptor<Methods4MockTests>? = null
    }

    internal inner class Sub : Base() {
        @KCaptor
        var captor: KArgumentCaptor<Methods4MockTests>? = null
    }
}
