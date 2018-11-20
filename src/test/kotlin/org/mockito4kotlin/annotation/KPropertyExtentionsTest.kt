/*
 *
 * The MIT License
 *
 *   Copyright (c) 2017-2018 Wilhelm Schulenburg
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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mock
import kotlin.reflect.full.memberProperties

class KPropertyExtentionsTest {

    @Test
    fun testAllAnnotationsWithJavaAnnotation() {
        val anyWithMocks = TestClassWithJavaAnnotation()
        val properties = anyWithMocks::class.memberProperties

        val result = properties.elementAtOrNull(0)?.allAnnotations()

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.get(0) is Mock)
    }

    @Test
    fun testAllAnnotationsWithKotlinAnnotation() {
        val anyWithMocks = TestClassWithKotlinAnnotation()
        val properties = anyWithMocks::class.memberProperties

        val result = properties.elementAtOrNull(0)?.allAnnotations()

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.get(0) is KCaptor)
    }

    @Test
    fun testAllAnnotationsWithKotlinAndJavaAnnotation() {
        val anyWithMocks = TestClassWithKotlinAndJavaAnnotations()
        val properties = anyWithMocks::class.memberProperties

        val result1 = properties.elementAtOrNull(0)?.allAnnotations()
        val result2 = properties.elementAtOrNull(1)?.allAnnotations()

        assertNotNull(result1)
        assertNotNull(result2)
        assertEquals(1, result1?.size)
        assertEquals(1, result2?.size)
        assertTrue(result1?.get(0) is KCaptor)
        assertTrue(result2?.get(0) is Mock)
    }

    @Test
    fun testAllAnnotationsWithKotlinAnnotationInOneProperty() {
        val anyWithMocks = TestClassWithKotlinAndJavaAnnotationsInOneProperty()
        val properties = anyWithMocks::class.memberProperties

        val result = properties.elementAtOrNull(0)?.allAnnotations()

        assertNotNull(result)
        assertEquals(2, result?.size)
        assertTrue(result?.get(0) is KCaptor)
        assertTrue(result?.get(1) is Mock)
    }

    class TestClassWithJavaAnnotation {
        @Mock
        lateinit var lateinitList: List<String>
    }

    class TestClassWithKotlinAnnotation {
        @KCaptor
        lateinit var lateinitList: List<String>
    }

    class TestClassWithKotlinAndJavaAnnotations {
        @KCaptor
        lateinit var lateinitList1: List<String>
        @Mock
        lateinit var lateinitList2: List<String>
    }

    class TestClassWithKotlinAndJavaAnnotationsInOneProperty {
        @KCaptor
        @Mock
        lateinit var lateinitList: List<String>
    }


}
