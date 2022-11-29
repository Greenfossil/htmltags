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

/**
  * Adapted from https://github.com/chrsan/css-selectors-scala
  * https://www.st.cs.uni-saarland.de/edu/seminare/2005/advanced-fp/docs/huet-zipper.pdf
  * Created by chungonn on 10/7/17.
  */

import com.greenfossil.htmltags.css.parser.*
import com.greenfossil.htmltags.css.parser.Combinator.*
import com.greenfossil.htmltags.css.parser.Selector.*
import com.greenfossil.htmltags.css.parser.Specifier.*
import com.greenfossil.htmltags.{Node, StringText, Tag}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

object Selectors:

  val logger = LoggerFactory.getLogger(this.getClass.getPackage.getName)

  def query[T <: Tag](selectorString: String, root: T): List[Tag] = new Selectors(root).query(selectorString)

  def query[T <: Tag](selectorGroups: List[SelectorGroup], root: T): List[Tag] = new Selectors(root).query(selectorGroups)

  def addNodes[T <: Tag](selectorString: String, newNodes: Matchable*)(root: T): T =
    val _newNodes = newNodes.map(com.greenfossil.htmltags.anyToNode(_))
    new Selectors(root).addNodes(selectorString, _newNodes*)

  def deleteTags[T <: Tag](selectorString: String, deleteAttrs: com.greenfossil.htmltags.Attribute*)(root: T): T =
    new Selectors(root).deleteTags(selectorString, deleteAttrs*)

  def replaceNodes[T <: Tag](selectorString: String, newNodes: Node*)(root: T): T =
    new Selectors(root).replaceNodes(selectorString, newNodes*)

  def modifyNodes[T <: Tag](selectorString: String, modFn: (Tag, Location) => Option[Tag])(root: T): T =
    new Selectors(root).modifyNodes(selectorString, modFn)

  def extractTags[T <: Tag](selectorString: String)(root: T): (T, Seq[T]) =
    new Selectors(root).extractTags(selectorString)

  def addAsFirstChild[T <: Tag](selectorString: String, newNodes: Node*)(root: T): T =
    new Selectors(root).addAsFirstChild(selectorString, newNodes* )

  def modifyAttribute[T <: Tag](selectorString: String,
                                modAttr: com.greenfossil.htmltags.Attribute,
                                modFn: (com.greenfossil.htmltags.Attribute) => Option[com.greenfossil.htmltags.Attribute]
                               )(root: T): T =
    new Selectors(root).modifyAttribute(selectorString, modAttr, modFn)

  def removeAttrTokens[T <: Tag](selectorString: String,
                                 removeAttrVal: com.greenfossil.htmltags.Attribute
                                )(root: T): T =
    new Selectors(root).removeAttrTokens(selectorString, removeAttrVal)

end Selectors

class Selectors[T <: Tag](val root: T):

  val rootLoc: Location = TopLocation(root)

  def query(selectorString: String): List[Tag] = 
    Selectors.logger.debug(s"SelectorString (${selectorString})...")
    query(SelectorParser.parse(selectorString))

  def query(selectorGroups: List[SelectorGroup]): List[Tag] = 
    val tags = locationsOf(selectorGroups).map(loc => loc.tag).distinct
    Selectors.logger.debug(s"SelectorGroup (${selectorGroups}) found ${tags.size} locs")
    tags.zipWithIndex foreach ((tag, index) => Selectors.logger.debug(s"loc #${index} {${tag.tagLabel}}"))
    tags
  

  def locationsOf(selectorString: String): List[Location] = 
    val locations = locationsOf(SelectorParser.parse(selectorString))
    Selectors.logger.debug(s"SelectorString [${selectorString}] found ${locations.size} locs")
    locations.zipWithIndex foreach ((loc, index) => Selectors.logger.debug(s"loc #${index} {${loc.tag.tagLabel}}"))
    locations
  

  def locationsOf(selectorGroups: List[SelectorGroup]): List[Location] =
    // TODO: Make this tail recursive.
    def loop(selectors: List[Selector]): List[Location] = selectors.foldLeft(rootLoc :: Nil) { (locations, selector) =>
      val locationsByTagName = queryByTagNameAndCombinator(selector, locations)
      //filter locationsByTagName if selector as specifiers
      selector.specifiers.foldLeft(locationsByTagName) { (locations, specifier) =>
        specifier match 
          case a: Attribute => queryByAttribute(a, locations)
          case p: PseudoClass => queryByPseudoClass(p.value, locations)
          case p: PseudoElement => locations
          case p: PseudoNth => queryByPseudoNth(p, locations)
          case n: Negation => locations diff loop(n.selector :: Nil)
        
      }
    }
    selectorGroups.foldLeft(Nil: List[Location]) { (res, group) => res ++ loop(group.selectors) }

  /*
   * Mutation API
   */

  /**
    *
    * @param selectorString
    * @param modifyFn
    * @return modified Tag
    */
  def modifyNodes(selectorString: String, modifyFn: (Tag, Location) => Option[Tag]): T = {
    def mutateChildrenTag(parentTag: Tag, mutableLocs: List[Location]): Seq[Tag] = {
      parentTag.elems.flatMap { elem =>
        mutableLocs.find(loc => loc.tag == elem) match
          case Some(loc) => modifyFn(elem, loc)
          case None =>
            /*
             * If elem does not have mutable descendants return elem
             * else descend and to find the mutable descendants
             */
            val elemHasMutableDescendants = mutableLocs.exists { loc => loc.ancestors.map(_.tag).contains(elem) }
            if (!elemHasMutableDescendants) then Some(elem)
            else 
                /*
                 * if elem is not in mutableLocs, descends furthe else apply change
                 */
                mutableLocs.find(loc => loc.tag == elem) match
                  case Some(loc) =>
                    modifyFn(elem, loc)
                  case None =>
                    Some(elem.using(elem.attrs ++ elem.text ++ mutateChildrenTag(elem, mutableLocs)))
      }
    }

    val mutableLocs = locationsOf(selectorString)
    (mutateChildrenTag(root, mutableLocs) match 
      case Nil => root
      case newChildTags => root.using(root.attrs ++ root.text ++ newChildTags)
    ).asInstanceOf[T]
  }

  def addNodes(selectorString: String, newNodes: Node*): T = 
    modifyNodes(selectorString, (tag, loc) => Option(tag.apply(newNodes*)))

  def deleteTags(selectorString: String, deleteNodes: com.greenfossil.htmltags.Attribute*): T = {
    def deleteFn(tag: Tag, loc:Location):Option[Tag] = {
      deleteNodes match {
        case Nil => None
        case _ =>
          val deleteAttrsByName = deleteNodes.collect{case attr: com.greenfossil.htmltags.Attribute => attr}.map(a => a.name -> a).toMap
          val retainAttrs = tag.attrs.foldLeft(Seq.empty[com.greenfossil.htmltags.Attribute]){(attrs, attr)=>
            deleteAttrsByName.get(attr.name) match 
              case None => attrs :+ attr
              case Some(deleteAttr) => attrs :+ attr.removeTokens(deleteAttr.rawValue)
          }
          val newTag = tag.using(retainAttrs ++ tag.text ++ tag.elems)
          Some(newTag)
      }
    }
    modifyNodes(selectorString, deleteFn)
  }

  /**
    *
    * @param selectorString
    * @param newNodes - if Node is a single Tag that the entire node is replace, else the content is replaced
    * @return
    */
  def replaceNodes(selectorString: String, newNodes: Node*): T = {
    def replaceFn(tag: Tag, loc: Location): Option[Tag] =
      newNodes match
        case Seq(newTag: Tag) => Option(newTag)
        case _ => Option(tag.using(newNodes))
    modifyNodes(selectorString, replaceFn)
  }

  def extractTags(selectorString: String): (T, Seq[T]) = {
    var extractedList = ListBuffer.empty[T]
    def extractFn(tag: Tag, loc: Location): Option[Tag] = {
      extractedList += tag.asInstanceOf[T]
      None
    }
    val remainingTags = modifyNodes(selectorString, extractFn)
    (remainingTags, extractedList.toSeq)
  }

  def addAsFirstChild(selectorString: String, newNodes: Node*): T =
    def addAsFirstChildFn(tag: Tag, loc: Location): Option[Tag] = Option(tag.using(newNodes ++ tag.nodes))
    modifyNodes(selectorString, addAsFirstChildFn)

  def modifyAttribute(selectorString: String,
                      targetAttr: com.greenfossil.htmltags.Attribute,
                      _modifyAttrFn: (com.greenfossil.htmltags.Attribute) => Option[com.greenfossil.htmltags.Attribute]
                     ): T = {
    def modifyAttrInTagFn(tag: Tag, loc: Location): Option[Tag] = {
      val existingAttr = tag.attrs
      val newAttrs = existingAttr.foldLeft(Seq.empty[com.greenfossil.htmltags.Attribute]){(res,attr) =>
        existingAttr.find(_.name == targetAttr.name) match 
          case None => res :+ attr
          case Some(tokenAttr) => res ++ _modifyAttrFn(tokenAttr)
      }
      Option(tag.replaceAttrs(newAttrs *))
    }
    modifyNodes(selectorString, modifyAttrInTagFn)
  }

  def removeAttrTokens(selectorString: String, removeAttrVal: com.greenfossil.htmltags.Attribute): T =
    def removeAttrTokensFn(tag: Tag, loc: Location): Option[Tag] = Option(tag.removeAttrsTokens(removeAttrVal))
    modifyNodes(selectorString, removeAttrTokensFn)

  private def queryByTagNameAndCombinator(selector: Selector, locations: List[Location]): List[Location] =
    val filter = tagLabelFilter(selector)
    selector.combinator match 
      case Descendant =>
        locations match 
          case head :: Nil if head == rootLoc =>
            locations.flatMap(_.descendantsOrSelf(_.tag.isInstanceOf[Tag])).filter(filter)
          case _ =>
            locations.flatMap(_.findDescendants(_.tag.isInstanceOf[Tag])).filter(filter)

      case Child => locations.flatMap(_.findChildren(filter))
      case AdjacentSibling => locations.flatMap(_.findNext(filter).toList)
      case GeneralSibling =>
        val seen = collection.mutable.HashSet[Int]()
        locations.flatMap(_.following(l => filter(l) && seen.add(l.id)))

  private def queryByAttribute(attr: Attribute, locations: List[Location]): List[Location] = locations.filter { loc =>
    (loc.tag.attr(attr.name), attr.matches) match
      case (None, _) => false
      case (_, None) => true // Attribute is present.
      case (Some(attr2), Some((m, v))) =>
        val txt = attr2.escapedValue
        if txt.isEmpty then false 
        else m match 
          case Attribute.Exact => txt == v
          case Attribute.Hyphen => txt == v || txt.startsWith(v + '-')
          case Attribute.Prefix => txt.startsWith(v)
          case Attribute.Suffix => txt.endsWith(v)
          case Attribute.Contains => txt.contains(v)
          case Attribute.List => txt.split("\\s+").contains(v)
  }

  private def queryByPseudoClass(value: PseudoClass.Value, locations: List[Location]): List[Location] = value match 
    case PseudoClass.Root => locations.filter(_.isTop)
    case PseudoClass.Empty =>
      locations.filterNot(loc => loc.tag.nodes.exists {
        case v@StringText(txt) => v.valueString.nonEmpty
        case _ => true
      })
    case PseudoClass.FirstChild => locations.filter(prevElem(_).isEmpty)
    case PseudoClass.LastChild => locations.filter(nextElem(_).isEmpty)
    case PseudoClass.OnlyChild => locations.filter(l => prevElem(l).isEmpty && nextElem(l).isEmpty)
    case PseudoClass.FirstOfType => locations.filter(l => prevElemByName(l, l.tag.tagLabel).isEmpty)
    case PseudoClass.LastOfType => locations.filter(l => nextElemByName(l, l.tag.tagLabel).isEmpty)
    case PseudoClass.OnlyOfType => locations.filter(l => prevElemByName(l, l.tag.tagLabel).isEmpty
      && nextElemByName(l, l.tag.tagLabel).isEmpty)

  private def queryByPseudoNth(nth: PseudoNth, locations: List[Location]): List[Location] = nth.value match
    case PseudoNth.NthChild => locations.filter(l => nth.isMatch(l.preceding(isElem).length + 1))
    case PseudoNth.NthLastChild => locations.filter(l => nth.isMatch(l.following(isElem).length + 1))
    case PseudoNth.NthOfType => locations.filter(l => nth.isMatch(l.preceding(elemByName(_, l.tag.tagLabel)).length + 1))
    case PseudoNth.NthLastOfType => locations.filter(l => nth.isMatch(l.following(elemByName(_, l.tag.tagLabel)).length + 1))

  private def tagLabelFilter(selector: Selector): Location.Filter = loc => loc.tag match
    case tag: Tag => selector.tagLabel == UniversalTag || selector.tagLabel == tag.tagLabel
    case null => false

  private def isElem(loc: Location): Boolean = loc.tag.isInstanceOf[Tag]

  private def prevElem(loc: Location): Option[Location] = loc.findPrevious(isElem)

  private def nextElem(loc: Location): Option[Location] = loc.findNext(isElem)

  private def elemByName(loc: Location, name: String): Boolean = isElem(loc) && loc.tag.tagLabel == name

  private def prevElemByName(loc: Location, name: String): Option[Location] = loc.findPrevious(elemByName(_, name))

  private def nextElemByName(loc: Location, name: String): Option[Location] = loc.findNext(elemByName(_, name))
  
end Selectors
