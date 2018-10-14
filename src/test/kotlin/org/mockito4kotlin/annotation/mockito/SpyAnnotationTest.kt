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

package org.mockito4kotlin.annotation.mockito

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.exceptions.base.MockitoException
import org.mockito4kotlin.annotation.KMockitoAnnotations
import java.util.*

/**
 * This test class is originated from Mockito's [org.mockitousage.annotation.SpyAnnotationTest] and
 * ensures that [KMockitoAnnotations] is compatible with Mockito Annotations like
 * * @[org.mockito.Mock]
 * * @[org.mockito.Spy]
 * * @[org.mockito.Captor]
 * * @[org.mockito.InjectMocks]
 */
class SpyAnnotationTest {

    @Spy
    private var spiedList: List<String> = mutableListOf()

    @Spy
    private lateinit var staticTypeWithNoArgConstructor: InnerStaticClassWithNoArgConstructor

    @Spy
    internal lateinit var staticTypeWithoutDefinedConstructor: InnerStaticClassWithoutDefinedConstructor

    @Spy
    internal lateinit var translator: MockTranslator

    @BeforeEach
    fun setUp() {
        KMockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("should init spy by instance")
    fun testInitSpyByInstance() {
        doReturn("foo").whenever(spiedList).get(10)

        assertEquals("foo", spiedList[10])
        assertTrue(spiedList.isEmpty())
    }

    @Test
    @DisplayName("should init spy and automatically create instance")
    fun testInitSpyByInstanceAndCreateInstance() {
        whenever(staticTypeWithNoArgConstructor.toString()).thenReturn("x")
        whenever(staticTypeWithoutDefinedConstructor.toString()).thenReturn("y")

        assertEquals("x", staticTypeWithNoArgConstructor.toString())
        assertEquals("y", staticTypeWithoutDefinedConstructor.toString())
    }


    @Test
    @DisplayName("should allow spying on interfaces")
    fun testSpyOnInterface() {
        class WithSpy {
            @Spy
            var list: List<String>? = null
        }

        val withSpy = WithSpy()

        KMockitoAnnotations.initMocks(withSpy)

        whenever(withSpy.list!!.size).thenReturn(3)
        assertEquals(3, withSpy.list!!.size)
    }

    @Test
    @DisplayName("should allow spying on interfaces when instance is concrete")
    fun testSpyOnInterfaceWithCurrentInstance() {
        class WithSpy {
            @Spy
            var list: List<String> = mutableListOf()
        }

        val withSpy = WithSpy()

        //when
        KMockitoAnnotations.initMocks(withSpy)

        //then
        verify(withSpy.list, never()).isEmpty()
    }

    @Test
    @DisplayName("should report when no arg less constructor")
    fun testSpyWithNoArgConstructor() {
        class FailingSpy {
            @Spy
            var noValidConstructor: NoValidConstructor? = null
        }

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(FailingSpy())
        })

        assertThat(result.message)
            .contains("Unable to create mock instance")
            .contains(NoValidConstructor::class.java.simpleName)
    }

    @Test
    @DisplayName("should report when constructor is explosive")
    fun shouldReportWhenConstructorIsExplosive() {
        class FailingSpy {
            @Spy
            var throwingConstructor: ThrowingConstructor? = null
        }

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(FailingSpy())
        })

        assertThat(result.message).contains("Unable to create mock instance")
    }

    @Test
    @DisplayName("should spy abstract class")
    fun testSpyWithAbstractClass() {
        class SpyAbstractClass {
            @Spy
            var list: AbstractList<String>? = null

            fun asSingletonList(s: String): List<String> {
                whenever(list!!.size).thenReturn(1)
                whenever(list!![0]).thenReturn(s)
                return list!!
            }
        }

        val withSpy = SpyAbstractClass()

        KMockitoAnnotations.initMocks(withSpy)

        assertEquals(listOf("a"), withSpy.asSingletonList("a"))
    }

    @Test
    @DisplayName("should spy inner class")
    fun testSpyInnerClass() {

        class WithMockAndSpy {
            @Spy
            internal var strength: InnerStrength? = null
            @Mock
            private var list: List<String>? = null

            internal abstract inner class InnerStrength {
                private val name: String

                init {
                    // Make sure that @Mock fields are always injected before @Spy fields.
                    assertNotNull(list)
                    // Make sure constructor is indeed called.
                    this.name = "inner"
                }

                abstract fun strength(): String

                fun fullStrength(): String {
                    return name + " " + strength()
                }
            }
        }

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(WithMockAndSpy())
        })

        assertThat(result.message).contains("Unable to create mock instance of type 'InnerStrength'")
    }

    @Test
    @DisplayName("should reset spy")
    fun testResetSpy() {
        assertThrows(IndexOutOfBoundsException::class.java, {
            spiedList[10] // see shouldInitSpy
        })
    }

    @Test
    @DisplayName("should report when enclosing instance is needed")
    fun testIfEnclosingInstanceIsNeeded() {
        class Outer {
            internal open inner class Inner
        }

        class WithSpy {
            @Spy
            private var inner: Outer.Inner? = null
        }

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(WithSpy())
        })

        assertThat(result).hasMessageContaining("Unable to create mock instance of type")
    }

    @Test
    @DisplayName("should report private inner not supported")
    fun testReportPrivateInnerClassesNotSupported() {
        val spyWithInnerPrivate = WithInnerPrivate()

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(spyWithInnerPrivate)
        })

        assertThat(result).hasMessageContaining("@Spy annotation can't initialize private/internal inner classes.")
            .hasMessageContaining(WithInnerPrivate::class.simpleName)
            .hasMessageContaining("InnerPrivate")
            .hasMessageContaining("You should augment the visibility of this inner class.")
    }

    @Test
    @DisplayName("should report internal abstract inner not supported")
    fun testReportInternalAbstractInnerClassesNotSupported() {
        val spyWithInnerInternal = WithInnerInternal()

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(spyWithInnerInternal)
        })

        assertThat(result).hasMessageContaining("@Spy annotation can't initialize private/internal inner classes.")
            .hasMessageContaining(WithInnerInternal::class.simpleName)
            .hasMessageContaining("InnerInternal")
            .hasMessageContaining("You should augment the visibility of this inner class.")
    }

    @Test
    @DisplayName("should report private abstract inner not supported")
    fun testReportPrivateAbstractInnerClassesNotSupported() {
        val spyWithInnerPrivateAbstract = WithInnerPrivateAbstract()

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(spyWithInnerPrivateAbstract)
        })

        assertThat(result).hasMessageContaining("@Spy annotation can't initialize private/internal inner classes.")
            .hasMessageContaining(WithInnerPrivateAbstract::class.simpleName)
            .hasMessageContaining("InnerPrivateAbstract")
            .hasMessageContaining("You should augment the visibility of this inner class.")
    }

    @Test
    @DisplayName("should report private static abstract inner not supported")
    fun testReportPrivateStaticAbstractInnerClassesNotSupported() {
        val spyWithInnerPrivateStaticAbstract = WithInnerPrivateStaticAbstract()

        val result = assertThrows(MockitoException::class.java, {
            KMockitoAnnotations.initMocks(spyWithInnerPrivateStaticAbstract)
        })


        assertThat(result).hasMessageContaining("@Spy annotation can't initialize companion objects.")
            .hasMessageContaining("${WithInnerPrivateStaticAbstract::class.qualifiedName}.InnerPrivateStaticAbstract.Test")
            .hasMessageContaining("You should avoid to mock/spy companion objects.")
    }

    @Test
    @DisplayName("should be able to stub and verify via varargs for list params")
    fun testSpyWithVarargs() {
        // You can stub with varargs.
        whenever(translator.translate("hello", "mockito")).thenReturn(Arrays.asList("you", "too"))

        // Pretend the prod code will call translate(List<String>) with these elements.
        assertThat(translator.translate(Arrays.asList("hello", "mockito"))).containsExactly("you", "too")
        assertThat(translator.translate(Arrays.asList("not stubbed"))).isEmpty()

        // You can verify with varargs.
        verify(translator).translate("hello", "mockito")
    }

    @Test
    @DisplayName("should be able to stub and verify via varargs of matcher for list params")
    fun testSpyWithVarargsViaMatcher() {
        // You can stub with varargs of matcher.
        whenever(translator.translate(any<String>())).thenReturn(Arrays.asList("huh?"))
        whenever(translator.translate(eq("hello"))).thenReturn(Arrays.asList("hi"))

        // Pretend the prod code will call translate(List<String>) with these elements.
        assertThat(translator.translate(Arrays.asList("hello"))).containsExactly("hi")
        assertThat(translator.translate(Arrays.asList("not explicitly stubbed"))).containsExactly("huh?")

        // You can verify with varargs of matchers.
        verify(translator).translate(eq("hello"))
    }


    internal class WithInnerPrivateStaticAbstract {
        @Spy
        private var spy_field: InnerPrivateStaticAbstract.Test? = null

        private abstract class InnerPrivateStaticAbstract {
            internal companion object Test
        }
    }

    internal class WithInnerPrivateAbstract {
        @Spy
        private var spy_field: InnerPrivateAbstract? = null

        fun some_method() {
            InnerPrivateConcrete()
        }

        private abstract inner class InnerPrivateAbstract

        private inner class InnerPrivateConcrete : InnerPrivateAbstract()
    }

    internal class WithInnerPrivate {
        @Spy
        private var spy_field: InnerPrivate? = null

        private open inner class InnerPrivate

        private inner class InnerPrivateSub : InnerPrivate()
    }

    internal class WithInnerInternal {
        @Spy
        private var spy_field: InnerInternal? = null

        internal open inner class InnerInternal

        private inner class InnerInternalSub : InnerInternal()
    }

    internal open class InnerStaticClassWithoutDefinedConstructor

    internal open class InnerStaticClassWithNoArgConstructor {
        constructor()

        @Suppress("UNUSED_PARAMETER")
        constructor(f: String)
    }

    @Suppress("UNUSED_PARAMETER")
    internal open class NoValidConstructor(f: String)

    internal open class ThrowingConstructor {
        init {
            throw RuntimeException("boo!")
        }
    }

    internal interface Translator {
        fun translate(messages: List<String>): List<String>
    }

    internal abstract class MockTranslator : Translator {
        override fun translate(messages: List<String>): List<String> {
            return translate(*messages.toTypedArray())
        }

        internal abstract fun translate(vararg messages: String): List<String>
    }
}
