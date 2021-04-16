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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.exceptions.base.MockitoException
import io.github.wickie73.mockito4kotlin.annotation.KMockitoAnnotations
import org.junit.jupiter.api.AfterEach
import java.util.*

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.MockInjectionUsingSetterOrPropertyTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class MockInjectionUsingSetterOrPropertyTest {

    private lateinit var testCloseable: AutoCloseable

    private val superUnderTestWithoutInjection = SuperUnderTesting()

    @InjectMocks
    private val superUnderTest = SuperUnderTesting()
    @InjectMocks
    private val baseUnderTest = BaseUnderTesting()
    @InjectMocks
    private val subUnderTest = SubUnderTesting()
    @InjectMocks
    private val otherBaseUnderTest = OtherBaseUnderTesting()
    @InjectMocks
    private val hasTwoFieldsWithSameType = HasTwoFieldsWithSameType()

    private val baseUnderTestingInstance = BaseUnderTesting()
    @InjectMocks
    private val initializedBase = baseUnderTestingInstance
    @InjectMocks
    private lateinit var notInitializedBase: BaseUnderTesting

    @Spy
    @InjectMocks
    private var initializedSpy = SuperUnderTesting()
    @Spy
    @InjectMocks
    private var notInitializedSpy: SuperUnderTesting? = null

    @Mock
    private var map: Map<*, *>? = null
    @Mock
    private var list: List<*>? = null
    @Mock
    private var histogram1: Set<*>? = null
    @Mock
    private var histogram2: Set<*>? = null
    @Mock
    private var candidate2: Methods4MockTests? = null

    @Spy
    private var searchTree: NavigableSet<String> = TreeSet()

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
    @DisplayName("should keep same instance if field initialized")
    fun testSameInstanceIfFieldIsInitialized() {
        assertSame(baseUnderTestingInstance, initializedBase)
    }

    @Test
    @DisplayName("should initialize annotated field if null")
    fun testIfFieldIsNotInitialized() {
        assertNotNull(notInitializedBase)
    }

    @Test
    @DisplayName("should inject mocks in spy")
    fun testInjectMocksWithInitializedSpy() {
        assertNotNull(initializedSpy.aList)
        assertTrue(Mockito.mockingDetails(initializedSpy).isMock)
    }

    @Test
    @DisplayName("should initialize spy if null and inject mocks")
    fun testInjectMocksWithNotInitializedSpy() {
        assertNotNull(notInitializedSpy)
        assertNotNull(notInitializedSpy!!.aList)
        assertTrue(Mockito.mockingDetails(notInitializedSpy).isMock)
        assertTrue(Mockito.mockingDetails(notInitializedSpy).isSpy)
    }

    @Test
    @DisplayName("should inject mocks if annotated")
    fun testInjectMocksWithAnnotation() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertSame(list, superUnderTest.aList)

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should not inject if not annotated")
    fun testInjectMocksWithoutAnnotation() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertNull(superUnderTestWithoutInjection.aList)

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should inject mocks for class hierarchy if annotated")
    fun testInjectMocksWithinClassHierarchy() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertSame(list, baseUnderTest.aList)
        assertSame(map, baseUnderTest.aMap)

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should inject mocks by name")
    fun testInjectMocksByName() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertSame(histogram1, subUnderTest.histogram1)
        assertSame(histogram2, subUnderTest.histogram2)

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should inject spies")
    fun testInjectMocksWithSpy() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertSame(searchTree, otherBaseUnderTest.searchTree)

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should insert into field with matching name when multiple fields of same type exists in injectee")
    fun testInjectMocksWithFieldsOfSameType() {
        val innerTestCloseable = KMockitoAnnotations.openMocks(this)

        assertNull(hasTwoFieldsWithSameType.candidate1, "not injected, no mock named 'candidate1'")
        assertNotNull(hasTwoFieldsWithSameType.candidate2, "injected, there's a mock named 'candidate2'")

        innerTestCloseable.close()
    }

    @Test
    @DisplayName("should instantiate inject mock field if possible")
    fun testInjectMocksWithNotInitializedField() {
        assertNotNull(notInitializedBase)
    }

    @Test
    @DisplayName("should report nicely")
    fun testNicelyReport() {
        val failing = object : Any() {
            @InjectMocks
            internal var failingConstructor: ThrowingConstructor? = null
        }

        val result = assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.openMocks(failing)
        }

        assertThat(result.message).contains("failingConstructor").contains("constructor").contains("threw an exception")
    }


    internal class ThrowingConstructor {
        init {
            throw RuntimeException("aha")
        }
    }

    internal open class SuperUnderTesting {
        var aList: List<*>? = null
    }

    internal open class BaseUnderTesting : SuperUnderTesting() {
        lateinit var aMap: Map<*, *>
    }

    internal class OtherBaseUnderTesting : SuperUnderTesting() {
        var searchTree: NavigableSet<*>? = null
    }

    internal class SubUnderTesting : BaseUnderTesting() {
        lateinit var histogram1: Set<*>
        lateinit var histogram2: Set<*>
    }

    internal class HasTwoFieldsWithSameType {
        var candidate1: Methods4MockTests? = null
        var candidate2: Methods4MockTests? = null
    }
}
