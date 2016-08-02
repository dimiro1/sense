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

class Knowledge(var rules: Rules = Rules(),
                var rewards: Rewards = Rewards()) {
    fun rules(init: Rules.() -> Unit): Rules {
        val dslRules = Rules()
        dslRules.init()
        rules = dslRules
        return dslRules
    }

    fun rewards(init: Rewards.() -> Unit): Rewards {
        val dslRewards = Rewards()
        dslRewards.init()
        rewards = dslRewards
        return dslRewards
    }
}

class Rewards(val rewards: MutableSet<Reward> = mutableSetOf()) {
    fun reward(selector: String, ranker: (Element) -> Amount): Reward {
        val dslReward = Reward(selector, ranker)
        rewards.add(dslReward)
        return dslReward
    }
}

class Rules(val rules: MutableSet<Rule> = mutableSetOf()) {
    fun rule(selector: String, ranker: (Element) -> Score): Rule {
        val rule = Rule(selector, ranker)
        rules.add(rule)
        return rule
    }
}

fun ruleset(init: Knowledge.() -> Unit): SenseRules {
    val knowledge = Knowledge()
    knowledge.init()
    return SenseRules(knowledge.rules.rules, knowledge.rewards.rewards)
}
