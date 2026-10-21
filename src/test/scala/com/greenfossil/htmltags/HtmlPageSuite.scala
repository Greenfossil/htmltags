package com.greenfossil.htmltags

import scala.language.implicitConversions

class HtmlPageSuite extends munit.FunSuite {

  test("renders the doctype, title, charset and body content") {
    val page = HtmlPage("Example")(div(cls := "content")("Hello"))
    val html = page.render
    assert(html.startsWith("<!DOCTYPE html>"), html.take(40))
    assert(html.contains("<title>Example</title>"))
    assert(html.contains("charset=\"utf-8\""))
    assert(html.contains("<div class=\"content\">Hello</div>"))
  }

  test("hoists link, style and nested assets while preserving order") {
    val page = HtmlPage("Example")(
      link(rel := "stylesheet", href := "/a.css"),
      div(
        span("body"),
        script(src := "/nested.js")
      ),
      embeddedStyle("body{color:red}"),
      script(src := "/top.js")
    )
    val head = page.head.render
    val body = page.body.render
    assert(head.indexOf("/a.css") < head.indexOf("body{color:red}"))
    assert(body.contains("<span>body</span>"))
    assert(body.indexOf("/nested.js") >= 0)
    assert(body.indexOf("/top.js") < body.indexOf("/nested.js"))
  }

  test("preserves supplied head/body tags and body attributes") {
    val page = HtmlPage.toHeadBody("Example", Seq(cls := "mode", attr("data-x") := "1"))(
      Seq(
        head(meta(name := "description", content := "desc")),
        body(div("content"))
      )
    )
    val (headTag, bodyTag) = page
    assert(headTag.render.contains("description"))
    assert(bodyTag.render.contains("class=\"mode\""))
    assert(bodyTag.render.contains("data-x=\"1\""))
    assert(bodyTag.render.contains("<div>content</div>"))
  }

  test("flattens Frag nodes") {
    val page = HtmlPage("Example")(Frag(Seq(div("one"), div("two"))))
    assert(page.body.render.contains("<div>one</div>"))
    assert(page.body.render.contains("<div>two</div>"))
  }

  test("using keeps the supplied head and body") {
    val base = HtmlPage("Example")(div("old"))
    val updated = base.using(Seq(head(meta(name := "x", content := "y")), body(div("new"))))
    assert(updated.head.render.contains("content=\"y\""))
    assert(updated.body.render.contains("<div>new</div>"))
  }
}
