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

/*
 * http://xahlee.info/js/html5_tags.html
 * https://developer.mozilla.org/en/docs/Web/HTML/Element
 */


/*
 * Basic elments
 */
val doctype: Tag = StartTag("!DOCTYPE")(NoValueStaticAttribute("html"))
val html = PairedTag("html")

/*
 * Document metadata
 * Note: Style and Title is found in Tags2
 */
val base = UnPairedTag("base")
val head = PairedTag("head")
val link = StartTag("link")
val meta = StartTag("meta")


/*
 * Content Sectioning
 */
val address = PairedTag("address")
val article = PairedTag("article")
val aside = PairedTag("aside")
val footer = PairedTag("footer")
val header = PairedTag("header")
val h1 = PairedTag("h1")
val h2 = PairedTag("h2")
val h3 = PairedTag("h3")
val h4 = PairedTag("h4")
val h5 = PairedTag("h5")
val h6 = PairedTag("h6")
val hgroup = PairedTag("hgroup")
val nav = PairedTag("nav")
val section = PairedTag("section")


/*
 * Text Content - Block
 */
val body = PairedTag("body")
val dd = PairedTag("dd")
val div = PairedTag("div")
val dl = PairedTag("dl")
val dt = PairedTag("dt")
val figcaption = PairedTag("figcaption")
val figure = PairedTag("figure")
val hr = UnPairedTag("hr")
val li = PairedTag("li")
val main = PairedTag("main")
val ol = PairedTag("ol")
val p = PairedTag("p")
val pre = PairedTag("pre")
val ul = PairedTag("ul")


/*
 * Inline text semantics
 */
val a = PairedTag("a")
val abbr = PairedTag("abbr")
val b = PairedTag("b")
val blockquote = PairedTag("blockquote")
val bdi = PairedTag("bdi")
val bdo = PairedTag("bdo")
val br = UnPairedTag("br")
val cite = PairedTag("cite")
val code = PairedTag("code")
val dfn = PairedTag("dfn")
val em = PairedTag("em")
val i = PairedTag("i")
val kbd = PairedTag("kbd")
val mark = PairedTag("mark")
val q = PairedTag("q")
val rp = PairedTag("rp")
val rt = PairedTag("rt")
val rtc = PairedTag("rtc")
val ruby = PairedTag("ruby")
val s = PairedTag("s")
val samp = PairedTag("samp")
val small = PairedTag("small")
val span = PairedTag("span")
val strong = PairedTag("strong")
val sub = PairedTag("sub")
val time = PairedTag("time")
val u = PairedTag("u")
val wbr = UnPairedTag("wbr")


/*
 * Image and multimedia
 */
val img = UnPairedTag("img")
val area = UnPairedTag("area")
val audio = PairedTag("audio")
val map = PairedTag("map")
val track = UnPairedTag("track")
val video = PairedTag("video")

/*
 * Embedded content
 */
val embed = UnPairedTag("embed")
val `object` = PairedTag("object")
val param = UnPairedTag("param")
val source = UnPairedTag("source")


/*
 * Scripting
 */

val canvas = PairedTag("canvas")
val noscript = PairedTag("noscript")
val script = PairedTag("script")

/*
 * Demarcating edits
 */
val del = PairedTag("del")
val ins = PairedTag("ins")


/*
 * Table content
 */

val caption = PairedTag("caption")
val col = UnPairedTag("col")
val colgroup = PairedTag("colgroup")
val table = PairedTag("table")
val tbody = PairedTag("tbody")
val td = PairedTag("td")
val tfoot = PairedTag("tfoot")
val th = PairedTag("th")
val thead = PairedTag("thead")
val tr = PairedTag("tr")

/*
 * Forms
 */

val button = PairedTag("button")
val datalist = PairedTag("datalist")
val fieldset = PairedTag("fieldset")
val form = PairedTag("form")
val input = UnPairedTag("input")
val label = PairedTag("label")
val legend = PairedTag("legend")
val meter = PairedTag("meter")
val optgroup = PairedTag("optgroup")
val progress = PairedTag("progress")
val select = PairedTag("select")
val option = PairedTag("option")
val textarea = PairedTag("textarea")

/*
 * Interactive elements
 */

val details = PairedTag("details")
val dialog = PairedTag("dialog")
val menu = PairedTag("menu")
val menuitem = PairedTag("menuitem")
val summary = PairedTag("summary")

/*
 * Web components
 */

val element = UnPairedTag("element")
val shadow = UnPairedTag("shadow")
val template = UnPairedTag("template")

/*
 * Misc
 */
val output = PairedTag("output")
val iframe = PairedTag("iframe")
val sup = PairedTag("sup")


/*
 * Obsolete and deprecated elements
 * Refer to MDN for reference
 */


/*
 * Html 5.1
 */
val picture = PairedTag("picture")

trait Tags2:
  val title = PairedTag("title")
  val style = PairedTag("style")

object Tags2 extends Tags2

/**
* https://developer.mozilla.org/en-US/docs/Web/Guide/Graphics
* https://developer.mozilla.org/en-US/docs/Web/SVG/Element
*/
//Structural elements
val svg = PairedTag("svg")
val g = PairedTag("g")
val symbol = PairedTag("symbol")
val defs = PairedTag("defs")

//Animation elements
val animate = PairedTag("animate")
val animateColor = PairedTag("animateColor")
val animateMotion = PairedTag("animateMotion")
val animateTransform = PairedTag("animateTransform")
val discard = PairedTag("discard")
val mpath = PairedTag("mpath")
val set = PairedTag("set")

//Basic shapes
val circle = PairedTag("circle")
val ellipse = PairedTag("ellipse")
val line = PairedTag("line")
val polygon = PairedTag("polygon")
val polyline = PairedTag("polyline")
val rect = PairedTag("rect")

//Filter primitive elements

//Font elements

//Gradient elements
val linearGradient = PairedTag("linearGradient")
val meshGradient = PairedTag("meshGradient")
val radialGradient = PairedTag("radialGradient")
val stop = PairedTag("stop")

//Graphics elements
//  val text = PairedTag("text")
val use = PairedTag("use")
val path = UnPairedTag("path")
