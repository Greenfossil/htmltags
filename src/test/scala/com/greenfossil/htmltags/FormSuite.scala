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

package com.greenfossil.htmltags

import scala.language.implicitConversions

class FormSuite extends munit.FunSuite {

  test("inputs") {
    val inputs = Form.inputs(("hidden", "apple",1), "orange" -> "two", "pear" -> null)

    val output = div(inputs).render

    assertNoDiff(output,  """<div><input type="hidden" name="apple" value="1"/><input name="orange" value="two"/></div>""")

  }

  test("input texts") {
    val texts = Form.text("apple" -> 1, "orange" -> "two", "pear" -> null)

    val output = div(texts).render

    assertNoDiff(output, """<div><input type="text" name="apple" value="1"/><input type="text" name="orange" value="two"/><input type="text" name="pear" value=""/></div>""")

  }

  test("input hidden") {
    val hiddens = Form.hidden("apple" -> 1, "orange" -> "two", "pear" -> null)

    val output = div(hiddens).render

    assertNoDiff(output, """<div><input type="hidden" name="apple" value="1"/><input type="hidden" name="orange" value="two"/><input type="hidden" name="pear" value=""/></div>""")
  }

  test("urlencoded form with tuples"){
    val form = Form.urlencoded("/action", ("hidden", "apple","1"), "orange" -> 2, ("submit", "submit", "Submit"))(id:="id", cls:="ckass")
    assertEquals(form.elems.size, 3)
    assertNoDiff(form.render, """<form method="POST" id="id" class="ckass" enctype="application/x-www-form-urlencoded" action="/action"><input type="hidden" name="apple" value="1"/><input name="orange" value="2"/><input type="submit" name="submit" value="Submit"/></form>""")
    assertNoDiff(form.prettyPrint, """|<form action="/action" class="ckass" enctype="application/x-www-form-urlencoded" id="id" method="POST">
                                      |  <input name="apple" type="hidden" value="1"/>
                                      |  <input name="orange" value="2"/>
                                      |  <input name="submit" type="submit" value="Submit"/>
                                      |</form>
                                      |""".stripMargin)
  }


  test("create urlencoded form with hidden()"){
    val form = Form.urlencoded("/action")(
      Form.hidden("apple" -> 1, "orange" -> 2),
      input(tpe:="submit", value:="Submit")
    )
    assertNoDiff(form.prettyPrint,  """|<form action="/action" enctype="application/x-www-form-urlencoded" method="POST">
                                       |  <input name="apple" type="hidden" value="1"/>
                                       |  <input name="orange" type="hidden" value="2"/>
                                       |  <input type="submit" value="Submit"/>
                                       |</form>
                                       |""".stripMargin)
  }

  test("create urlencoded form with input tags"){
    val form = Form.urlencoded("/action")(
      input(tpe:="text", name:="apple", value:="2"),
      input(tpe:="submit", value:="Submit")
    )
    assertNoDiff(form.prettyPrint, """|<form action="/action" enctype="application/x-www-form-urlencoded" method="POST">
                                      |  <input name="apple" type="text" value="2"/>
                                      |  <input type="submit" value="Submit"/>
                                      |</form>
                                      |""".stripMargin)
  }

  test("submitButton"){
    val button = Form.submitButton("/action", "apple" -> 1, "orange" -> "two")(cls:="ui button", "Click to Submit")
    assertNoDiff(button.prettyPrint, """|<form action="/action" enctype="application/x-www-form-urlencoded" method="POST">
                                        |  <input name="apple" type="hidden" value="1"/>
                                        |  <input name="orange" type="hidden" value="two"/>
                                        |  <button class="ui button" type="submit">Click to Submit</button>
                                        |</form>
                                        |""".stripMargin)
  }

}
