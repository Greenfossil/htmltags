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

object Location:
  type Filter = Location => Boolean
  val NoFilter: Filter = _ => true

sealed case class Location(tag: Tag, path: Path):

  import Location.*

  val id: Int = System.identityHashCode(tag)

  protected def create(tag: Tag, path: Hole): Location = Location(tag, path)

  override def equals(other: Any): Boolean = other.isInstanceOf[Location] && other.asInstanceOf[Location].id == id

  override def hashCode: Int = id

  override def toString: String = "%s at %s".format(tag.tagLabel, path)

  def isTop: Boolean = path == Root

  def isChild: Boolean = !isTop

  def isFirst: Boolean = path match
    case Root => true
    case Hole(Nil, _, _) => true
    case _ => false

  def isLast: Boolean = path match
    case Root => true
    case Hole(_, _, Nil) => true
    case _ => false

  lazy val previous: Option[Location] = path match
    case Hole(head :: tail, p, r) => Some(create(head, Hole(tail, p, tag :: r)))
    case _ => None

  def findPrevious(f: Filter): Option[Location] = find(previous, f)(_.previous)

  def preceding(f: Filter = NoFilter): List[Location] = unfold(previous, f)(_.previous)

  lazy val next: Option[Location] = path match
    case Hole(l, p, head :: tail) => Some(create(head, Hole(tag :: l, p, tail)))
    case _ => None

  def findNext(f: Filter): Option[Location] = find(next, f)(_.next)

  def following(f: Filter = NoFilter): List[Location] = unfold(next, f)(_.next)

  def first: Location = if (isFirst) this else previous.get.first

  def last: Location = if (isLast) this else next.get.last

  lazy val child: Option[Location] = tag.elems match
    case Nil => None
    case head :: tail => Some(create(head, Hole(Nil, this, tail)))

  lazy val children: List[Location] = findChildren()

  def findChildren(f: Filter = NoFilter): List[Location] = unfold(child, f)(_.next)

  lazy val lastChild: Option[Location] = tag.elems.reverse match 
    case Nil => None
    case head :: tail => Some(create(head, Hole(tail, this, Nil)))

  lazy val parent: Option[Location] = path match 
    case p: Hole =>
      val xs = p.left.reverse_::: (tag :: p.right)
      Some(Location(p.parent.tag, p.parent.path))
    case _ => None

  lazy val ancestors: List[Location] = findAncestors()

  def findAncestors(f: Filter = NoFilter): List[Location] = unfold(parent, f)(_.parent)

  def findAncestorsOrSelf(f: Filter = NoFilter): List[Location] = this :: findAncestors(f)

  lazy val descendants: List[Location] = findDescendants()

  def findDescendants(f: Filter = NoFilter): List[Location] = findChildren(f).flatMap(_.descendantsOrSelf(f))

  def descendantsOrSelf(f: Filter = NoFilter): List[Location] = this :: findChildren(f).flatMap(_.descendantsOrSelf(f))

  def unfold(seed: Option[Location], filter: Filter)(f: Location => Option[Location]): List[Location] = {
    val b = List.newBuilder[Location]

    @annotation.tailrec
    def loop(loc: Location): Unit =
      f(loc) match 
        case None =>
        case Some(l) => if (filter(l)) b += l; loop(l)

    for (loc <- seed) 
      if (filter(loc)) b += loc
      loop(loc)

    b.result()
  }

  @annotation.tailrec
  final def find(start: Option[Location], filter: Filter)(f: Location => Option[Location]): Option[Location] =
    start match
      case None => None
      case s @ Some(loc) => if (filter(loc)) s else find(f(loc), filter)(f)
  
end Location

final class ParentLocation(tag: Tag, path: Hole) extends Location(tag, path):
  override protected def create(tag: Tag, path: Hole): Location = new ParentLocation(tag, path)
//  def parent: Option[Location] = Some(path.parent)

final class TopLocation(tag: Tag) extends Location(tag, Root):
  override protected def create(tag: Tag, path: Hole): Location = new ParentLocation(tag, path)
  override lazy val parent: Option[Location] = None
