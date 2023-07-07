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
  * Created by chungonn on 30/9/16.
  */
class HtmltagsImplicitsSuite extends munit.FunSuite  {

  test("attr implicit") {
    val someInt = Option(1)
    val someIntTag = div(someInt.map("value" := _))
    assertNoDiff(someIntTag.render, """<div value="1"></div>""")

    val someString = Some("world")
    val someStringTag = div(someString.map("hello" := _))
    assertNoDiff(someStringTag.render, """<div hello="world"></div>""")

    val multiAttrTag = div(someInt.map("value" := _), someString.map("hello" := _))
    assertNoDiff(multiAttrTag.render, """<div value="1" hello="world"></div>""")
  }

  test("List[Node] aka Frag") {
    val fruits = List("apple", "orange", "pear")
    val fruitTag = div(fruits)
    assertNoDiff(fruitTag.render, """<div>appleorangepear</div>""")

    val vegs = List("spinach", "kailan", "potatoes")
    val basketTag = div(fruits, vegs)
    assertNoDiff(basketTag.render, """<div>appleorangepearspinachkailanpotatoes</div>""")

    val drinks: Seq[Node] =
      List("coke", "pepsi", Option("coffee"),
        div(
          List(
            Option("bread"),
            Option(1.7),
            "wt" := 3.0,
            Option(1).map("barcode" := _),
            div(
              Option("calorie").map(_ := "5"),
              "Health info"
            )
          ): Seq[Node])
      )
    val container = div(fruitTag, div(div(vegs), div(drinks)))
    assertNoDiff(container.render, """<div><div>appleorangepear</div><div><div>spinachkailanpotatoes</div><div>cokepepsicoffee<div wt="3.0" barcode="1">bread1.7<div calorie="5">Health info</div></div></div></div></div>""")
  }

  test("Any to Text") {
    val anyString: Any = "test"
    val anyDouble: Any = 1.2
    val anyLocalDate: Any = java.time.LocalDate.now
    val anyNull: Any = null
    val anyNil: Any = Nil
    val optNum: Option[Any] = Option(1)
    val tags = div("String=", anyString,  ", Double=", anyDouble, ", Date=", anyLocalDate, ", Option[Int]=", optNum, ", Null=", anyNull, ", AnyNil=", anyNil)
    println(s"any text ${tags.render}")
  }

}
