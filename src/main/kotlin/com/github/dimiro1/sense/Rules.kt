// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

import org.jsoup.nodes.Document

/**
 * Rules have a list of all rules and offer a method to extract Knowledge from
 * the document.
 */
class Rules(val rules: Set<Rule>,
            val rewards: Set<Reward> = setOf()) {
    /**
     * Apply the rules and return a Knowledge about the document
     */
    fun score(document: Document): Knowledge {
        val rulesScores = mutableMapOf<String, MutableSet<Score>>()

        // Apply dom rules
        for (rule in rules) {
            for (element in document.select(rule.selector)) {
                val result = rule.ranker(element)

                if (!rulesScores.containsKey(result.flavor)) {
                    rulesScores[result.flavor] = mutableSetOf()
                }

                rulesScores[result.flavor]?.add(result)
            }
        }

        // Apply rewards
        for (reward in rewards) {
            for ((selector, scores) in rulesScores.filter { it.key == reward.selector }) {
                for (score in scores) {
                    val punishmentResult = reward.ranker(score.element)
                    score.score *= punishmentResult.score
                    score.metadata.putAll(punishmentResult.metadata)
                }
            }
        }

        return Knowledge(rulesScores)
    }
}

