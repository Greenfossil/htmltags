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
 * https://developer.mozilla.org/en-US/docs/Web/HTML
 * https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes
 */
val accesskey = attr("accesskey")
val cls = attr("class")
val contenteditable = attr("contenteditable")
val contextmenu = attr("contextmenu")
val dir = attr("dir")
val draggable = attr("draggable")
val dropzone = attr("dropzone")
val hidden = attr("hidden")
val id = attr("id")
val itemid = attr("itemid")
val itemprop = attr("itemprop")
val itemref = attr("itemref")
val itemscope = attr("itemscope")
val itemtype = attr("itemtype")
val lang = attr("lang")
val spellcheck = attr("spellcheck")
val style = cssattr("style")
val tabindex = attr("tabindex")
val title = attr("title")
val translate = attr("translate")


/*
 * https://www.w3.org/TR/html-markup/global-attributes.html
 * https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes
 * Core attributes
 */

val accept = attr("accept")
val acceptCharset = attr("accept-charset")
val action = attr("action")
val align = attr("align")
val alt = attr("alt")
val async = attr("async")
val autocomplete = attr("autocomplete")
val autofocus = attr("autofocus")
val autoplay = attr("autoplay")
val autosave = attr("autosave")
val buffered = attr("buffered")
val capture = attr("capture")
val challenge = attr("challenge")
val charset = attr("charset")
val checked = attr("checked")
val codebase = attr("codebase")
val cols = attr("cols")
val colspan = attr("colSpan")
val content = attr("content")
val coords = attr("coords")
val datetime = attr("datetime")
val dirname = attr("dirname")
val disabled = attr("disabled")
val enctype = attr("enctype")
val `for` = attr("for")
val formaction = attr("formaction")
val headers = attr("headers")
val high = attr("high")
val href = attr("href")
val hreflang = attr("hreflang")
val httpEquiv = attr("http-equiv")
val ismap = attr("ismap")
val list = attr("list")
val loop = attr("loop")
val low = attr("low")
val manifest = attr("manifest")
val max = attr("max")
val maxlength = attr("maxlength")
val media = attr("media")
val method = attr("method")
val min = attr("min")
val multiple = attr("multiple")
val muted = attr("muted")
val name = attr("name")
val novalidate = attr("novalidate")
val open = attr("open")
val optimum = attr("optimum")
val pattern = attr("pattern")
val ping = attr("ping")
val placeholder = attr("placeholder")
val poster = attr("poster")
val preload = attr("preload")
val readonly = attr("readonly")
val rel = attr("rel")
val required = attr("required")
val reversed = attr("reversed")
val rows = attr("rows")
val rowspan = attr("rowspan")
val sandbox = attr("sandbox")
val scope = attr("scope")
val seamless = attr("seamless")
val selected = attr("selected")
val shape = attr("shape")
val size = attr("size")
val src = attr("src")
val srcdoc = attr("srcdoc")
val srclang = attr("srclang")
val srcset = attr("srcset")
val start = attr("start")
val step = attr("step")
val target = attr("target")
val tpe = attr("type")
val usemap = attr("usemap")
val value = attr("value")
val wrap = attr("wrap")
val loading = attr("loading")


/*
 * misc
 */
val background = attr("background")
val subject = attr("subject")
val valign = attr("valign")
val role = attr("role")
val onclick = attr("onclick")
val onload = attr("onload")
val onsubmit = attr("onsubmit")
val sizes = attr("sizes")
val onerror = attr("onerror")
val onblur = attr("onblur")
val ondrag = attr("ondrag")
val ondrop = attr("ondrop")
val onfocus = attr("onfocus")


/*
 * Deprecated
 */
@deprecated("use CSS background", "")
val bgcolor = attr("bgcolor")


/*
 * https://developer.mozilla.org/en-US/docs/Web/CSS
 * https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Properties_Reference
 */

val backgound = cssattr("background")
val backgoundAttachment = cssattr("background-attachment")
val backgroundColor = cssattr("background-color")
val backgroundImage = cssattr("background-image")
val backgroundPosition = cssattr("background-position")
val backgroundRepeat = cssattr("background-repeat")
val border = cssattr("border")
val borderColor = cssattr("border-color")
val borderStyle = cssattr("border-style")
val borderWidth = cssattr("border-width")
val borderBottom = cssattr("border-bottom")
val borderBottomColor = cssattr("border-bottom-color")
val borderBottomStyle = cssattr("border-bottom-style")
val borderBottomWidth = cssattr("border-bottom-width")
val borderLeft = cssattr("border-left")
val borderLeftColor = cssattr("border-left-color")
val borderLeftStyle = cssattr("border-left-style")
val borderLeftWidth = cssattr("border-left-width")
val borderRight = cssattr("border-right")
val borderRightColor = cssattr("border-right-color")
val borderRightStyle = cssattr("border-right-style")
val borderRightWidth = cssattr("border-right-width")
val borderSpacing = cssattr("border-spacing")
val borderCollapse = cssattr("border-collapse")
val borderTop = cssattr("border-top")
val borderTopColor = cssattr("border-top-color")
val borderTopStyle = cssattr("border-top-style")
val borderTopWidth = cssattr("border-top-width")
val borderRadius = cssattr("border-radius")
val bottom = cssattr("bottom")
val clear = cssattr("clear")
val clip = cssattr("clip")
val color = cssattr("color")
val cursor = cssattr("cursor")
val display = cssattr("display")
val filter = cssattr("filter")
val font = cssattr("font")
val fontFamily = cssattr("font-family")
val fontSize = cssattr("font-size")
val fontStyle = cssattr("font-style")
val fontVariant = cssattr("font-variant")
val fontWeight = cssattr("font-weight")
val fonHeight = cssattr("font-height")
val left = cssattr("left")
val letterSpacing = cssattr("letter-spacing")
val lineHeight = cssattr("line-height")
val lineStyle = cssattr("line-style")
val lineStyleImage = cssattr("line-style-image")
val lineStylePosition = cssattr("line-style-position")
val lineStyleType = cssattr("line-style-type")
val listStyle = cssattr("list-style")
val listStyleImage = cssattr("list-style-image")
val listStylePosition = cssattr("list-style-position")
val listStyleType = cssattr("list-style-type")
val margin = cssattr("margin")
val marginBottom = cssattr("margin-bottom")
val marginLeft = cssattr("margin-left")
val marginRight = cssattr("margin-right")
val marginTop = cssattr("margin-top")

val overflow = cssattr("overflow")
val padding = cssattr("padding")
val paddingBottom = cssattr("padding-bottom")
val paddingLeft = cssattr("padding-left")
val paddingRight = cssattr("padding-right")
val paddingTop = cssattr("padding-top")
val pageBreakAfter = cssattr("page-break-after")
val pageBreakBefore = cssattr("page-break-before")
val position = cssattr("position")
val float = cssattr("float")
val right = cssattr("right")
val textAlign = cssattr("text-align")
val textDecoration = cssattr("text-decoration")
val textDecorationBlink = cssattr("text-decoration: blink")
val textDecorationLineThrough = cssattr("text-decoration: line-through")
val textDecorationNone = cssattr("text-decoration: none")
val textDecorationOverline = cssattr("text-decoration: overline")
val textDecorationUnderline = cssattr("text-decoration: underline")
val textIndent = cssattr("text-indent")
val textTransform = cssattr("text-transform")
val top = cssattr("top")
val verticalAlign = cssattr("vertical-align")
val visibility = cssattr("visibility")
val width = cssattr("width")
val zIndex = cssattr("z-index")
val height = cssattr("height")
val minWidth = cssattr("min-width")
val maxWidth = cssattr("max-width")
val minHeight = cssattr("min-height")
val maxHeight = cssattr("max-height")
val wordWrap = cssattr("word-wrap")
val whiteSpace = cssattr("white-space")


/*
 * Attributes in this trait name class with Tag
 */
trait Attrs2:
  val cite = attr("cite")
  val code = attr("code")
  val form = attr("form")
  val summary = attr("summary")
  val `default` = attr("default")

object Attrs2 extends Attrs2

/*
 * Attributes for SVG
 */
val viewBox = attr("viewBox")
val stroke = attr("stroke")
val fill = attr("fill")
val d = attr("d")
val x = attr("x")
val y = attr("y")
//val width = attr("width") - defined in CSSAttrs
//val height = attr("height") - defined in CSSAttrs
val xlink = attr("xmlns:xlink")
val version = attr("version")
val preserveAspectRatio = attr("preserveAspectRatio")
