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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.mockito.ArgumentCaptor
import org.mockito.exceptions.base.MockitoException

class ArgumentCaptorTrapWithInnerClassTest {

    @Captor
    lateinit var captor: ArgumentCaptor<ClassWithPerson.InnerSubPerson>
    @Mock
    lateinit var personDAO: PersonDAO

    @BeforeEach
    fun setUp() {
        MockAnnotations.initMocks(this)
    }

    @Disabled("Does not work with Mockito")
    @DisplayName("should capture the person when class is a inner class of another")
    fun testTrapWithInnerClass() {
        val person = createPerson(ClassWithPerson().InnerSubPerson())
        personDAO.save(person)

        val result = Assertions.assertThrows(MockitoException::class.java, {
            verify(personDAO).save(captor.trap())
        })

        assertThat(result).hasMessageContaining("cause it has no empty, accessible constructor to create an instance!")
    }

    private fun createPerson(person: Person = ClassWithPerson().InnerSubPerson()) = person.apply {
        name = "John"
    }
}
