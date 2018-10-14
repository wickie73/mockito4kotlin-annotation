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

import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Spy

class ClassWithMutableProperties {

    @Mock
    lateinit var lateinitList: List<String>

    @Spy
    lateinit var lateinitSet: Set<String>

    @Spy
    var initializedSet: Set<String> = HashSet()

    @Captor
    lateinit var lateinitCaptor: ArgumentCaptor<*>
}

class ClassWithImmutableMockProperty {

    @Mock
    val immutableList = listOf<String>()

}

class ClassWithImmutableSpyProperty {

    @Mock
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

class ClassWithSpyPropertyOfFinalClass {

    @Spy
    lateinit var name: String

}

sealed class SealedClass {

    @Mock
    lateinit var lateinitList: List<String>
    @Spy
    lateinit var lateinitSet: Set<String>

}

data class DataSubClassOfSealedClass(val value: Int) : SealedClass() {
    @Mock
    lateinit var lateinitMap: Map<String, String>
}

class ClassWithMockOfSealedClasses {

    @Mock
    lateinit var sealedPropertyToMock: SealedClass

}

class ClassWithSpyOfSealedClasses {

    @Spy
    lateinit var sealedPropertyToSpy: SealedClass

}

class ClassWithSpyOfInitializedSealedClasses {

    @Spy
    var sealedPropertyToSpy: SealedClass = DataSubClassOfSealedClass(34)

}


open class ClassWithExtensionProperty

var ClassWithExtensionProperty.value: List<String>
    get() = mutableListOf()
    set(value) = print(value)

class ClassWithMockExtensionProperty {

    @Mock
    lateinit var propertyToMock: ClassWithExtensionProperty

}

open class ClassWithCompanionObjectAndMockProperty {
    companion object {

        @Mock
        lateinit var propertyToMock: CharSequence

    }
}

class ClassWithCompanionObjectMockProperty {

    @Mock
    lateinit var propertyToMock: ClassWithCompanionObjectAndMockProperty

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

class ClassWithSpyObjectProperties {

    @Spy
    lateinit var lateinitObj: ObjectForTests

}

data class DataClassWithMockPropertyInConstructor(@Mock var propertyToMock: CharSequence)

data class DataClassWithSpyPropertyInConstructor(@Spy var propertyToSpy: CharSequence?)



