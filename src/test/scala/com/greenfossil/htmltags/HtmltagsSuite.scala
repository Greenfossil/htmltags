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

/**
  * Created by chungonn on 24/9/16.
  */
class HtmltagsSuite extends munit.FunSuite {

  test("Tag with Text") {
    val tag = div("hello world!")
    assertEquals(tag, div(Seq(StringText("hello world!"))))
    assertNoDiff(tag.render, "<div>hello world!</div>")
  }

  test("Tag with attribute") {
    val tag = input("class" := "ui button")
    assertEquals(tag, input(Seq(StaticAttribute("class", "ui button"))))
    assertNoDiff(tag.render, """<input class="ui button"/>""")
  }

  test("Tag with attribute and Text") {
    val tag = div("class" := "ui button", "data-list" := "abc", "hello world!")
    assertEquals(tag, div(Seq(StaticAttribute("class", "ui button"), StaticAttribute("data-list", "abc"), StringText("hello world!"))))
    assertNoDiff(tag.render, """<div class="ui button" data-list="abc">hello world!</div>""")
  }

  test("Nested tag") {
    val nestedTag = div("Outer", div("Inner"))
    assertEquals(nestedTag, div(Seq(StringText("Outer"), PairedTag("div", Seq(StringText("Inner"))))))
    assertNoDiff(nestedTag.render, """<div>Outer<div>Inner</div></div>""")
  }

  test("Escape TextString") {
    val tag = div("<hello>")
    assertNoDiff(tag.render, "<div>&lt;hello&gt;</div>")
  }

  test("Escape Attribute") {
    val tag = div(cls := "'")
    assertNoDiff(tag.render, """<div class="&apos;"></div>""")
  }

  test("Reversing character entity TextString") {

    val escapedString = div("<hello>").render
    assertNoDiff(StringText(escapedString).nonEscapedValue, "<div><hello></div>")

    val escapedString2 = div("A&E").render
    assertNoDiff(StringText(escapedString2).nonEscapedValue, "<div>A&E</div>")

    val escapedString3 = div("Today's Date").render
    assertNoDiff(StringText(escapedString3).nonEscapedValue, "<div>Today's Date</div>")

    val nonEscapedString = div("Hello world").render
    assertNoDiff(StringText(nonEscapedString).nonEscapedValue, "<div>Hello world</div>")

    assertNoDiff(StringText(null).nonEscapedValue, "")

  }

  test("Reversing character entity String"){
    val escapedString = StringText("Today&#x27;s Admission")
    assertNoDiff(escapedString.nonEscapedValue, "Today's Admission")

    val escapedString2 = StringText("Today&#X27;s Admission")
    assertNoDiff(escapedString2.nonEscapedValue, "Today's Admission")

    val escapedMultipleString = StringText("&#x3C;Today&#x27;s Admission&#x3E;")
    assertNoDiff(escapedMultipleString.nonEscapedValue, "<Today's Admission>")

    val escapedMixedString = StringText("&lt;Today&#x27;s Admission&gt;")
    assertNoDiff(escapedMixedString.nonEscapedValue, "<Today's Admission>")
  }


  test("Self closing tag") {
    assertEquals(hr.render, "<hr/>")
    assertNoDiff(hr(cls := "ui header").render, """<hr class="ui header"/>""")
  }

  test("CSS Attrs") {
    val tag = input(cls:="btn", borderColor:="yellow", margin:=2.px)
    assertNoDiff(tag.render, """<input class="btn" style="border-color: yellow; margin: 2px;"/>""")
  }

  test("Predefined attribute") {
    val btnTag = button(id := "submit-btn", tpe := "submit", value := "1")
    assertNoDiff(btnTag.render, """<button id="submit-btn" type="submit" value="1"></button>""")

    val inputTag = input(tpe := "text", name := "amount", value := "1")
    assertNoDiff(inputTag.render, """<input type="text" name="amount" value="1"/>""")
  }

  test("Frag") {
    val fragTag = form(id := "form")(
      label("Name"),
      Seq(
        input(tpe := "text", "firstname"),
        input(tpe := "text", "lastname")
      ),
      button(tpe := "submit")
    )

    assertEquals(fragTag,
      form(
        StaticAttribute("id", "form"),
        PairedTag("label")("Name"),
        Frag(
          Seq(
            UnPairedTag("input")(StaticAttribute("type", "text"), StringText("firstname")),
            UnPairedTag("input")(StaticAttribute("type", "text"), StringText("lastname"))
          )
        ),
        PairedTag("button")(StaticAttribute("type", "submit"))
      )
    )
    assertNoDiff(fragTag.render, """<form id="form"><label>Name</label><input type="text">firstname</input><input type="text">lastname</input><button type="submit"></button></form>""")
  }

  test("css units") {
    val tag = div(cls := "ui button", marginTop := 1.1.em)
    assertNoDiff(tag.render, """<div class="ui button" style="margin-top: 1.1em;"></div>""")
  }

  test("Dynamic attribute") {
    val dataAttr = data.list := "test"
    assertEquals(dataAttr, DynAttribute("data-list", "test"))
  }

  test("nested dynamic attribute") {
    val dataAttr = data.click.count := 0
    assertEquals(dataAttr.render, DynAttribute("data-click-count", "0").render)
  }

  test("Seq of Attributes") {
    val tag = div(cls:="ui", Seq(href:="#", tpe:="text"))
    assertNoDiff(tag.render, """<div class="ui" href="#" type="text"></div>""")
  }

  test("White-space attribute") {
    val tag = div(whiteSpace:="nowrap", wordWrap :="true", id:="#")
    assertEquals(tag.render, """<div id="#" style="white-space: nowrap; word-wrap: true;"></div>""")
  }

  test("CSS backgroundImage") {
    val tag = div(cls:="cover-photo", backgroundImage:= raw(s"url('/test/url')"), style:="background-repeat: no-repeat; background-size: contain")
    assertNoDiff(tag.render, """<div class="cover-photo" style="background-repeat: no-repeat; background-size: contain; background-image: url('/test/url');"></div>""")
  }

  test("script tag not to be escaped") {
    val s = script("//hello world")
    assertNoDiff(s.render, """<script>//hello world</script>""")
  }
}
