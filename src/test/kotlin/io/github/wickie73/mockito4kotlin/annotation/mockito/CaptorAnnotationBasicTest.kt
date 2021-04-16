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

package io.github.wickie73.mockito4kotlin.annotation.mockito

import org.mockito.kotlin.firstValue
import org.mockito.kotlin.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import io.github.wickie73.mockito4kotlin.annotation.KMockitoAnnotations
import org.junit.jupiter.api.AfterEach

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.CaptorAnnotationBasicTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class CaptorAnnotationBasicTest {

    inner open class Person(val name: String, val surname: String)

    interface PeopleRepository {
        fun save(capture: Person?)
    }

    private lateinit var testCloseable: AutoCloseable

    @Mock
    internal lateinit var peopleRepository: PeopleRepository

    private fun createPerson(name: String, surname: String) {
        peopleRepository.save(Person(name, surname))
    }

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
    @DisplayName("Should use captor in ordinary way")
    fun testArgumentCaptor() {
        //when
        createPerson("Wes", "Williams")

        //then
        val captor = ArgumentCaptor.forClass(Person::class.java)

        verify(peopleRepository).save(captor.capture())
        assertEquals("Wes", captor.firstValue.name)
        assertEquals("Williams", captor.firstValue.surname)
    }

    @Captor
    internal lateinit var captor: ArgumentCaptor<Person>

    @Test
    @DisplayName("Should use annotated captor")
    fun testAnnotatedCaptor() {
        //when
        createPerson("Wes", "Williams")

        //then
        verify(peopleRepository).save(captor.capture())
        assertEquals("Wes", captor.value.name)
        assertEquals("Williams", captor.value.surname)
    }

    @Captor
    internal lateinit var genericLessCaptor: ArgumentCaptor<*>

    @Test
    @DisplayName("Should use genericless annotated captor")
    fun testGenericlessAnnotatedCaptor() {
        //when
        val name = "Wes"
        val surname = "Williams"
        createPerson(name, surname)

        //then
        verify(peopleRepository).save(genericLessCaptor.capture() as Person?)
        assertEquals(name, (genericLessCaptor.value as Person).name)
        assertEquals(surname, (genericLessCaptor.value as Person).surname)
    }

    @Captor
    internal lateinit var genericListCaptor: ArgumentCaptor<List<String>>

    @Mock
    internal lateinit var mock: Methods4MockTests

    @Test
    @DisplayName("Should capture generic list")
    fun testCaptureOfGenericList() {
        //given
        val list = listOf<String>()
        mock.listNullableArgMethod(list)

        //when
        verify(mock).listNullableArgMethod(genericListCaptor.capture())

        //then
        assertSame(list, genericListCaptor.value)
    }

}
