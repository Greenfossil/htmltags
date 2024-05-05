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

class TagSuite extends munit.FunSuite {

  test("Tag.addAsFirstChild") {
    val tag =
      div(cls:="container")(
        div(cls:="firstDiv",
          div("test"),
          div("something")
        )
      )
    val addNewFirstChild = div("first", div("child"))

    val result = tag.addAsFirstChild(".firstDiv", addNewFirstChild)

    val expectedResult =
      div(cls:="container")(
        div(cls:="firstDiv",
          addNewFirstChild,
          div("test"),
          div("something")
        )
      )

    assertEquals(result, expectedResult)
  }

  test("Tag.deleteFromAttribute") {
    val tag =
      div(cls:="ui huge blue container")(
        div(cls:="firstDiv huge blue header",
          div("test"),
          div("something")
        )
      )

    val result = tag.deleteFromAttribute(".firstDiv",  cls:="huge blue")

    val expectedResult =
      div(cls:="ui huge blue container")(
        div(cls:="firstDiv header",
          div("test"),
          div("something")
        )
      )

    assertEquals(result, expectedResult)
  }

  test("Tag.modifyAttribute") {
    val tag =
      div(cls:="ui huge blue container")(
        div(cls:="firstDiv huge blue header",
          div("test"),
          div("something")
        )
      )

    def modFn(attr: Attribute): Option[Attribute] = {
      val x = if(attr.rawValue.contains("blue")) attr.findAndReplaceValue("blue", "green") else attr
      Option(x)
    }

    val result = tag.modifyAttribute(".firstDiv", cls, modFn)

    val expectedResult =
      div(cls:="ui huge blue container")(
        div(cls:="firstDiv huge green header",
          div("test"),
          div("something")
        )
      )

    assertNoDiff(result.prettyPrint, """|<div class="ui huge blue container">
                                        |  <div class="firstDiv huge green header">
                                        |    <div>test</div>
                                        |    <div>something</div>
                                        |  </div>
                                        |</div>
                                        |""".stripMargin)
    assertEquals(result, expectedResult)

  }

  import scala.util.Try
  test("scala.util.Try - Success") {

    val tag = div(Try("Hello World!"))
    assertEquals(tag.render, "<div>Hello World!</div>")
  }

  test("scala.util.Try - Failure") {
    import scala.util.Try
    val tag = div(Try(throw IllegalArgumentException("Bad Argument")))
    
    assertEquals(tag.render, "<div>Throwable caught: java.lang.IllegalArgumentException - message:Bad Argument</div>")
  }

  test("Try.map - success"){
    val tryTag = Try("Hello World!").map(s => div(s))
    assertEquals(tryTag.render, "<div>Hello World!</div>")
  }

  test("Try.map - failure") {
    val tryTag = Try(throw IllegalArgumentException("Bad Argument")).map(s => div(s))
    assertEquals(tryTag.render, "Throwable caught: java.lang.IllegalArgumentException - message:Bad Argument")
  }

  test("aTarget") {
    val atag = aTarget
    assertNoDiff(atag.render, """<a target="__single_global_anchor_tab__"></a>""")
  }

  test("aTarget()") {
    val atag = aTarget("_self")
    assertNoDiff(atag.render, """<a target="_self"></a>""")
  }

}
