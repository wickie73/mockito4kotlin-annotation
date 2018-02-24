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

import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentCaptor

class ArgumentCaptorTrapTest {
    @Captor
    lateinit var captor: ArgumentCaptor<Address>
    @Mock
    lateinit var addressDAO: AddressDAO

    @BeforeEach
    fun setUp() {
        MockAnnotations.initMocks(this)
    }

    @Test
    @Disabled("Does not work with all Tests")
    @DisplayName("should report an error if captor.capture() is called because it is null")
    fun testMockitoCapture() {
        addressDAO.save(createAddress())

        val result = Assertions.assertThrows(IllegalStateException::class.java, {
            verify(addressDAO).save(captor.capture())
        })

        assertThat(result).hasMessageContaining("captor.capture() must not be null")
    }

    @Test
    @DisplayName("should report an error if captor.capture() is called because it is null")
    fun testMockitoCaptureWithNullableCaptureClass() {
        val address: Address? = createAddress()
        addressDAO.register(address)

        verify(addressDAO).register(captor.capture())

        assertEquals(address, captor.value)
    }

    @Test
    @DisplayName("should capture the address with #trap()")
    fun testTrap() {
        val address = createAddress()
        addressDAO.save(address)

        verify(addressDAO).save(captor.trap())

        assertEquals(address, captor.value)
    }

    @Test
    @DisplayName("should capture the address with #trap(Address)")
    fun testTrapWithInstance() {
        val address = createAddress()
        addressDAO.save(address)

        verify(addressDAO).save(captor.trap(address))

        assertEquals(address, captor.value)
    }

    private fun createAddress(address: Address = Address()) = address.apply {
        street = "Abbey Road 73"
        zip = "NW8"
        city = "London"
        country = "UK"
    }

}
