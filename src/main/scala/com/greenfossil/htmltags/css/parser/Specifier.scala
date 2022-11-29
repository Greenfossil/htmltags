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

package com.greenfossil.htmltags.css.parser

/**
  * Adapted from https://github.com/chrsan/css-selectors-scala
  * Created by chungonn on 10/7/17.
  */

sealed abstract class Combinator(val repr: String):
  override def toString: String = repr

object Combinator:
  case object Descendant extends Combinator("")
  case object Child extends Combinator(">")
  case object AdjacentSibling extends Combinator("+")
  case object GeneralSibling extends Combinator("~")

sealed trait Specifier

object Specifier :

  object Attribute:
    type Value = String

    sealed abstract class Match(val repr: String) :
      override def toString: String = repr
    end Match

    case object Exact extends Match("=")
    case object List extends Match("~=")
    case object Hyphen extends Match("|=")
    case object Prefix extends Match("^=")
    case object Suffix extends Match("$=")
    case object Contains extends Match("*=")

  case class Attribute(name: String, matches: Option[(Attribute.Match, Attribute.Value)]) extends Specifier:
    override def toString: String = matches match {
      case Some(m) =>
        (name, m._1) match {
          case ("class", Attribute.List) => "." + m._2
          case ("id", Attribute.Exact) => "#" + m._2
          case _ => "[%s%s%s]".format(name, m._1, m._2)
        }
      case _ => "[%s]".format(name)
    }

  case class Negation(selector: Selector) extends Specifier:
    override def toString: String = ":not(%s)".format(selector)

  sealed trait PseudoSpecifier extends Specifier

  object PseudoClass:
    sealed abstract class Value(val repr: String):
      override def toString: String = repr
    end Value

    case object Empty extends Value("empty")
    case object Root extends Value("root")
    case object FirstChild extends Value("first-child")
    case object LastChild extends Value("last-child")
    case object OnlyChild extends Value("only-child")
    case object FirstOfType extends Value("first-of-type")
    case object LastOfType extends Value("last-of-type")
    case object OnlyOfType extends Value("only-of-type")

  case class PseudoClass(value: PseudoClass.Value) extends PseudoSpecifier:
    override def toString: String = ":" + value

  case class PseudoElement(value: String) extends PseudoSpecifier:
    override def toString: String = "::" + value

  object PseudoNth:
    sealed abstract class Value(val repr: String):
      override def toString: String = repr
    end Value

    case object NthChild extends Value("nth-child")
    case object NthLastChild extends Value("nth-last-child")
    case object NthOfType extends Value("nth-of-type")
    case object NthLastOfType extends Value("nth-last-of-type")

  case class PseudoNth(value: PseudoNth.Value, argument: String) extends PseudoSpecifier:
    val (a: Int, b: Int) =
      argument.replaceAll("\\+|\\s+", "") match
        case "odd" => 2 -> 1
        case "even" => 2 -> 0
        case str => str.indexOf("n") match
          case -1 => 0 -> str.toInt
          case n =>
            val a = if n == 0 then 1 else if n == 1 && str(0) == '-' then -1 else str.substring(0, n).toInt
            val b = if (n + 1) != str.length then str.substring(n + 1, str.length).toInt else 0
            a -> b

    def isMatch(nodeCount: Int): Boolean =
      if a == 0 then nodeCount == b
      else if a > 0 then if nodeCount < b then false else (nodeCount - b) % a == 0
      else if nodeCount > b then false else (b - nodeCount) % (-a) == 0

    override def toString: String = ":%s(%s)".format(value, argument)

  end PseudoNth


end Specifier
