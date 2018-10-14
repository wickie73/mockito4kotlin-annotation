/*
 *
 *  * The MIT License
 *  *
 *  *   Copyright (c) 2017-2018 Wilhelm Schulenburg
 *  *   Copyright (c) 2007 Mockito contributors
 *  *
 *  *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  *  this software and associated documentation files (the "Software"), to deal in
 *  *  the Software without restriction, including without limitation the rights to
 *  *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  *  copies of the Software, and to permit persons to whom the Software is
 *  *  furnished to do so, subject to the following conditions:
 *  *
 *  *  The above copyright notice and this permission notice shall be included
 *  *  in all copies or substantial portions of the Software.
 *  *
 *  *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  *  THE SOFTWARE.
 *
 *
 */

package org.mockito4kotlin.annotation.engine

import org.mockito.Mock
import org.mockito.MockSettings
import org.mockito.Mockito
import org.mockito4kotlin.annotation.allAnnotations
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

internal object MockAnnotationProcessor {

    fun createMock(property: KMutableProperty<*>): Any = Mockito.mock(property.javaField?.type, retrieveMockSettings(property))

    private fun retrieveMockSettings(property: KProperty<*>): MockSettings {
        val mockSettings = Mockito.withSettings()
        with(property.allAnnotations().find { it is Mock } as Mock) {
            when {
                extraInterfaces.isNotEmpty() -> mockSettings.extraInterfaces(*extraInterfaces.map { it.java }.toTypedArray())
            }
            if (stubOnly) mockSettings.stubOnly()
            if (name.isNotEmpty()) mockSettings.name(name) else mockSettings.name(property.name)
            if (serializable) mockSettings.serializable()
            mockSettings.defaultAnswer(answer)
        }
        return mockSettings
    }
}
