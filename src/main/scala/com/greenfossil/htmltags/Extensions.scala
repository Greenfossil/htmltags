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

import org.slf4j.LoggerFactory

import scala.language.implicitConversions
import scala.util.{Failure, Success}

/*
 * Type Aliases
 */

trait AttributeValueLike:
  def render: String

type AttributeValueScalarType = String | Int | Long | Double | Float | BigDecimal | Boolean | CSSUnit | RawText | AttributeValueLike

type AttributeValueType =  AttributeValueScalarType | Option[AttributeValueScalarType] | Seq[AttributeValueScalarType]

type CCSUnits =  "px" | "pt" | "pct" | "mm" | "cm" | "in" | "pc" | "em" | "ch" | "ex" | "rem" | "def" | "grad" | "rad" | "turn"

case class CSSUnit(value: Int| Long | Double | Float | BigDecimal, unit: CCSUnits = "px"):
  def px = CSSUnit(value, "px")
  def pt = CSSUnit(value, "pt")
  def pct = CSSUnit(value, "pct")
  def mm = CSSUnit(value, "mm")
  def cm = CSSUnit(value, "cm")
  def in = CSSUnit(value, "in")
  def pc = CSSUnit(value, "pc")
  def em = CSSUnit(value, "em")
  def ch = CSSUnit(value, "ch")
  def ex = CSSUnit(value, "ex")
  def rem = CSSUnit(value, "rem")
  def deg = CSSUnit(value, "def")
  def grad = CSSUnit(value, "grad")
  def rad = CSSUnit(value, "rad")
  def turn = CSSUnit(value, "turn")

  override def toString: String =
    unit match
      case "px" | "pt" =>
        value match
          case i: (Int | Long) => i.toString + unit
          case d: Double => d.toInt.toString + unit
          case f: Float => f.toInt.toString + unit
          case bd: BigDecimal => bd.toInt.toString + unit

      case "pct" => s"$value%"
      case _ => value.toString + unit
  
end CSSUnit


/*
 * Constants
 */
val POSTFIX = "postfix"
val PREFIX = "prefix"

val EmptyTag: Tag = Tag.empty

/*
 * Utility functions
 */

def tag(tagLabel: String, nodes: Node*): Tag =
  if tagLabel == "" then Tag(nodes) else PairedTag(tagLabel, nodes)

def embeddedStyle(value: String): Tag = Tags2.style(RawText(value))

def ifTrue(predicate: => Boolean, tag: => Tag): Tag = if predicate then tag else Tag()
def ifTrue(predicate: => Boolean, attr: => Attribute): Attribute = if predicate then attr else Attribute()
def ifTrue(predicate: => Boolean, str: => String): String = if predicate then str else ""
def ifTrue(predicate: => Boolean, node: => Node): Node = if predicate then node else Node()

def ifFalse(predicate: => Boolean, tag: => Tag): Tag = if predicate then Tag() else tag
def ifFalse(predicate: => Boolean, attr: => Attribute): Attribute = if predicate then Attribute() else attr
def ifFalse(predicate: => Boolean, str: => String): String = if predicate then "" else str
def ifFalse(predicate: => Boolean, node: => Node): Node = if predicate then Node() else node

//Convenience method for creating a default Attribute
def attr(name: String, renderValueAsEscape:Boolean = true) = StaticAttribute(name, "", renderValueAsEscape)

def cssattr(name: String) = CSSAttribute(name, "")

def raw(text: String): RawText = RawText(text)

/*
 * Extensions
 */
extension (sc: StringContext)
  def htmltags(args: Matchable*): String =
    val partIterator = sc.parts.iterator
    val argIterator: Iterator[Matchable] = args.iterator
    var buf = new StringBuffer(partIterator.next())
    while(partIterator.hasNext)
      buf.append(anyToNode(argIterator.next()).render)
      buf.append(partIterator.next)
    buf.toString

extension (s: String) def :=(value: AttributeValueType): Attribute = StaticAttribute(s, "") := value

extension (ts: Seq[Node]) def render: String = Frag(ts).render

/*
 * Conversions
 */

given [U <:  Int| Long | Double | Float | BigDecimal]: Conversion [U, CSSUnit] = CSSUnit(_)


def anyToNode(any: Any): Node =
  any.asInstanceOf[Matchable] match
    case Success(x) => anyToNode(x)
    case Failure(ex) =>
      LoggerFactory.getLogger("htmltags").error("Exception caught", ex)
      s"Throwable caught: ${ex.getClass.getCanonicalName} - message:${ex.getMessage}"
    case opt: Option[?] => anyToNode(opt.toSeq)
    case xs: Seq[?] => Frag(xs.map(x => anyToNode(x)))
    case (null | Nil) => Node()
    case n: Node => n
    case zId: java.time.ZoneId => StringText(zId.getId)
    case x: (String | Int | Long | Double | Float | BigDecimal | Boolean) => StringText(x)
    case _ => StringText(any.toString)
