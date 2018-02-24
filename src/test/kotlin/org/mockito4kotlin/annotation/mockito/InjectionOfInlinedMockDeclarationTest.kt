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

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito4kotlin.annotation.InjectMocks
import org.mockito4kotlin.annotation.Mock
import org.mockito4kotlin.annotation.MockAnnotations

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.InjectionOfInlinedMockDeclarationTest] and
 * ensures that [MockAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class InjectionOfInlinedMockDeclarationTest {

    @InjectMocks
    private lateinit var receiver: Receiver
    @InjectMocks
    private var spiedReceiver = spy<Receiver>(Receiver())

    private val oldAntenna = mock<Antenna>(Antenna::class.java)
    @Mock
    private lateinit var newAntenna: Antenna
    private val satelliteAntenna = mock<Antenna>(Antenna::class.java)
    private val antenna = mock<Antenna>(Antenna::class.java, "dvbtAntenna")
    private val tuner = spy<Tuner>(Tuner())

    @BeforeEach
    fun setUp() {
        MockAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("mock declared fields shall be injected too")
    fun testMockDeclaredFieldsShallBeInjectedToo() {
        assertNotNull(receiver.oldAntenna)
        assertNotNull(receiver.newAntenna)
        assertNotNull(receiver.satelliteAntenna)
        assertNotNull(receiver.dvbtAntenna)
        assertNotNull(receiver.tuner)
    }

    @Test
    @DisplayName("unnamed mocks should be resolved with their field names")
    fun testResolveUnnamedMocksWithTheirFieldNames() {
        assertSame(oldAntenna, receiver.oldAntenna)
        assertSame(newAntenna, receiver.newAntenna)
        assertSame(satelliteAntenna, receiver.satelliteAntenna)
    }

    @Test
    @DisplayName("named mocks should be resolved with their name")
    fun testResolveNamedMocksWithTheirName() {
        assertSame(antenna, receiver.dvbtAntenna)
    }


    @Test
    @DisplayName("inject mocks even in declared spy")
    fun testInjectMocksWithDeclaredSpy() {
        assertNotNull(spiedReceiver.oldAntenna)
        assertNotNull(spiedReceiver.tuner)
    }

    internal open class Receiver {
        var oldAntenna: Antenna? = null
        var newAntenna: Antenna? = null
        var satelliteAntenna: Antenna? = null
        var dvbtAntenna: Antenna? = null
        var tuner: Tuner? = null

        fun tune(): Boolean {
            return true
        }
    }

    internal open class Antenna
    internal open class Tuner
}
