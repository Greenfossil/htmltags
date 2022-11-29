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

package com.greenfossil.htmltags.css

import com.greenfossil.htmltags.Tag

/**
  * Adapted from https://github.com/chrsan/css-selectors-scala
  * Created by chungonn on 10/7/17.
  */

sealed trait Path:
  def left: List[Tag]
  def parentLocation: Option[Location]
  def right: List[Tag]

case object Root extends Path:
  override def toString: String = "ROOT"
  override def left: List[Tag] = Nil
  override def parentLocation: Option[Location] = None
  override def right: List[Tag] = Nil

case class Hole(left: List[Tag], parent: Location, right: List[Tag]) extends Path:
  override def toString: String = "%s/%d".format(parent.path, left.size)
  override def parentLocation: Option[Location] = Some(parent)
