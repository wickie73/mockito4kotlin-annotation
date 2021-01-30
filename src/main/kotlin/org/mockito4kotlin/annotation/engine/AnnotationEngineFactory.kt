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

package org.mockito4kotlin.annotation.engine

import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito4kotlin.annotation.KCaptor
import org.mockito4kotlin.annotation.KMock

internal object AnnotationEngineFactory {

    internal fun create(annotation: Annotation?): AnnotationEngine {
        return when (annotation) {
            is Mock -> MockAnnotationEngine(Mock::class)
            is KMock -> MockAnnotationEngine(KMock::class)
            is Spy -> SpyAnnotationEngine()
            is Captor -> CaptorAnnotationEngine()
            is KCaptor -> KCaptorAnnotationEngine()
            is InjectMocks -> InjectMocksAnnotationEngine()
            else -> throw IllegalArgumentException("Unknown annotation $annotation")
        }
    }

}
