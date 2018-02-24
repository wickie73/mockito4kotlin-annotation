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
import org.mockito.exceptions.base.MockitoException
import kotlin.reflect.full.createInstance


/**
 * Extension of [org.mockito.ArgumentCaptor.capture] in case of [org.mockito.ArgumentCaptor.capture] returns null,
 * it will be tried to create an instance.
 * An empty constructor is assumed.
 *
 * @see org.mockito.ArgumentCaptor.capture
 */
inline fun <reified T : Any> ArgumentCaptor<T>.trap(): T =
        capture() ?: try {
            T::class.objectInstance ?: T::class.createInstance()
        } catch (iae: IllegalArgumentException) {
            throw MockitoException("""
                |Can't capture class '${T::class.qualifiedName}', cause it has no empty, accessible constructor to create an instance!
                """.trimMargin(), iae)
        } catch (e: Exception) {
            throw MockitoException("""
                |Can't capture class '${T::class.qualifiedName}', cause to create an instance failed!
                """.trimMargin(), e)
        }

/**
 * Extension of [org.mockito.ArgumentCaptor.capture] in case of [org.mockito.ArgumentCaptor.capture] returns null,
 * the given [instance] will be returned.
 *
 * @see org.mockito.ArgumentCaptor.capture
 *
 * @param instance the instance of type T which will be returned if [org.mockito.ArgumentCaptor.capture] returns null.
 */
inline fun <reified T : Any> ArgumentCaptor<T>.trap(instance: T): T = this.capture() ?: instance
