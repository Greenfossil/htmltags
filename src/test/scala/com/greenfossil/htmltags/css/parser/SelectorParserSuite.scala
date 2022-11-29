/*
 * Copyright 2022 Greenfossil Pte Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greenfossil.htmltags.css.parser

/**
  * Created by chungonn on 11/7/17.
  */
class SelectorParserSuite extends munit.FunSuite {

  val selectors = List("*", "E", "E[foo=bar]", "E[foo~=bar]", "E[foo^=bar]", "E[foo$=bar]", "E[foo*=bar]", "E[foo|=en]",
    "E:root", "E:nth-child(n)", "E:nth-last-child(n)", "E:nth-of-type(n)", "E:nth-last-of-type(n)",
    "E:first-child", "E:last-child", "E:first-of-type", "E:last-of-type", "E:last-of-type", "E:only-of-type",
    "E:empty", "E#myid", "E:not(s)", "E F", "E > F", "E + F", "E ~ F"
  )

  test("SelectorParser") {
    selectors.foreach { selector =>
      val selectorGroups = SelectorParser.parse(selector)
      val str = selectorGroups.mkString(", ")
      assertNoDiff(str, selector)
    }
  }

  test("Test for input[name=orderables[0].price] ") {
    val selector = """input[name="orderables[0].price"]"""
    val selectorGroups = SelectorParser.parse(selector)
    val str = selectorGroups.mkString(", ")
    assertNoDiff(str, "input[name=orderables[0].price]")
  }

  test("Test for a[href=#] ") {
    val selector = """a[href="#1"]"""
    val selectorGroups = SelectorParser.parse(selector)
    val str = selectorGroups.mkString(", ")
    assertNoDiff(str,"a[href=#1]")
  }

}
