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

import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import java.security.SecureRandom
import kotlin.reflect.KFunction0

class KMockitoAnnotationsOpenMocksTest {

    private lateinit var testCloseable: AutoCloseable
    private lateinit var secondCloseable: AutoCloseable

    @AfterEach
    fun releaseMocks() {
        if (this::testCloseable.isInitialized) {
            testCloseable.close()
        }
        if (this::secondCloseable.isInitialized) {
            secondCloseable.close()
        }
    }

    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.openMocks(testee)' is called twice")
    fun testOpenMocksTwoTimes() {
        val testee = ClassWithMutableProperties()

        testCloseable = KMockitoAnnotations.openMocks(testee)
        secondCloseable = KMockitoAnnotations.openMocks(testee)
        whenever(testee.lateinitList[0]).thenReturn("test")

        assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
        assertEquals("test", testee.lateinitList[0])
    }

    @RepeatedTest(value = 15, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.openMocks(testee)' is called many times in many treads")
    fun testOpenMocksManyTimesInDifferentThreads() {

        fun runMockAnnotationsOpenMocks() {
            sleepRandomTime()
            val testee = ClassWithMutableProperties()

            try {
                testCloseable = KMockitoAnnotations.openMocks(testee)
                whenever(testee.lateinitList[0]).thenReturn("test")

                assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
                assertEquals("test", testee.lateinitList[0])
            } finally {
                releaseMocks()
            }
        }

        fun fill(size: Int, kFunction0: KFunction0<Unit>): List<KFunction0<Unit>> {
            val result = mutableListOf<KFunction0<Unit>>()
            for (i in 1..size) result.add(kFunction0)
            return result.toList()
        }

        val list = fill(200, ::runMockAnnotationsOpenMocks)

        list.parallelStream().forEach(KFunction0<Unit>::invoke)
    }

    @RepeatedTest(value = 5, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.openMocks(testee)' is called many times in many coroutines")
    fun testOpenMocksManyTimesWithCoroutines() = runBlocking {

        fun runMockAnnotationsOpenMocks() {
            sleepRandomTime()
            val testee = ClassWithMutableProperties()

            try {
                testCloseable = KMockitoAnnotations.openMocks(testee)
                whenever(testee.lateinitList[0]).thenReturn("test")

                assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
                assertEquals("test", testee.lateinitList[0])
            } finally {
                releaseMocks()
            }
        }

        (1..200).map {
            launch {
                val delayTime = SecureRandom().nextInt(10).toLong()
                delay(delayTime)
                runMockAnnotationsOpenMocks()
            }
        }.joinAll()
    }

    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.openMocks(testee1 / testee2)' is called")
    fun testOpenMocksTwoTimesWithDifferentInstances() {
        val testee1 = ClassWithMutableProperties()
        val testee2 = ClassWithMutableProperties()

        testCloseable = KMockitoAnnotations.openMocks(testee1)
        secondCloseable = KMockitoAnnotations.openMocks(testee2)
        whenever(testee1.lateinitList[0]).thenReturn("test1")
        whenever(testee2.lateinitList[0]).thenReturn("test2")

        assertTrue(Mockito.mockingDetails(testee1.lateinitList).isMock)
        assertTrue(Mockito.mockingDetails(testee2.lateinitList).isMock)
        assertEquals("test1", testee1.lateinitList[0])
        assertEquals("test2", testee2.lateinitList[0])
    }

    private fun sleepRandomTime() {
        val sleepTime = SecureRandom().nextInt(10).toLong()
        Thread.sleep(sleepTime)
    }
}
