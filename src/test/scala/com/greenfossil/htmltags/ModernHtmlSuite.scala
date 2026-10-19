package com.greenfossil.htmltags

/** The modern-HTML coverage additions (2026-10 audit) and the typed ARIA names.
 *  The audit deliberately listed these as future public API; each is pinned here. */
class ModernHtmlSuite extends munit.FunSuite:

  test("search and slot are first-class semantic tags"):
    assertNoDiff(search.render, "<search></search>")
    assertNoDiff(slot.render, "<slot></slot>")

  test("slot attribute is available without shadowing the slot tag"):
    assertNoDiff(span(Attrs2.slot := "title").render, """<span slot="title"></span>""")

  test("popover, invoker command, and dialog attributes render"):
    assertNoDiff(
      button(popover := "auto", popovertarget := "menu", popovertargetaction := "show",
        command := "show-modal", commandfor := "dlg").render,
      """<button popover="auto" popovertarget="menu" commandfor="dlg" popovertargetaction="show" command="show-modal"></button>""")
    assertNoDiff(div(inert).render, """<div inert=""></div>""")

  test("input, resource, and shadow-DOM hints render"):
    assertNoDiff(
      input(enterkeyhint := "send", inputmode := "numeric").render,
      """<input enterkeyhint="send" inputmode="numeric"/>""")
    assertNoDiff(
      img(fetchpriority := "high", decoding := "async").render,
      """<img fetchpriority="high" decoding="async"/>""")
    assertNoDiff(
      template(shadowrootmode := "open", shadowrootdelegatesfocus, part := "card").render,
      """<template shadowrootmode="open" shadowrootdelegatesfocus="" part="card"/>""")

  test("hyphenated HTML attributes and CSS properties use camelCase Scala bindings"):
    assertNoDiff(meta(httpEquiv := "content-type").render, """<meta http-equiv="content-type">""")
    assertNoDiff(form(acceptCharset := "UTF-8").render, """<form accept-charset="UTF-8"></form>""")
    assertNoDiff(div(borderColor := "red").render, """<div style="border-color: red;"></div>""")
    assertNoDiff(td(colspan := 2).render, """<td colspan="2"></td>""")

  test("typed ARIA names emit the hyphenated attributes"):
    assertNoDiff(
      section(aria.labelledby := "title", aria.describedby := "question",
        aria.live := "polite", aria.hidden := true).render,
      """<section aria-labelledby="title" aria-describedby="question" aria-live="polite" aria-hidden="true"></section>""")

  test("typed ARIA names are exact aliases of the attr bindings"):
    val typed = aria.labelledby := "title"
    val raw = attr("aria-labelledby") := "title"
    assertEquals(typed, raw)
    assertNoDiff(typed.render, raw.render)
    assertNoDiff(typed.render, """aria-labelledby="title"""")
    // An uncovered name is a DynAttribute: same HTML channel, different type.
    val dynamic = aria.autocomplete := "list"
    assertNotEquals(dynamic, attr("aria-autocomplete") := "list")
    assertNoDiff(dynamic.render, """aria-autocomplete="list"""")

  test("dynamic ARIA fallback remains available"):
    assertNoDiff(span(aria.autocomplete := "legacy").render, """<span aria-autocomplete="legacy"></span>""")

end ModernHtmlSuite
