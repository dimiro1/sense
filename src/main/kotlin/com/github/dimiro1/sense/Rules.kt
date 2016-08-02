// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

import org.jsoup.nodes.Document

/**
 * Rules have a list of all rules and offer a method to extract KnowledgeBase from
 * the document.
 */
class Rules(val rules: Set<Rule>,
            val rewards: Set<Reward> = setOf()) {
    /**
     * Apply the rules and return a KnowledgeBase about the document
     */
    fun score(document: Document): KnowledgeBase {
        val ruleResults = mutableMapOf<String, MutableSet<Score>>()

        // Apply dom rules
        for (rule in rules) {
            for (element in document.select(rule.selector)) {
                val result = rule.ranker(element)

                if (!ruleResults.containsKey(result.flavor)) {
                    ruleResults[result.flavor] = mutableSetOf()
                }

                ruleResults[result.flavor]?.add(result)
            }
        }

        // Apply flavors rules
        for (reward in rewards) {
            for ((key, value) in ruleResults.filter { it.key == reward.selector }) {
                for (node in value) {
                    val punishmentResult = reward.ranker(node.element)
                    node.score *= punishmentResult.score
                    node.metadata.putAll(punishmentResult.metadata)
                }
            }
        }

        return KnowledgeBase(ruleResults)
    }
}

