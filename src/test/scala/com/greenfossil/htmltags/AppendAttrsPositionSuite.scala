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

/**
  * Created by chungonn on 1/10/16.
  */
class AppendAttrsPositionSuite extends munit.FunSuite{


  test("append attr by with prefix position") {
    val tag = div(cls := "btn")
    assertNoDiff(tag.appendAttrs(PREFIX, cls := "ui").render, """<div class="ui btn"></div>""")
  }

  test("append attr by with postfix position") {
    val tag = div(cls := "ui")
    assertNoDiff(tag.appendAttrs(POSTFIX, cls := "btn").render, """<div class="ui btn"></div>""")
  }

  test("Remove attr tokens from attr") {
    val attr = cls:="ui blue button"
    val newAttr = attr.removeTokens("ui blue ")
    assertNoDiff(newAttr.render, "class=\"button\"")
  }

  test("Remove attr token from Tag") {
    val tag = div(cls := "ui blue button")
    assertNoDiff(tag.removeAttrsTokens(cls:="ui button").render, """<div class="blue"></div>""")
  }
}
