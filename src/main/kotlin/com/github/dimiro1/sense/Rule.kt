// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

import org.jsoup.nodes.Element

/**
 * A rule unit
 */
class Rule(val selector: String, val ranker: (Element) -> Score)