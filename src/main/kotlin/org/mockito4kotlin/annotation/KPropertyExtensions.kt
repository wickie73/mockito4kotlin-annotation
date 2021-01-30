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

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

/**
 * Returns [KType] of this property with nullability.
 */
val <T> KProperty<T>.type: KType
    get() = this.returnType.withNullability(false)

/**
 * Returns [KClass] of this property, if available.
 */
val <T> KProperty<T>.kClass: KClass<*>?
    get() = this.returnType.classifier as? KClass<*>

/**
 * Returns whether this property has type arguments like generics classes, e.g.
 *
 *     var property: List<String>? = null
 * , where [String] the type argument of class [List].
 */
fun <T> KProperty<T>.hasTypeArguments(): Boolean = this.type.arguments.isNotEmpty()

/**
 * Returns all annotations including java-based annotations.
 */
fun <T> KProperty<T>.allAnnotations(): List<Annotation> = this.javaField?.annotations?.let { this.annotations + it }
    ?: this.annotations



