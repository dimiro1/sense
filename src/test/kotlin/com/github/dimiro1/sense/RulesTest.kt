// Copyright 2016 Claudemiro Alves Feitosa Neto. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE.txt file.

package com.github.dimiro1.sense

import com.github.dimiro1.sense.dsl.ruleset
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test

class RulesTest {

    private fun haveDashes(text: String) = text.contains("-")

    @Test
    fun basicTest() {
        val titleFinder = ruleset {
            rule("metadata[property=og:title]") { Score(50.0, "title", it) }
            rule("title") { Score(40.0, "title", it) }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""
            <html>
                <head>
                    <title>Hello-World</title>
                    <metadata property="og:title" content="World Hello">
                </head>
            </html>
        """))

        val bestMatch = knowledgeBase.max("title")
        val matches = knowledgeBase.nodesOfFlavor("title")

        assertNotNull(bestMatch)
        assertEquals(50.0, bestMatch?.score)
        assertEquals(2, matches.size)
    }

    @Test
    fun noMatches() {
        val titleFinder = ruleset {
            rule("title") { Score(50.0, "title", it) }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""<html></html>"""))

        val bestMatch = knowledgeBase.max("title")
        val matches = knowledgeBase.nodesOfFlavor("title")

        assertNull(bestMatch)
        assertEquals(0, matches.size)
    }

    @Test
    fun moreThanOneMatchInTheSameRule() {
        val titleFinder = ruleset {
            rule("p") { Score(50.0, "title", it) }
            reward("title") { Amount(if (haveDashes(it.text())) 0.5 else 1.0) }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""
            <html>
                <p>Better title</p>
                <p>Worse-title</p>
            </html>
        """))

        val bestMatch = knowledgeBase.max("title")
        val matches = knowledgeBase.nodesOfFlavor("title")

        assertEquals(2, matches.size)

        assertNotNull(bestMatch)
        assertEquals(50.0, bestMatch?.score)

        assertEquals(50.0, matches.first().score, 0.1)
        assertEquals(25.0, matches.last().score, 0.1)
    }

    @Test
    fun metadata() {
        val titleFinder = ruleset {
            rule("a") { Score(50.0, "title", it, mutableMapOf("text" to it.attr("data-title"))) }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""
            <html>
                <a href="http://google.com" data-title="The title from data-title">The Link</a>
            </html>
        """))

        val bestMatch = knowledgeBase.max("title")
        assertNotNull(bestMatch)
        assertEquals("The title from data-title", bestMatch?.metadata("text"))
    }

    @Test
    fun punishment() {
        val titleFinder = ruleset {
            rule("metadata[property=og:title]") { Score(50.0, "title", it) }
            rule("title") { Score(40.0, "title", it) }
            reward("title") {
                Amount(if (haveDashes(it.text())) 0.5 else 1.0, mutableMapOf("reason" to "have dashes"))
            }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""
            <html>
                <head>
                    <title>Title that have a underscore - SiteName</title>
                    <metadata property="og:title" content="OG Title">
                </head>
            </html>
        """))

        val matches = knowledgeBase.nodesOfFlavor("title")

        assertEquals(2, matches.size)
        assertEquals(50.0, matches.first().score, 0.1)
        assertEquals(20.0, matches.last().score, 0.1)
        assertEquals("have dashes", matches.last().metadata("reason"))
    }

    @Test
    fun invalidPunishment() {
        val titleFinder = ruleset {
            rule("metadata[property=og:title]") { Score(50.0, "title", it) }
            rule("title") { Score(40.0, "title", it) }
            reward("invalid") {
                Amount(if (haveDashes(it.text())) 0.5 else 1.0, mutableMapOf("reason" to "have dashes"))
            }
        }

        val knowledgeBase = titleFinder.score(Jsoup.parse("""
            <html>
                <head>
                    <title>Title that have a underscore - SiteName</title>
                    <metadata property="og:title" content="OG Title">
                </head>
            </html>
        """))

        val matches = knowledgeBase.nodesOfFlavor("title")

        assertEquals(2, matches.size)
        assertEquals(50.0, matches.first().score, 0.1)
        assertEquals(40.0, matches.last().score, 0.1)
    }
}