package com.greenfossil.htmltags

import munit.FunSuite

class LazyImgSuite extends FunSuite:

  test("lazyimg"){
    assertNoDiff(lazyImg.render, """<img loading="lazy"/>""")
    assertNoDiff(lazyImg(src:="/test").render, """<img loading="lazy" src="/test"/>""")
    assertNoDiff(lazyImg(src:="/test", alt:="Hello world").render, """<img loading="lazy" src="/test" alt="Hello world"/>""")
  }
