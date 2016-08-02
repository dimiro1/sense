// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

/**
 * Knowledge is a container for querying facts, where a fact has a
 * flavor, a score, and some other arbitrary metadata.
 */
class Knowledge(val nodesByFlavor: Map<String, Set<Score>>) {
    /**
     * Return the highest-scored node of the given flavor, null if
     * there is none.
     */
    fun max(flavor: String) = nodesByFlavor[flavor]?.maxBy { it.score }

    /**
     * Returns all nodes of the given flavor ordered by the descending score.
     */
    fun nodesOfFlavor(flavor: String) = nodesByFlavor[flavor]?.sortedByDescending { it.score } ?: listOf()
}