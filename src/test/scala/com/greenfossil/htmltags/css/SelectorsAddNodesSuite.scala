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
  * Created by chungonn on 1/10/16.
  */
import com.greenfossil.htmltags.*

import scala.language.implicitConversions

class SelectorsAddNodesSuite extends munit.FunSuite {

  test("Add Attribute by Id") {
    val orig = div(
      a(href := "#", "hello world")(cls := "ui", id:="a1"),
      div(
        a("New world")(cls := "ui")
      )
    )

    val frag = Selectors.addNodes("#a1", "class" := "inline")(orig)
    assertNoDiff(frag.render, """<div><a href="#" class="ui inline" id="a1">hello world</a><div><a class="ui">New world</a></div></div>""")
  }

  test("Add Attribute by Class") {
    val orig = div(
      a("hello world")(cls := "ui", id:="a1"),
      div(
        a("New world")(cls := "ui btn")
      )
    )
    val frag = Selectors.addNodes(".ui", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a class="ui" id="a1" type="newtext">hello world</a><div><a class="ui btn" type="newtext">New world</a></div></div>""")
  }

  test("select by multiple class value") {
    val orig = div(
      a("hello world")(cls := "ui"),
      div(
        a("New world")(cls := "ui btn")
      )
    )
    val frag = Selectors.addNodes(".ui.btn", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a class="ui">hello world</a><div><a class="ui btn" type="newtext">New world</a></div></div>""")
  }

  test("select by tag with class") {
    val orig = div(
      a("hello world")(cls := "ui"),
      input("New world")(cls := "ui")
    )
    val frag = Selectors.addNodes("a.ui", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a class="ui" type="newtext">hello world</a><input class="ui">New world</input></div>""")
  }

  test("select by attr-type") {
    val orig = div(
      a("hello world")(href := "#1"),
      div(a("New world")(href := "#2"))
    )
    val frag = Selectors.addNodes("[href]", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a href="#1" type="newtext">hello world</a><div><a href="#2" type="newtext">New world</a></div></div>""")
  }

  test("select by attr-type and value") {
    val orig = div(
      a("hello world")(href := "_1"),
      div(a("New world")(href := "_2"))
    )
    val frag = Selectors.addNodes("[href=_1]", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a href="_1" type="newtext">hello world</a><div><a href="_2">New world</a></div></div>""")
  }

  //Fixme - select parser needs enhancements
  test("select by attr-type and value of array") {
    val orig = div(
      input(name := "orderables[0].code"),
      input(name := "orderables[0].price", cls:="ui")
    )
    val frag = Selectors.addNodes("input[name=\"orderables[0].price\"]", "class" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><input name="orderables[0].code"/><input name="orderables[0].price" class="ui newtext"/></div>""")
  }

  test("select by tag and attr-type and value") {
    val orig = div(
      a("hello world")(href := "#1"),
      div("New world")(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", "type" := "newtext")(orig)
    assertNoDiff(frag.render, """<div><a href="#1">hello world</a><div href="#2" type="newtext">New world</div></div>""")
  }

  test("add additional text to existing Text") {
    val orig = div(
      div("New world")(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", span("howdy"), " Is Beginning")(orig)
    assertNoDiff(frag.render, """<div><div href="#2">New world Is Beginning<span>howdy</span></div></div>""")
  }

  test("add additional text preceeded by <i>") {
    val orig = div(
      div(i("icon"),"New world")(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", span("howdy"), " Is Beginning")(orig)
    assertNoDiff(frag.render, """<div><div href="#2"><i>icon</i>New world Is Beginning<span>howdy</span></div></div>""")
  }

  test("add additional text followed by <i>") {
    val orig = div(
      div("New world", i("icon"))(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", span("howdy"), " Is Beginning")(orig)
    assertNoDiff(frag.render, """<div><div href="#2">New world Is Beginning<i>icon</i><span>howdy</span></div></div>""")
  }

  test("add additional text followed by <i> and Text") {
    val orig = div(
      div("New world", i("icon"), "First world")(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", span("howdy"), " Is Beginning")(orig)
    assertNoDiff(frag.render, """<div><div href="#2">New world<i>icon</i>First world Is Beginning<span>howdy</span></div></div>""")
  }

  test("add additional text without existing Text") {
    val orig = div(
      div(i("icon"))(href := "#2")
    )
    val frag = Selectors.addNodes("div[href]", span("howdy"), " Is Beginning")(orig)
    assertNoDiff(frag.render, """<div><div href="#2"><i>icon</i><span>howdy</span> Is Beginning</div></div>""")
  }


}
