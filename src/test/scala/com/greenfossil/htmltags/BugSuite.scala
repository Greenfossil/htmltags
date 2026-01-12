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

class BugSuite extends munit.FunSuite {

  test("ifTrue lazy evaluation") {
    val index = -1
    val xs = List(1, 2, 3)
    try {
      ifTrue(index != -1, div(xs(index)))
    }catch {
      case ex: Exception =>
        fail(ex.getMessage)
    }
  }

  test("attribute replace with multiple attributes") {
    val tag = div(
      div(cls := "red blue yellow", name := "nameAttr")
    )

    val replacedTag = tag.modifyAttribute(".red", cls, a => Option(a.findAndReplaceValue("blue", "green")))

    assertEquals(replacedTag, div(div(cls := "red green yellow", name := "nameAttr"))
    )
  }

}
