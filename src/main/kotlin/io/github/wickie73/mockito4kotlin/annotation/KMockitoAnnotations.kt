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

import io.github.wickie73.mockito4kotlin.annotation.engine.AnnotationEngineManager
import org.mockito.*
import kotlin.reflect.KClass

/**
 * Mark a field, property or property's backing field as a mock.
 *
 * * Makes code easier to read.
 * * Separate mock initialization from test code.
 * * Works like @[org.mockito.Mock] annotation of Mockito.
 * * With [KMock] you are able to use Mockito's Annotation by data classes and classes with delegates.
 *
 * See examples in [org.mockito.Mock]
 *
 * @see org.mockito.Mock
 * @see org.mockito.Mockito.mock
 * @see org.mockito.MockSettings
 * @see org.mockito.Spy
 * @see org.mockito.InjectMocks
 * @see org.mockito.MockitoAnnotations.openMocks
 *
 * @property extraInterfaces Extra interfaces the mock should implement.
 * @property stubOnly Does not record method invocation -> disallowing verification of invocations
 * @property name Name of this mock.
 * @property answer Default answers to interactions.
 * @property serializable Mock is serializable.
 * @property lenient Mock will be lenient [org.mockito.MockSettings.lenient].
 *
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@MustBeDocumented
annotation class KMock(val extraInterfaces: Array<KClass<out Any>> = [],
                       val stubOnly: Boolean = false,
                       val name: String = "",
                       val answer: Answers = Answers.RETURNS_DEFAULTS,
                       val serializable: Boolean = false,
                       val lenient: Boolean = false)

/**
 * Allows shorthand [org.mockito.kotlin.KArgumentCaptor] creation on fields,
 * properties or property's backing fields.
 *
 * * Makes code easier to read.
 * * Separate initialization of KArgumentCaptor from test code.
 * * Works like @[org.mockito.Captor] annotation of Mockito.
 *
 * See examples in [org.mockito.Captor]
 *
 * @see org.mockito.Captor
 * @see org.mockito.ArgumentCaptor
 * @see org.mockito.kotlin.KArgumentCaptor
 * @see org.mockito.Mock
 * @see org.mockito.Mockito.spy
 * @see org.mockito.InjectMocks
 * @see org.mockito.MockitoAnnotations.openMocks
 *
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@MustBeDocumented
annotation class KCaptor


/**
 * KMockitoAnnotations.openMocks(this) initializes fields, property or property's backing field
 * of the given instance annotated with mock annotations like [Mock], [KMock], [Spy], [Captor], [KCaptor] and [InjectMocks].
 *
 * * Makes code easier to read.
 * * Separate initialization of ArgumentCaptor from test code.
 * * Works like [org.mockito.MockitoAnnotations] of Mockito.
 *
 * See examples in [org.mockito.MockitoAnnotations]
 *
 * @see org.mockito.MockitoAnnotations.openMocks
 * @see org.mockito.Mockito.mock
 * @see org.mockito.Mock
 * @see org.mockito.Spy
 * @see org.mockito.Captor
 * @see org.mockito.InjectMocks
 */
object KMockitoAnnotations {

    /**
     * Initializes objects annotated with [Mock], [KMock], [Spy], [Captor], [KCaptor] and [InjectMocks].
     *
     * @param anyInstanceWithMocks instance with MockAnnotations to be initialized.
     * @return [AutoCloseable] which has to close after tests has been completed.
     * @see [MockitoAnnotations.openMocks]
     */
    fun openMocks(anyInstanceWithMocks: Any): AutoCloseable {
        val annotationEngineManager = AnnotationEngineManager(anyInstanceWithMocks)
        return annotationEngineManager.process()
    }

    /**
     * Initializes objects annotated with [Mock], [KMock], [Spy], [Captor], [KCaptor] and [InjectMocks].
     *
     * @param anyInstanceWithMocks instance with MockAnnotations to be initialized.
     *
     * This method is equivalent to `openMocks(testClass).close()`.
     * However the [AutoCloseable.close] method should only used after usage of parameter [anyInstanceWithMocks].
     * @see [MockitoAnnotations.initMocks]
     * @see [MockitoAnnotations.openMocks]
     * @since 0.5.0
     */
    @Deprecated("Since MockitoAnnotations.initMocks(Any) in version 3.4.0 is deprecated.", replaceWith = ReplaceWith("KMockitoAnnotations.openMocks(anyInstanceWithMocks)"))
    fun initMocks(anyInstanceWithMocks: Any) {
        openMocks(anyInstanceWithMocks).close();
    }
}

