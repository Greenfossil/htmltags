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

object Selector:
  val UniversalTag: String = "*"

  def apply(tagName: String, specifiers: List[Specifier]): Selector = Selector(tagName, Combinator.Descendant, specifiers)

  def apply(specifiers: List[Specifier]): Selector = Selector(UniversalTag, Combinator.Descendant, specifiers)

case class Selector(tagLabel: String, combinator: Combinator, specifiers: List[Specifier]):
  override def toString: String = (tagLabel, combinator, specifiers) match {
    case (Selector.UniversalTag, Combinator.Descendant, Nil) => tagLabel
    case (Selector.UniversalTag, c, Nil) => "%s %s".format(c, tagLabel)
    case (Selector.UniversalTag, Combinator.Descendant, s) => s.mkString
    case (Selector.UniversalTag, c, s) => "%s %s".format(c, s.mkString)
    case (t, Combinator.Descendant, s) => t + s.mkString
    case _ => "%s %s%s".format(combinator, tagLabel, specifiers.mkString)
  }

case class SelectorGroup(selectors: List[Selector]) :
  override def toString: String = selectors.mkString(" ")

