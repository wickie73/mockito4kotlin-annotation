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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.internal.util.MockUtil.isSpy
import org.mockito4kotlin.annotation.KMockitoAnnotations
import kotlin.reflect.full.createInstance

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.SpyInjectionTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class SpyInjectionTest {

    @Spy
    internal var spy: List<Any> = mutableListOf()

    @Spy
    private lateinit var lateinitSpy: List<Any>

    @InjectMocks
    private var hasSpy = HasSpy()

    internal class HasSpy {
        var spy: List<Any>? = null
        var lateinitSpy: List<Any>? = null
    }

    @BeforeEach
    fun setUp() {
        try {
            hasSpy = HasSpy::class.createInstance()
        } catch (e: IllegalArgumentException) {
            hasSpy = HasSpy()
        }
        KMockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("spies should be initialized")
    fun shouldInitSpies() {
        assertTrue(isSpy(hasSpy.lateinitSpy))
        assertTrue(isSpy(hasSpy.spy))
    }

    @Test
    @DisplayName("spies of injected class (HasSpy) and in this test class should be equals")
    fun spiesShouldBeEquals() {
        assertEquals(hasSpy.lateinitSpy, this.lateinitSpy)
        assertEquals(hasSpy.spy, this.spy)
    }
}
