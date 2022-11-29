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
import com.greenfossil.htmltags.css.parser.Specifier.*
import com.greenfossil.htmltags.css.parser.Specifier.PseudoClass.*
import com.greenfossil.htmltags.css.parser.Specifier.PseudoNth.*
import org.slf4j.LoggerFactory

import scala.util.parsing.combinator.*

object SelectorParser:
  def parse(input: String): List[SelectorGroup] = new SelectorParser().parse(input)


private[css] class SelectorParser extends RegexParsers with PackratParsers:
  override def skipWhitespace = false

  lazy val nonAscii = """[\x80-\xFF]""".r

  lazy val escapedNl = """\\(?:\n|\r\n|\r|\f)""".r

  lazy val stringChars = """[\t !#$%&\(-~]""".r

  lazy val nth = """[+\-]?\d*n(?:\s*[+\-]\s*\d+)?|[+\-]?\d+|odd|even""".r

  lazy val s = """\s+""".r

  def escape: Parser[String] = "\\" ~! ("""\p{XDigit}{1,6}\s?|[ \-~]""".r | nonAscii) ^^ { case a ~ b => a + b }

  def nmStart: Parser[String] = """[_a-zA-Z]""".r | nonAscii | escape

  def nmChar: Parser[String] = """[_a-zA-Z0-9\-]""".r | nonAscii | escape

  def string1: Parser[String] = "\"" ~> rep(stringChars | escapedNl | "'" | nonAscii | escape) <~ "\"" ^^ (_.mkString)

  def string2: Parser[String] = "'" ~> rep(stringChars | escapedNl | "\"" | nonAscii | escape) <~ "'" ^^ (_.mkString)

  def string: Parser[String] = (string1 | string2)

  def ident: Parser[String] = ("-".?) ~! nmStart ~! (nmChar.*) ^^ { case a ~ b ~ c => "%s%s%s".format(a.getOrElse(""), b, c.mkString) }

  def combinator: Parser[Combinator] =
    """\s*[+>~]\s*|\s+""".r ^^ {
      _.trim match
        case Combinator.Descendant.repr      => Combinator.Descendant
        case Combinator.Child.repr           => Combinator.Child
        case Combinator.AdjacentSibling.repr => Combinator.AdjacentSibling
        case Combinator.GeneralSibling.repr  => Combinator.GeneralSibling
    }

  def hash: Parser[Attribute] = "#" ~> (nmChar.*) ^^ { case x => Attribute("id", Some(Attribute.Exact -> x.mkString)) }

  def clazz: Parser[Attribute] = "." ~> ident ^^ { case x => Attribute("class", Some(Attribute.List -> x)) }

  def attribMatch: Parser[Option[(Attribute.Match, Attribute.Value)]] =
    opt("""\s*[~|^$*]?=\s*""".r ~! (string | ident)) ^^ {
      case None => None
      case Some(m ~ value) =>
        m.trim match
          case Attribute.Exact.repr    => Some(Attribute.Exact -> value)
          case Attribute.List.repr     => Some(Attribute.List -> value)
          case Attribute.Hyphen.repr   => Some(Attribute.Hyphen -> value)
          case Attribute.Prefix.repr   => Some(Attribute.Prefix -> value)
          case Attribute.Suffix.repr   => Some(Attribute.Suffix -> value)
          case Attribute.Contains.repr => Some(Attribute.Contains -> value)
          case _ => Some(Attribute.Contains -> value)
    }

  def attrib: Parser[Attribute] =
    "[" ~> rep(s) ~> ident ~! attribMatch <~ rep(s) <~ "]" ^^ { case name ~ m => Attribute(name, m) }

  def pseudoClassOrNth: Parser[PseudoSpecifier] =
    not("not") ~> ":" ~> ident ~ opt("(" ~> rep(s) ~> nth <~ rep(s) <~ ")") ^? ({
      case Empty.repr ~ None              => PseudoClass(Empty)
      case Root.repr ~ None               => PseudoClass(Root)
      case FirstChild.repr ~ None         => PseudoClass(FirstChild)
      case LastChild.repr ~ None          => PseudoClass(LastChild)
      case OnlyChild.repr ~ None          => PseudoClass(OnlyChild)
      case FirstOfType.repr ~ None        => PseudoClass(FirstOfType)
      case LastOfType.repr ~ None         => PseudoClass(LastOfType)
      case OnlyOfType.repr ~ None         => PseudoClass(OnlyOfType)
      case NthChild.repr ~ Some(arg)      => PseudoNth(NthChild, arg)
      case NthLastChild.repr ~ Some(arg)  => PseudoNth(NthLastChild, arg)
      case NthOfType.repr ~ Some(arg)     => PseudoNth(NthOfType, arg)
      case NthLastOfType.repr ~ Some(arg) => PseudoNth(NthLastOfType, arg)
    }, { case v ~ _ => v + " is not a valid pseudo class!" })

  def pseudoElement: Parser[PseudoElement] = "::" ~> ident ^^ { PseudoElement(_) }

  def pseudo: Parser[PseudoSpecifier] = pseudoElement | pseudoClassOrNth

  def negation: Parser[Negation] = ":not(" ~> rep(s) ~> negationSimpleSelector <~ rep(s) <~ ")" ^^ { case sel => Negation(sel) }

  def specifier: Parser[Specifier] = negation | hash | clazz | attrib | pseudo

  def tagSelector: Parser[Selector] = ("*" | ident) ~! rep(specifier) ^^ { case tag ~ specifiers => Selector(tag, specifiers) }

  def specifiersSelector: Parser[Selector] = rep1(specifier) ^^ { case specifiers => Selector(specifiers) }

  def simpleSelector: Parser[Selector] = (specifiersSelector | tagSelector) // <~ rep(s) // Added \s* in combinator.

  def selectorWithCombinator: Parser[Selector] = combinator ~! simpleSelector ^^ { case c ~ sel => sel.copy(combinator = c) }

  def selector: Parser[SelectorGroup] = simpleSelector ~! rep(selectorWithCombinator) ^^ { case sel ~ xs => SelectorGroup(sel :: xs) }

  def selectorGroup: Parser[List[SelectorGroup]] = rep("," ~> rep(s) ~> selector)

  def selectors: Parser[List[SelectorGroup]] = selector ~! selectorGroup ^^ { case group ~ xs =>  group :: xs }

  def negationSpecifier: Parser[Specifier] = hash | clazz | attrib | pseudo

  def negationTagSelector: Parser[Selector] = ("*" | ident) ~! rep(negationSpecifier) ^^ { case tag ~ specifiers => Selector(tag, specifiers) }

  def negationSpecifiersSelector: Parser[Selector] = rep1(negationSpecifier) ^^ { case specifiers => Selector(specifiers) }

  def negationSimpleSelector: Parser[Selector] = (negationSpecifiersSelector | negationTagSelector) <~ rep(s)

  def parse(input: String): List[SelectorGroup] =
    try parseAll(selectors, input).get
    catch
      case ex: Exception =>
        LoggerFactory.getLogger("htmltags.css.parser").error(s"Parser failure selector [${input}]", ex)
        Nil

end SelectorParser
