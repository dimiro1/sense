// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

abstract class BaseScore(var score: Double,
                         val metadata: MutableMap<String, Any>) {
    /**
     * Returns a metadata value by key, null when the key could not be found
     */
    fun metadata(key: String): Any? = metadata[key]
}