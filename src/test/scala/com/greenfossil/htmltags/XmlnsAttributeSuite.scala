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

class XmlnsAttributeSuite extends munit.FunSuite {

  test("Xmlns attribute") {
    val attr = xmlns:="http://www.w3.org/2000/svg"
    assertNoDiff(attr.render, """xmlns="http://www.w3.org/2000/svg"""")
  }

  test("Xmlns with name space") {
    val attr = xlink:="http://www.w3.org/1999/xlink"
    assertNoDiff(attr.render, """xmlns:xlink="http://www.w3.org/1999/xlink"""")

    val ref = href("xlink"):="http://www.greenfossil.com"
    assertNoDiff(ref.render, """xlink:href="http://www.greenfossil.com"""")
  }


}
