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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.exceptions.base.MockitoException
import io.github.wickie73.mockito4kotlin.annotation.KMockitoAnnotations

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.WrongSetOfAnnotationsTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class WrongSetOfAnnotationsTest {

    @Test
    @DisplayName("should not allow Mock and Spy")
    fun testWithMockAndSpyAnnotation() {
        val result = assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(object : Any() {
                @Mock
                @Spy
                internal var mock: List<*>? = null
            })
        }

        assertThat(result)
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
            .hasMessageContaining("Mock")
            .hasMessageContaining("Spy")
    }


    @Test
    @DisplayName("should not allow Spy and InjectMocks on interfaces")
    fun testWithSpyAndInjectMocksAnnotationOnInterfaces() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(object : Any() {
                @InjectMocks
                @Spy
                internal var mock: List<*>? = null
            })
        })
        assertThat(result)
            .hasMessageContaining("Cannot instantiate @InjectMocks field named")
            .hasMessageContaining("'List' is an interface")
    }

    @Test
    @DisplayName("should allow Spy and InjectMocks")
    fun testWithSpyAndInjectMocksAnnotation() {
        KMockitoAnnotations.initMocks(object : Any() {
            @InjectMocks
            @Spy
            internal var mock: WithDependency? = null
        })
    }

    internal open class WithDependency {
        var list: List<*>? = null
    }

    @Test
    @DisplayName("should not allow Mock and InjectMocks")
    fun testWithMockAndInjectMocksAnnotation() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(object : Any() {
                @InjectMocks
                @Mock
                internal var mock: List<*>? = null
            })
        })
        assertThat(result)
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
            .hasMessageContaining("InjectMocks")
            .hasMessageContaining("Mock")
    }

    @Test
    @DisplayName("should not allow Captor and Mock")
    fun testWithMockAndCaptorAnnotation() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(object : Any() {
                @Mock
                @Captor
                internal var captor: ArgumentCaptor<*>? = null
            })
        })
        assertThat(result)
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
            .hasMessageContaining("Captor")
            .hasMessageContaining("Mock")
    }

    @Test
    @DisplayName("should not allow Captor and Spy")
    fun testWithSpyAndCaptorAnnotation() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(object : Any() {
                @Spy
                @Captor
                internal var captor: ArgumentCaptor<*>? = null
            })
        })
        assertThat(result)
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
            .hasMessageContaining("Captor")
            .hasMessageContaining("Spy")
    }

    @Test
    @DisplayName("should not allow Captor and InjectMocks")
    fun testWithCaptorAndInjectsMocksAnnotation() {
        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(object : Any() {
                @InjectMocks
                @Captor
                internal var captor: ArgumentCaptor<*>? = null
            })
        })
        assertThat(result)
            .hasMessageContaining("multiple Mockito4Kotlin annotations")
            .hasMessageContaining("Captor")
            .hasMessageContaining("InjectMocks")
    }

}
