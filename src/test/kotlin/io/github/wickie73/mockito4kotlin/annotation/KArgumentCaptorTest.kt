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

package io.github.wickie73.mockito4kotlin.annotation

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock

class KArgumentCaptorTest {
    @KCaptor
    lateinit var captor: KArgumentCaptor<Address>
    @Mock
    lateinit var addressDAO: AddressDAO

    @BeforeEach
    fun setUp() {
        KMockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("should not report an error if captor.capture() is called because it is non nullable argument")
    fun testMockitoKCapture() {
        val address = createAddress()

        addressDAO.saveWithNonNullableArgument(address)

        verify(addressDAO).saveWithNonNullableArgument(captor.capture())
        assertEquals(address, captor.firstValue)
    }

    @Test
    @DisplayName("should not report an error if captor.capture() is called because it is nullable argument")
    fun testMockitoKCaptureWithNullableCaptureClass() {
        val address: Address? = createAddress()

        addressDAO.registerWithNullableArgument(address)

        verify(addressDAO).registerWithNullableArgument(captor.capture())
        assertEquals(address, captor.firstValue)
    }

    @Test
    @DisplayName("should not report an error if captor.capture() is called because it is null")
    fun testMockitoKCaptureWithNull() {
        val address: Address? = null

        addressDAO.registerWithNullableArgument(address)

        verify(addressDAO).registerWithNullableArgument(captor.capture())
        assertEquals(address, captor.firstValue)
    }

    private fun createAddress(address: Address = Address()) = address.apply {
        street = "Abbey Road 73"
        zip = "NW8"
        city = "London"
        country = "UK"
    }

}
