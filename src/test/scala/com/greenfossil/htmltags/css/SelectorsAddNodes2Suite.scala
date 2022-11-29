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

class SelectorsAddNodes2Suite extends munit.FunSuite {

  test("Bug extra space added for a none or empty value") {
    val orig = div(input(value := "package"))
    val frag = Selectors.addNodes("input", value := "  ")(orig)
    assertNoDiff(frag.render, """<div><input value="package"/></div>""")
  }

  test("AddTags - Attribute, Text, Tag") {
    val orig = div(a(href := "#", "hello world")(cls := "ui"))
    val frag = Selectors.addNodes("a", "href" := "apple", "class" := "inline", "type" := "newtext", "!", i(cls := "user icon"))(
      orig
    )
    assertNoDiff(frag.render, """<div><a href="# apple" class="ui inline" type="newtext">hello world!<i class="user icon"></i></a></div>""" )
  }

  test("AddTags - Nested fields - Attribute, Text, Tag ") {
    val orig = div(
      a(href := "#", "hello world")(cls := "ui"),
      div(
        a("New world")(cls := "ui")
      )
    )
    val frag = Selectors.addNodes("a", "href" := "apple", "class" := "inline", "type" := "newtext", "!")(
      orig
    )
    assertNoDiff(frag.render,
      """<div><a href="# apple" class="ui inline" type="newtext">hello world!</a><div><a class="ui inline" href="apple" type="newtext">New world!</a></div></div>"""
    )
  }

  test("multiple apply") {
    val orig = div(a(cls := "ui")(href := "#"))
    val frag = Selectors.addNodes("a", "href" := "apple", "class" := "inline", "type" := "newtext")(
      orig
    )
    assertNoDiff(frag.render, """<div><a class="ui inline" href="# apple" type="newtext"></a></div>""")
  }

  test("Extended String attr") {
    val orig = div(a(href := "#", "gf" := "green")(tpe := "link", "Hello world!"))
    val frag = Selectors.addNodes("a", "href" := "apple", "gf" := "fossil", "type" := "newtext")(
      orig
    )
    assertNoDiff(frag.render, """<div><a href="# apple" gf="green fossil" type="link newtext">Hello world!</a></div>""")
  }

  test("dynamic attribute") {
    val orig =  div(a(href := "#")(data.click.count := 0))
    val frag = Selectors.addNodes("a", data.click.count := "px")(
      orig
    )
    assertNoDiff(frag.render, """<div><a href="#" data-click-count="0 px"></a></div>""" )
  }

  test("Fields without mod fields") {
    val orig = div(
      a(href := "#", "hello world")(cls := "ui"),
      div(
        a("New world")(cls := "ui")
      )
    )
    val frag = Selectors.addNodes("input", "href" := "apple", "class" := "inline", "type" := "newtext")(
      orig
    )
    assertNoDiff(frag.render, """<div><a href="#" class="ui">hello world</a><div><a class="ui">New world</a></div></div>""")
  }

  test("Fields mixed mod fields") {
    val orig = div(
      a(href := "#", "hello world")(cls := "ui")(div(label("hello"))),
      div(input(tpe := "text", value := 10))
    )
    val frag = Selectors.addNodes("a", "href" := "apple", "class" := "inline", "type" := "newtext")(
      orig
    )
    assertNoDiff(frag.render, """<div><a href="# apple" class="ui inline" type="newtext">hello world<div><label>hello</label></div></a><div><input type="text" value="10"/></div></div>""")
  }

}
