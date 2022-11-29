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

/**
  * Created by chungonn on 6/12/16.
  */

import com.greenfossil.htmltags.*

import scala.language.implicitConversions

class SelectorsDeleteTagsSuite extends munit.FunSuite {

  test("DeleteTags") {
    val orig = div("parent",
      a(href := "1", cls := "ui inline", "hello world!"),
      div("child", label("label"), input("input2")),
      input("input1")
    )

    val cssSelector = "input, label, a[href]"
    val frag = Selectors.deleteTags(cssSelector)(orig)

    assertNoDiff(frag.render, """<div>parent<div>child</div></div>""" )
  }

  test("DeleteTag's attribute if exists") {
    val orig = div("parent",
      a(href := "blue", cls := "ui blue button", "hello world!"),
      div("child", label("label"), input(cls:="ui blue button", "input2")),
      input(cls:="ui blue button", "input1")
    )

    /*
     * If tag does not have the attribute it would be left unchanged
     */
    val cssSelector = "input, label, a[href]"
    val frag = Selectors.deleteTags(cssSelector, cls:="blue")(orig)
    assertNoDiff(frag.render, """<div>parent<a href="blue" class="ui button">hello world!</a><div>child<label>label</label><input class="ui button">input2</input></div><input class="ui button">input1</input></div>"""
      )
  }


}
