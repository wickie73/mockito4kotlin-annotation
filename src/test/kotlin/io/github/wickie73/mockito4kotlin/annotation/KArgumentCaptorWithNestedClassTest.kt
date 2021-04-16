/*
 *
 * The MIT License
 *
 *   Copyright (c) 2017 Wilhelm Schulenburg
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

import org.junit.jupiter.api.AfterEach
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock

class KArgumentCaptorWithNestedClassTest {

    private lateinit var testCloseable: AutoCloseable

    @KCaptor
    lateinit var captor: KArgumentCaptor<ClassWithPerson.SubPerson>
    @Mock
    lateinit var personDAO: PersonDAO

    @BeforeEach
    fun setUp() {
        testCloseable = KMockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun releaseMocks() {
        if (this::testCloseable.isInitialized) {
            testCloseable.close()
        }
    }

    @Test
    @DisplayName("should capture the person when class is a nested class of another")
    fun testKCaptorWithNestedClass() {
        val person = createPerson()

        personDAO.saveWithNonNullableArgument(person)

        verify(personDAO).saveWithNonNullableArgument(captor.capture())
        assertEquals(person, captor.firstValue)
    }

    @Test
    @DisplayName("should capture the person when class is a nested class of another with Nullable argument")
    fun testKCaptorWithNestedClassWithNullableArgument() {
        val person = null

        personDAO.saveWithNullableArgument(person)

        verify(personDAO).saveWithNullableArgument(captor.capture())
        assertEquals(person, captor.firstValue)
    }

    private fun createPerson(person: Person = ClassWithPerson.SubPerson()) = person.apply {
        name = "John"
    }
}
