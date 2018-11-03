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

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import java.security.SecureRandom
import kotlin.reflect.KFunction0

class MockAnnotationTest {

    @Test
    @DisplayName("should mock mutable property")
    fun testMutableMock() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitList[0]).thenReturn("test")

        assertNotNull(testee.lateinitList)
        assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
        assertEquals("test", testee.lateinitList[0])
    }

    @Test
    @DisplayName("should kmock mutable property")
    fun testMutableKMock() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitListK[0]).thenReturn("test")

        assertNotNull(testee.lateinitListK)
        assertTrue(Mockito.mockingDetails(testee.lateinitListK).isMock)
        assertEquals("test", testee.lateinitListK[0])
    }

    @Test
    @DisplayName("should spy mutable property")
    fun testMutableSpy() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitSet.size).thenReturn(5)

        assertNotNull(testee.lateinitSet)
        assertTrue(Mockito.mockingDetails(testee.lateinitSet).isMock)
        assertTrue(Mockito.mockingDetails(testee.lateinitSet).isSpy)
        assertEquals(5, testee.lateinitSet.size)
    }

    @Test
    @DisplayName("should spy initialized mutable property")
    fun testMutableInitializedSpy() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.initializedSet.size).thenReturn(5)

        assertNotNull(testee.initializedSet)
        assertTrue(Mockito.mockingDetails(testee.initializedSet).isMock)
        assertTrue(Mockito.mockingDetails(testee.initializedSet).isSpy)
        assertEquals(5, testee.initializedSet.size)
    }

    @Test
    @DisplayName("should captor mutable property")
    fun testMutableCaptor() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)

        assertNotNull(testee.lateinitCaptor)
        assertEquals(0, testee.lateinitCaptor.allValues.size)
    }

    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee)' is called twice")
    fun testInitMocksTwoTimes() {
        val testee = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee)
        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitList[0]).thenReturn("test")

        assertNotNull(testee.lateinitList)
        assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
        assertEquals("test", testee.lateinitList[0])
    }

    @RepeatedTest(value = 15, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee)' is called many times in many treads")
    fun testInitMocksManyTimesInDifferentThreads() {

        fun runMockAnnotationsInitMocks() {
            val sleepTime = SecureRandom().nextInt(10).toLong()
            Thread.sleep(sleepTime)
            val testee = ClassWithMutableProperties()

            KMockitoAnnotations.initMocks(testee)
            whenever(testee.lateinitList[0]).thenReturn("test")

            assertNotNull(testee.lateinitList)
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

    @Test
    @DisplayName("should mock mutable property when 'KMockitoAnnotations.initMocks(testee1 / testee2)' is called")
    fun testInitMocksTwoTimesWithDifferentInstances() {
        val testee1 = ClassWithMutableProperties()
        val testee2 = ClassWithMutableProperties()

        KMockitoAnnotations.initMocks(testee1)
        KMockitoAnnotations.initMocks(testee2)
        whenever(testee1.lateinitList[0]).thenReturn("test1")
        whenever(testee2.lateinitList[0]).thenReturn("test2")

        assertNotNull(testee1.lateinitList)
        assertNotNull(testee2.lateinitList)
        assertTrue(Mockito.mockingDetails(testee1.lateinitList).isMock)
        assertTrue(Mockito.mockingDetails(testee2.lateinitList).isMock)
        assertEquals("test1", testee1.lateinitList[0])
        assertEquals("test2", testee2.lateinitList[0])
    }

    @Test
    @DisplayName("should report mock immutable property is not supported")
    fun testImmutableMock() {
        val testee = ClassWithImmutableMockProperty()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock field named 'immutableList', cause it is immutable!")
        assertThat(result).hasMessageContaining("@Mock")
    }

    @Test
    @DisplayName("should report kmock immutable property is not supported")
    fun testImmutableKMock() {
        val testee = ClassWithImmutableKMockProperty()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock field named 'immutableListK', cause it is immutable!")
        assertThat(result).hasMessageContaining("@Mock")
    }

    @Test
    @DisplayName("should report spy immutable property is not supported")
    fun testImmutableSpy() {
        val testee = ClassWithImmutableSpyProperty()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock field named 'immutableMap', cause it is immutable!")
        assertThat(result).hasMessageContaining("@Mock")
    }

    @Test
    @DisplayName("should report captor immutable property is not supported")
    fun testImmutableCaptor() {
        val testee = ClassWithImmutableCaptorProperty()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock field named 'captor', cause it is immutable!")
        assertThat(result).hasMessageContaining("@Mock")
    }

    @Test
    @DisplayName("should report mock property of final class like String is not supported")
    fun testMockOfFinalClass() {
        val testee = ClassWithMockPropertyOfFinalClass()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class java.lang.String")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should report kmock property of final class like String is not supported")
    fun testKMockOfFinalClass() {
        val testee = ClassWithKMockPropertyOfFinalClass()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class java.lang.String")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should report spy property of final class like String is not supported")
    fun testSpyOfFinalClass() {
        val testee = ClassWithSpyPropertyOfFinalClass()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class java.lang.String")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should mock mutable property (List) in a data class with sealed supertype class")
    fun testMockOfDataClassWithList() {
        val testee = DataSubClassOfSealedClass(34)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitList[0]).thenReturn("test")

        assertNotNull(testee.lateinitList)
        assertTrue(Mockito.mockingDetails(testee.lateinitList).isMock)
        assertEquals("test", testee.lateinitList[0])
    }

    @Test
    @DisplayName("should kmock mutable property (List) in a data class with sealed supertype class")
    fun testMockOfDataClassWithListWithKMock() {
        val testee = DataSubClassOfSealedClassWithKMock(34)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitListK[0]).thenReturn("test")

        assertNotNull(testee.lateinitListK)
        assertTrue(Mockito.mockingDetails(testee.lateinitListK).isMock)
        assertEquals("test", testee.lateinitListK[0])
    }

    @Test
    @DisplayName("should mock mutable property (Map) in a data class with sealed supertype class")
    fun testMockOfDataClassWithMap() {
        val testee = DataSubClassOfSealedClass(34)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitMap["key"]).thenReturn("test")

        assertNotNull(testee.lateinitMap)
        assertTrue(Mockito.mockingDetails(testee.lateinitMap).isMock)
        assertEquals("test", testee.lateinitMap["key"])
    }

    @Test
    @DisplayName("should kmock mutable property (Map) in a data class with sealed supertype class")
    fun testMockOfDataClassWithMapWithKMock() {
        val testee = DataSubClassOfSealedClassWithKMock(34)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitMapK["key"]).thenReturn("test")

        assertNotNull(testee.lateinitMapK)
        assertTrue(Mockito.mockingDetails(testee.lateinitMapK).isMock)
        assertEquals("test", testee.lateinitMapK["key"])
    }

    @Test
    @DisplayName("should spy mutable property (Set) in a data class with sealed supertype class")
    fun testMockOfDataClassWithSet() {
        val testee = DataSubClassOfSealedClass(34)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.lateinitSet.contains("test")).thenReturn(true)

        assertNotNull(testee.lateinitSet)
        assertTrue(Mockito.mockingDetails(testee.lateinitSet).isMock)
        assertTrue(Mockito.mockingDetails(testee.lateinitSet).isSpy)
        assertEquals(true, testee.lateinitSet.contains("test"))
    }

    @Test
    @DisplayName("should mock mutable property of a sealed class")
    fun testMockOfSealedClass() {
        val testee = ClassWithMockOfSealedClasses()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.sealedPropertyToMock.toString()).thenReturn("test")

        assertNotNull(testee.sealedPropertyToMock)
        assertTrue(Mockito.mockingDetails(testee.sealedPropertyToMock).isMock)
        assertEquals("test", testee.sealedPropertyToMock.toString())
    }

    @Test
    @DisplayName("should kmock mutable property of a sealed class")
    fun testKMockOfSealedClass() {
        val testee = ClassWithKMockOfSealedClasses()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.sealedPropertyToKMock.toString()).thenReturn("test")

        assertNotNull(testee.sealedPropertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.sealedPropertyToKMock).isMock)
        assertEquals("test", testee.sealedPropertyToKMock.toString())
    }

    @Test
    @DisplayName("should report spy of mutable property of a sealed class is not supported")
    fun testSpyOfSealedClass() {
        val testee = ClassWithSpyOfSealedClasses()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Unable to initialize @Spy annotated field named 'sealedPropertyToSpy' of type 'org.mockito4kotlin.annotation.SealedClass'.")
        assertThat(result).hasMessageContaining("@Spy annotation can't initialize sealed classes.")
    }

    @Test
    @DisplayName("should report spy of mutable initialized property of sealed class is not supported")
    fun testMockOfInitializedSealedClass() {
        val testee = ClassWithSpyOfInitializedSealedClasses()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Unable to initialize @Spy annotated field named 'sealedPropertyToSpy' of type 'org.mockito4kotlin.annotation.SealedClass'.")
        assertThat(result).hasMessageContaining("@Spy annotation can't initialize sealed classes.")
    }

    @Test
    @DisplayName("should report spy of mutable initialized property of sealed class is not supported")
    fun testMockOfInitializedSealedClassWithKMock() {
        val testee = ClassWithSpyOfInitializedSealedClassesWithKMock()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Unable to initialize @Spy annotated field named 'sealedPropertyToSpy' of type 'org.mockito4kotlin.annotation.SealedClass'.")
        assertThat(result).hasMessageContaining("@Spy annotation can't initialize sealed classes.")
    }

    @Test
    @DisplayName("should mock mutable property of a class with extension property")
    fun testMockOfClassWithExtensionProperty() {
        val testee = ClassWithMockExtensionProperty()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToMock.toString()).thenReturn("test")

        assertNotNull(testee.propertyToMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToMock).isMock)
        assertNotNull(testee.propertyToMock.value)
        assertEquals("test", testee.propertyToMock.toString())
    }

    @Test
    @DisplayName("should mock mutable property of a class with extension property")
    fun testKMockOfClassWithExtensionProperty() {
        val testee = ClassWithMockExtensionProperty()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToKMock.toString()).thenReturn("test")

        assertNotNull(testee.propertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToKMock).isMock)
        assertNotNull(testee.propertyToKMock.value)
        assertEquals("test", testee.propertyToKMock.toString())
    }

    @Test
    @DisplayName("should mock mutable property in a companion object")
    fun testMockOfPropertyOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectAndMockProperty.Companion

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToMock).isMock)
        assertEquals('k', testee.propertyToMock[0])
    }

    @Test
    @DisplayName("should kmock mutable property in a companion object")
    fun testKMockOfPropertyOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectAndMockProperty.Companion

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToKMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToKMock).isMock)
        assertEquals('k', testee.propertyToKMock[0])
    }

    @Test
    @DisplayName("should mock mutable property in a companion object")
    fun testMockOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectMockProperty()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToMock.toString()).thenReturn("test")

        assertNotNull(testee.propertyToMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToMock).isMock)
        assertEquals("test", testee.propertyToMock.toString())
    }

    @Test
    @DisplayName("should kmock mutable property in a companion object")
    fun testKMockOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectMockProperty()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToKMock.toString()).thenReturn("test")

        assertNotNull(testee.propertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToKMock).isMock)
        assertEquals("test", testee.propertyToKMock.toString())
    }

    @Test
    @DisplayName("should spy mutable property of companion object")
    fun testSpyOfPropertyOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectAndSpyProperty.Companion

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToSpy[0]).thenReturn('k')

        assertNotNull(testee.propertyToSpy)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isSpy)
        assertEquals('k', testee.propertyToSpy[0])
    }

    @Test
    @DisplayName("should spy mutable property of companion object")
    fun testSpyOfClassCompanionObject() {
        val testee = ClassWithCompanionObjectSpyProperty()

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToSpy.toString()).thenReturn("test")

        assertNotNull(testee.propertyToSpy)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isSpy)
        assertEquals("test", testee.propertyToSpy.toString())
    }

    @Test
    @DisplayName("should mock mutable property in a object (singleton)")
    fun testMockOfObject() {
        val testee = ObjectWithMockProperty

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToMock).isMock)
        assertEquals('k', testee.propertyToMock[0])
    }

    @Test
    @DisplayName("should kmock mutable property in a object (singleton)")
    fun testKMockOfObject() {
        val testee = ObjectWithMockProperty

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToKMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToKMock).isMock)
        assertEquals('k', testee.propertyToKMock[0])
    }

    @Test
    @DisplayName("should spy mutable property in a object (singleton)")
    fun testSpyOfObject() {
        val testee = ObjectWithSpyProperty

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToSpy[0]).thenReturn('k')

        assertNotNull(testee.propertyToSpy)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isSpy)
        assertEquals('k', testee.propertyToSpy[0])
    }

    @Test
    @DisplayName("should report mock mutable property of a object (singleton, final) is not supported")
    fun testMockObjectProperty() {
        val testee = ClassWithMockObjectProperties()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class org.mockito4kotlin.annotation.ObjectForTests")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should report mock mutable property of a object (singleton, final) is not supported")
    fun testKMockObjectProperty() {
        val testee = ClassWithKMockObjectProperties()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class org.mockito4kotlin.annotation.ObjectForTests")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should report spy mutable property of a object (singleton, final) is not supported")
    fun testSpyObjectProperty() {
        val testee = ClassWithSpyObjectProperties()

        val result = Assertions.assertThrows(MockitoException::class.java) {
            KMockitoAnnotations.initMocks(testee)
        }

        assertThat(result).hasMessageContaining("Cannot mock/spy class org.mockito4kotlin.annotation.ObjectForTests")
        assertThat(result).hasMessageContaining("final class")
    }

    @Test
    @DisplayName("should not mock mutable property in data class")
    fun testMockOfDataClassProperty() {
        val testee = DataClassWithMockPropertyInConstructor("mockito for kotlin")

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToMock)
        assertFalse(Mockito.mockingDetails(testee.propertyToMock).isMock)
        assertNotEquals('k', testee.propertyToMock[0])
    }

    @Test
    @DisplayName("should kmock mutable property in data class")
    fun testKMockOfDataClassProperty() {
        val testee = DataClassWithKMockPropertyInConstructor("mockito for kotlin")

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToKMock[0]).thenReturn('k')

        assertNotNull(testee.propertyToKMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToKMock).isMock)
        assertEquals('k', testee.propertyToKMock[0])
    }

    @Test
    @DisplayName("should spy mutable property in data class")
    fun testSpyOfDataClassProperty() {
        val testee = DataClassWithSpyPropertyInConstructor(null)

        KMockitoAnnotations.initMocks(testee)
        whenever(testee.propertyToSpy?.get(0)).thenReturn('k')

        assertNotNull(testee.propertyToSpy)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isMock)
        assertTrue(Mockito.mockingDetails(testee.propertyToSpy).isSpy)
        assertEquals('k', testee.propertyToSpy?.get(0))
    }

}