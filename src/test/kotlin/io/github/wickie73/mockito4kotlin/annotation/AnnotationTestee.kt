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

import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Spy

class ClassWithMutableProperties {
    @Mock
    lateinit var lateinitList: List<String>
    @KMock
    lateinit var lateinitListK: List<String>
    @Spy
    lateinit var lateinitSet: Set<String>
    @Captor
    lateinit var lateinitCaptor: ArgumentCaptor<*>

    @Spy
    var initializedSet: Set<String> = HashSet()
}

class ClassWithImmutableMockProperty {
    @Mock
    val immutableList = listOf<String>()
}

class ClassWithImmutableKMockProperty {
    @KMock
    val immutableListK = listOf<String>()
}

class ClassWithImmutableSpyProperty {
    @Spy
    val immutableMap = mapOf<String, String>()
}

class ClassWithImmutableCaptorProperty {
    @Captor
    val captor = ArgumentCaptor.forClass(String::class.java)
}

class ClassWithMockPropertyOfFinalClass {
    @Mock
    lateinit var name: String
}

class ClassWithKMockPropertyOfFinalClass {
    @KMock
    lateinit var nameK: String
}

class ClassWithSpyPropertyOfFinalClass {
    @Spy
    lateinit var name: String
}

sealed class SealedClass {
    @Mock
    lateinit var lateinitList: List<String>
    @KMock
    lateinit var lateinitListK: List<String>
    @Spy
    lateinit var lateinitSet: Set<String>
}

data class DataSubClassOfSealedClass(val value: Int) : SealedClass() {
    @Mock
    lateinit var lateinitMap: Map<String, String>
}

data class DataSubClassOfSealedClassWithKMock(val value: Int) : SealedClass() {
    @KMock
    lateinit var lateinitMapK: Map<String, String>
}

class ClassWithMockOfSealedClasses {
    @Mock
    lateinit var sealedPropertyToMock: SealedClass
}

class ClassWithKMockOfSealedClasses {
    @KMock
    lateinit var sealedPropertyToKMock: SealedClass
}

class ClassWithSpyOfSealedClasses {
    @Spy
    lateinit var sealedPropertyToSpy: SealedClass
}

class ClassWithSpyOfInitializedSealedClasses {
    @Spy
    var sealedPropertyToSpy: SealedClass = DataSubClassOfSealedClass(34)
}

class ClassWithSpyOfInitializedSealedClassesWithKMock {
    @Spy
    var sealedPropertyToSpy: SealedClass = DataSubClassOfSealedClassWithKMock(34)
}


open class ClassWithExtensionProperty

var ClassWithExtensionProperty.value: List<String>
    get() = mutableListOf()
    set(value) = print(value)

class ClassWithMockExtensionProperty {
    @Mock
    lateinit var propertyToMock: ClassWithExtensionProperty
    @KMock
    lateinit var propertyToKMock: ClassWithExtensionProperty
}

open class ClassWithCompanionObjectAndMockProperty {
    companion object {
        @Mock
        lateinit var propertyToMock: CharSequence
        @KMock
        lateinit var propertyToKMock: CharSequence
    }
}

class ClassWithCompanionObjectMockProperty {
    @Mock
    lateinit var propertyToMock: ClassWithCompanionObjectAndMockProperty
    @KMock
    lateinit var propertyToKMock: ClassWithCompanionObjectAndMockProperty
}

open class ClassWithCompanionObjectAndSpyProperty {
    companion object {
        @Spy
        lateinit var propertyToSpy: CharSequence
    }
}

class ClassWithCompanionObjectSpyProperty {
    @Spy
    lateinit var propertyToSpy: ClassWithCompanionObjectAndSpyProperty
}

object ObjectWithMockProperty {
    @Mock
    lateinit var propertyToMock: CharSequence
    @KMock
    lateinit var propertyToKMock: CharSequence
}

object ObjectWithSpyProperty {
    @Spy
    lateinit var propertyToSpy: CharSequence
}

object ObjectForTests

class ClassWithMockObjectProperties {
    @Mock
    lateinit var lateinitObj: ObjectForTests
}

class ClassWithKMockObjectProperties {
    @KMock
    lateinit var lateinitObjK: ObjectForTests
}

class ClassWithSpyObjectProperties {
    @Spy
    lateinit var lateinitObj: ObjectForTests
}

interface InterfaceWithSuspendMethod {
    suspend fun suspendCall()
}

class ClassWithMockOfClassWithSuspendMethod {
    @Mock
    lateinit var property: InterfaceWithSuspendMethod
}

class ClassWithKMockOfClassWithSuspendMethod {
    @KMock
    lateinit var property: InterfaceWithSuspendMethod
}

data class DataClassWithMockPropertyInConstructor(@Mock var propertyToMock: CharSequence)

data class DataClassWithKMockPropertyInConstructor(@KMock var propertyToKMock: CharSequence)

data class DataClassWithSpyPropertyInConstructor(@Spy var propertyToSpy: CharSequence?)

// inline class InlineClass(@Mock val propertyToMock: CharSequence)

