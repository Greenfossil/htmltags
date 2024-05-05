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

package com.greenfossil.htmltags.css

import com.greenfossil.htmltags.*

import scala.language.implicitConversions

/**
  * Created by chungonn on 9/11/16.
  */
class SelectorsQuerySuite extends munit.FunSuite{

  /*
   * https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes
   * Wrote test cases based on the Pseudo-class found above
   * Expected results based on specs
   */

  val p1 = p("First paragraph")
  val p2 = p("Second paragraph")
  val p3a = p("nested div, first paragraph")
  val p3b = p("nested div, second paragraph")
  val p4a = p("nested div 2, first child p")
  val p4b = p("nested div 2, second child p")
  val h2_one = h2("nested div, second child h2")
  val h2_two = h2("nested div 2, first child h2")

  val sampleCode =
      div(
        p1,
        p2,
        div(
          p3a,
          p3b,
          h2_one
        ),
        div(
          p4a,
          h2_two,
          p4b
        )
      )

  def render(cssSelectorString: String, codeToTestAgainst: Tag): String = {
    codeToTestAgainst.cssQuery(cssSelectorString).render
  }

  def renderFromSampleCode(cssSelectorString: String) = render(cssSelectorString, sampleCode)

  test("Test for E:first-child") {
    val result = renderFromSampleCode("p:first-child")
    assertNoDiff(result, p1.render + p3a.render + p4a.render)
  }

  test("Test for E:last-child") {
    val result = renderFromSampleCode("p:last-child")
    assertNoDiff(result, p4b.render)
  }

  test("Test for first-of-type"){
    val result = renderFromSampleCode("p:first-of-type")
    val expectedMarkup = p1.render + p3a.render + p4a.render
    assertNoDiff(result, expectedMarkup)
  }

  test("Test for last-of-type"){
    val result = renderFromSampleCode("p:last-of-type")
    val expectedMarkup = p2.render + p3b.render + p4b.render
    assertNoDiff(result, expectedMarkup)
  }

  test("Test for nth-of-type"){
    val cssQuery = "p:nth-of-type(1)"
    val result = renderFromSampleCode(cssQuery)
    val expectedMarkup = p1.render + p3a.render + p4a.render
    assertNoDiff(result, expectedMarkup)

    val cssQuery1 = "p:nth-of-type(odd)"
    val result1 = renderFromSampleCode(cssQuery1)
    val expectedMarkup1 = p1.render + p3a.render + p4a.render
    assertNoDiff(result1, expectedMarkup1)

    val cssQuery2 = "p:nth-of-type(even)"
    val result2 = renderFromSampleCode(cssQuery2)
    val expectedMarkup2 = p2.render + p3b.render + p4b.render
    assertNoDiff(result2, expectedMarkup2)

  }

  test("Test for nth-child"){
    val cssQueryOdd = "p:nth-child(odd)"
    val result = renderFromSampleCode(cssQueryOdd)

    val expectedMarkupOdd = p1.render+p3a.render+p4a.render+p4b.render
    assertNoDiff(result, expectedMarkupOdd)

    val cssQueryEven = "p:nth-child(even)"
    val resultEven = renderFromSampleCode(cssQueryEven)

    val expectedMarkupEven = p2.render+p3b.render
    assertNoDiff(resultEven, expectedMarkupEven)
  }

  test("Test for nth-last-child"){
    val cssQueryOdd = "p:nth-last-child(odd)"
    val result = renderFromSampleCode(cssQueryOdd)

    val expectedMarkupOdd = p2.render+p3a.render+p4a.render+p4b.render
    assertNoDiff(result, expectedMarkupOdd)

    val cssQueryEven = "p:nth-last-child(even)"
    val resultEven = renderFromSampleCode(cssQueryEven)

    val expectedMarkupEven = p1.render+p3b.render
    assertNoDiff(resultEven, expectedMarkupEven)
  }

  val onlyChildSampleCode =
    div(
      p("test first paragraph"),
      div(
        p("only child 1")
      ),
      div(
        p("not only child1"),
        p("not only child2")
      ),
      div(
        p("only child 2")
      )
    )

  test("Test for only-child"){
    val cssQuery = "p:only-child"
    val result = render(cssQuery, onlyChildSampleCode)
    val expectedMarkup = p("only child 1").render + p("only child 2").render
    assertNoDiff(result, expectedMarkup)
  }

  /*
   * Tests cases - CSS selector then modify
   */
  val sampleInputCode = div(
    input(name:="input1", cls:="input1"),
    input(name:="input2", cls:="input2"),
    input(name:="input3", cls:="input3"),
    input(name:="input4", cls:="input4")
  )

  test("change even input fields"){
    val cssQueryForEvenInputFields = "input:nth-child(even)"
    val result = sampleInputCode.addNodes(cssQueryForEvenInputFields, color:="red")

    val expectedResult = div(
      input(name:="input1", cls:="input1"),
      input(name:="input2", cls:="input2", color:="red"),
      input(name:="input3", cls:="input3"),
      input(name:="input4", cls:="input4", color:="red")
    )

    assertNoDiff(result.render, expectedResult.render)
  }

  test("change first type of input"){
    val cssQuery = "input:first-of-type"
    val result = sampleInputCode.addNodes(cssQuery, color:="red")
    val expectedResult = div(
      input(name:="input1", cls:="input1", color:="red"),
      input(name:="input2", cls:="input2"),
      input(name:="input3", cls:="input3"),
      input(name:="input4", cls:="input4")
    )

    assertNoDiff(result.render, expectedResult.render)
  }

}
