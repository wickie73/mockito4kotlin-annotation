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

package org.mockito4kotlin.annotation

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.security.SecureRandom
import kotlin.reflect.KFunction0

class KMockitoAnnotationsInitMocksTest {


    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee)' is called twice")
    fun testInitMocksTwoTimes() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitList[0]).thenReturn("test")

        assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
        assertEquals("test", testee.lateinitList[0])
    }

    @RepeatedTest(value = 15, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee)' is called many times in many treads")
    fun testInitMocksManyTimesInDifferentThreads() {

        fun runMockAnnotationsInitMocks() {
            sleepRandomTime()
            val testee = ClassWithMutableProperties()

            KMockitoAnnotations.initMocks(testee)
            whenever(testee.lateinitList[0]).thenReturn("test")

            assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
            assertEquals("test", testee.lateinitList[0])
        }

        fun fill(size: Int, kFunction0: KFunction0<Unit>): List<KFunction0<Unit>> {
            val result = mutableListOf<KFunction0<Unit>>()
            for (i in 1..size) result.add(kFunction0)
            return result.toList()
        }

        val list = fill(200, ::runMockAnnotationsInitMocks)

        list.parallelStream().forEach(KFunction0<Unit>::invoke)
    }

    @RepeatedTest(value = 5, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee)' is called many times in many coroutines")
    fun testInitMocksManyTimesWithCoroutines() = runBlocking {

        fun runMockAnnotationsInitMocks() {
            sleepRandomTime()
            val testee = ClassWithMutableProperties()

            KMockitoAnnotations.initMocks(testee)
            whenever(testee.lateinitList[0]).thenReturn("test")

            assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
            assertEquals("test", testee.lateinitList[0])
        }

        (1..200).map {
            launch {
                val delayTime = SecureRandom().nextInt(10).toLong()
                delay(delayTime)
                runMockAnnotationsInitMocks()
            }
        }.joinAll()
    }

    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee1 / testee2)' is called")
    fun testInitMocksTwoTimesWithDifferentInstances() {
        val testee1 = ClassWithMutableProperties()
        val testee2 = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee1)
        KMockitoAnnotations.initMocks(testee2)
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
