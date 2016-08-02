// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

@file:JvmName("Ruleset")

package com.github.dimiro1.sense.dsl

import com.github.dimiro1.sense.Amount
import com.github.dimiro1.sense.Score
import com.github.dimiro1.sense.Rule
import com.github.dimiro1.sense.Reward
import org.jsoup.nodes.Element
import com.github.dimiro1.sense.Rules as SenseRules

class Knowledge(val rules: MutableSet<Rule> = mutableSetOf(),
                val rewards: MutableSet<Reward> = mutableSetOf()) {

    fun rule(selector: String, ranker: (Element) -> Score): Rule {
        val rule = Rule(selector, ranker)
        rules.add(rule)
        return rule
    }

    fun reward(selector: String, ranker: (Element) -> Amount): Reward {
        val dslReward = Reward(selector, ranker)
        rewards.add(dslReward)
        return dslReward
    }
}

fun ruleset(init: Knowledge.() -> Unit): SenseRules {
    val knowledge = Knowledge()
    knowledge.init()
    return SenseRules(knowledge.rules, knowledge.rewards)
}
