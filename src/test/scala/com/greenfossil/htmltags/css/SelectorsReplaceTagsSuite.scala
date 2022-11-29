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

class SelectorsReplaceTagsSuite extends munit.FunSuite {

  test("ReplaceTag"){
    val orig = div("parent",
      div("child", input("input1")),
      input("input2")
    )
    val frag = Selectors.replaceNodes("input", cls:="ui btn", label("hello world"), div("grandChild"))(
      orig
    )

    assertNoDiff(frag.render, """<div>parent<div>child<input class="ui btn"><label>hello world</label><div>grandChild</div></input></div><input class="ui btn"><label>hello world</label><div>grandChild</div></input></div>""")
  }

}
