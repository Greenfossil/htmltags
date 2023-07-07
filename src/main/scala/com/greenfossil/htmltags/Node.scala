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

import com.greenfossil.htmltags.css.Selectors
import com.greenfossil.htmltags.css.parser.SelectorGroup

private def anyToString(any: AttributeValueScalarType): String =
  any match
    case s: String => s
    case x:(Int | Long | Double | Float | BigDecimal | Boolean | CSSUnit) => x.toString
    case rt : RawText => rt.render
    case at: AttributeValueLike => at.render
    case null => ""

/**
  * https://en.wikipedia.org/wiki/HTML_element
  * http://www.456bereastreet.com/archive/200508/html_tags_vs_elements_vs_attributes/
  * Node: Attribute, Text, Tag, Frag
  *
  */
sealed trait Node:
  def render: String

case object Node extends Node:
  def apply(): Node = this

  type NodeCompatible = Any // Try[?] | Option[?] | IterableOnce[?] | Null | String | Int | Long | Double | Float | BigDecimal
  given Conversion[NodeCompatible, Node] = anyToNode(_)

  override def render: String = ""
end Node

/*
 * Attribute type - Static Attribute, Dynamic Attribute and CSS Attribute
 */
trait Attribute extends Node:

  val name: String

  val value: AttributeValueType

  val renderValueAsEscape:Boolean = true

  private def toAttrValueSeq(attr: AttributeValueType): Seq[AttributeValueScalarType] =
    attr match
      case v: AttributeValueScalarType => Seq(v)
      case opt: Option[?] => opt.collect{ case v: AttributeValueScalarType => v}.toSeq
      case seq: Seq[?] => seq.collect{ case v: AttributeValueScalarType => v}

  private def toEscapedValue(value: AttributeValueType, escapeChar: Boolean): String =
    def escapeAttrValue(v: AttributeValueScalarType): String = {
      v match
        case rt: RawText => rt.render
        case _ =>
          val x = anyToString(v)
          if escapeChar then
            x.map {
              case '"' => "&quot;"
              case '\'' => "&apos;"
              case c => c.toString
            } mkString ""
          else x
    }
    toAttrValueSeq(value).map(escapeAttrValue).mkString(" ").trim

  def rawValue: String = toEscapedValue(value, false)

  lazy val escapedValue: String = toEscapedValue(value, true)

  def :=(newValue: AttributeValueType): Attribute

  def appendValue(newValue: AttributeValueType): Attribute = appendValue(POSTFIX, newValue)

  def concat(position: String, xs: AttributeValueType, ys: AttributeValueType): AttributeValueType =
    val lhs: Seq[AttributeValueScalarType] = toAttrValueSeq(xs)
    val rhs: Seq[AttributeValueScalarType] = toAttrValueSeq(ys)
    position match
      case PREFIX => rhs ++ lhs
      case _ => lhs ++ rhs //default is postfix

  /**
    *
    * @param position - prefix or postfix affects how the new value is appended to the existing value
    * @param newValue - new attribute value
    * @return
    */
  def appendValue(position: String, newValue: AttributeValueType): Attribute =
    replaceValue(concat(position, value, newValue))

  def replaceValue(newValue: AttributeValueType): Attribute

  def findAndReplaceValue(existingValue: String, newValue: String): Attribute

  /*
   * Remove tokens from the value
   */
  def removeTokens(tokensString: String): Attribute =
    if tokensString.trim.isEmpty then this
    else
      val deleteTokens = tokensString.split("\\s+")
      val newTokens = rawValue.split("\\s+").foldLeft(Seq[String]()) { (tokens, token) =>
        if deleteTokens.contains(token) then tokens else tokens :+ token
      }
      replaceValue(newTokens.mkString(" "))

  def isNull: Boolean = value == null

  def notNull: Boolean = value != null

  def render: String =
    val v = Option(value) match
      case None => ""
      case Some(_) => if(renderValueAsEscape) escapedValue else value.toString
    name +  s"""="$v""""

end Attribute

case object Attribute extends Attribute:

  val empty: Attribute = this

  def apply(): Attribute = empty

  override val name: String = ""

  override val value: AttributeValueType = ""

  override def :=(newValue: AttributeValueType): Attribute = this

  override def replaceValue(newValue: AttributeValueType): Attribute = this

  override def findAndReplaceValue(existingValue: String, newValue: String): Attribute = this

  override def render: String = ""

case class NoValueStaticAttribute(name: String) extends Attribute:

  override val value = ""

  override def :=(newValue: AttributeValueType): Attribute = this

  override def replaceValue(newValue: AttributeValueType): Attribute = this

  override def findAndReplaceValue(existingValue: String, newValue: String): Attribute = this

  override def render: String =  name


case class StaticAttribute(name: String, value: AttributeValueType, override val renderValueAsEscape:Boolean = true) extends Attribute:
  def apply(ns: String): Attribute = copy(ns+ ":" + name)

  override def :=(newValue: AttributeValueType): Attribute = copy(value = newValue)

  override def replaceValue(newValue: AttributeValueType): Attribute = copy(value = newValue)

  override def findAndReplaceValue(existingValue: String, newValue: String): Attribute =
    copy(value = this.rawValue.replaceAll(existingValue, newValue))

case class CSSAttribute(name: String, value: AttributeValueType) extends Attribute:

  def :=(newValue: AttributeValueType): Attribute = copy(value = newValue)

  override def replaceValue(newValue: AttributeValueType): Attribute = copy(value = newValue)

  override def findAndReplaceValue(existingValue: String, newValue: String): Attribute = copy(value = rawValue.replaceAll(existingValue, newValue))

  override def render: String =
    val x = name match
      case "style" => escapedValue
      case _ => name + ":" + Option(value).map(_ => " " + escapedValue).getOrElse("")
    x + ";"

/*
 * Dynamic attributes
 */
import com.greenfossil.htmltags.css.Location

import scala.language.dynamics

case class DynAttribute(name: String, value: AttributeValueType, sep: String = "-", override val renderValueAsEscape: Boolean = true) extends Dynamic with Attribute:
  def selectDynamic(extName: String): DynAttribute = copy(name = name + sep + extName)

  def :=(newValue: AttributeValueType): Attribute = copy(value = newValue)

  override def findAndReplaceValue(existingValue: String, newValue: String): Attribute =
    this.copy(value = this.rawValue.replaceAll(existingValue, newValue))

  override def replaceValue(newValue: AttributeValueType): Attribute = copy(value = newValue)

object aria extends Dynamic:
  def selectDynamic(attrName: String) = DynAttribute("aria-" + attrName, "")

object data extends Dynamic:
  def selectDynamic(attrName: String) = DynAttribute("data-" + attrName, "")

/*
 * Namespace Attr
 */
object xmlns extends Dynamic:
  def apply(ns: String): Attribute = StaticAttribute("xmlns:"+ns, "")

  def :=(url: String): Attribute = StaticAttribute("xmlns", url)

  def selectDynamic(ns: String) = DynAttribute("xmlns:" + ns, "")


trait Text extends Node

case class StringText(value: AttributeValueScalarType) extends Text:
  def render: String = escapedValue

  lazy val valueString: String = anyToString(value)

  //escapes characters to html entities
  lazy val escapedValue: String = valueString.map(chr => escapeMap.getOrElse(chr, chr)).mkString("")

  private lazy val charEntityRegex = "&#([xX])([A-F0-9]+)?;".r

  //reverse an escaped value. Eg: '&gt;' will become '>'
  lazy val nonEscapedValue: String = {
    //Replace all character entities to characters
    val hexedEscapedValue = charEntityRegex.findAllIn(valueString).foldLeft(valueString){(res, charEntity) =>
      charEntity.toLowerCase match
        case s"&#x$i;" => res.replaceAll(charEntity, Integer.parseInt(i, 16).toChar.toString)
        case _ => charEntity
    }

    //finds and converts escapedValues to characters
    escapeMap.toList.foldLeft(hexedEscapedValue) { (res, kv) =>
      res.replaceAll(kv._2, kv._1.toString)
    }
  }

  //list of escape characters map
  lazy val escapeMap: Map[Char, String] = Map(
    '"'  -> "&quot;",
    '\'' -> "&apos;",
    '<'  -> "&lt;",
    '>'  -> "&gt;",
    '&'  -> "&amp;",
    '/'  -> "&#x2F;"
  )

end StringText

case class RawText(value: String) extends Text:
  def render: String = value

object Frag:
  val empty: Frag = new Frag(Nil)
  def apply(): Frag = empty
  def apply(n: Node): Frag = new Frag(Seq(n))
end Frag

case class Frag(nodes: Seq[Node]) extends Node with TagCSSQuerySupport[Frag]:

  def tags: Seq[Tag] = nodes.collect{case t: Tag => t}

  def nonTags: Seq[Node] = nodes diff tags

  def map(fn: Node => Node): Frag = Frag(nodes.map(fn))

  def addNodes(newNodes: Seq[Node]): Frag = Frag(nodes ++ newNodes)

  def cssQuery(selectorString: String): Seq[Tag] =  tags.flatMap(root =>  Selectors.query(selectorString, root))

  def cssQuery(selectorGroups: List[SelectorGroup]): Seq[Tag] = tags.flatMap(root => Selectors.query(selectorGroups, root))

  override def addNodes(cssSelector: String, newNodes: Node*): Frag =
    val newTags = tags.map(_.addNodes(cssSelector, newNodes*))
    Frag(newTags ++ nonTags)

  override def deleteTags(cssSelector: String, deleteAttrs: Attribute*): Frag =
    val newTags = tags.map(_.deleteTags(cssSelector, deleteAttrs*))
    Frag(newTags ++ nonTags)

  override def replaceNodes(cssSelector: String, newNodes: Node*): Frag =
    val newTags = tags.map(_.replaceNodes(cssSelector, newNodes*))
    Frag(newTags ++ nonTags)

  override def extractTags(cssSelector: String): (Seq[Tag], Seq[Tag]) = 
    val tags = nodes.collect{case t:Tag => t}
    tags.foldLeft((Seq.empty[Tag], Seq.empty[Tag])){ case (accu, tag) =>
      val (accuTag, accuExtractedTags) = accu
      val (remainingTag, extractedTags) = tag.extractTags(cssSelector)
      (accuTag :+ remainingTag, accuExtractedTags ++ extractedTags)
    }

  def render: String = nodes.map(_.render).mkString

end Frag

/**
 * Tag - PairedTag, UnpairedTag, Tag()
 */
trait Tag extends Node with TagCSSQuerySupport[Tag]:

  /*
   * create new instance using nodes, only label is the same
   */
  def using(nodes: Seq[Node]): Tag

  val tagLabel: String

  def nodes: Seq[Node]

  def apply(newNodes: Node*): Tag = if newNodes.isEmpty then this else {
    val _newNodes =  newNodes.filterNot(n => n == Node() || n == Tag.empty)
    /*
     * Existing elements if in the newContent must be keep in the same order
     * If there is a new Text, it will be appended to the last Text
     * The rest of the Element are appended
     * If there is no Text in existing content, the new content will be appended as is
     */
    val cs = nodes.lastIndexWhere(c => c.isInstanceOf[Text]) match {
      case -1 => nodes ++ _newNodes
      case lastTextIndex =>
        val (newTexts, newChildNodes) = flattenNodes(_newNodes).partition(n => n.isInstanceOf[Text])
        val(as, bs) = nodes.splitAt(lastTextIndex + 1)
        as ++ newTexts ++ bs ++ newChildNodes
    }

    using(normalizeNodes(cs))
  }

  def cssQuery(selectorString: String): Seq[Tag] = Selectors.query(selectorString, this)

  def cssQuery(selectorGroups: List[SelectorGroup]): Seq[Tag] = Selectors.query(selectorGroups, this)

  def replaceAttrs(newAttrs: Attribute*): Tag = using(normalizeNodes(newAttrs ++ content))

  def replaceElems(newElems: Tag*): Tag = using(normalizeNodes(attrs ++ text ++ newElems))

  def replaceTexts(newTexts: Text*): Tag = using(normalizeNodes(attrs ++ newTexts ++ elems))

  def replaceNodes(newNodes: Seq[Node]): Tag = using(normalizeNodes(newNodes = newNodes))

  def render: String

  /*
   * May have a Seq[Node] or a Seq[Attr]
   */
  def flattenNodes(nodes: Seq[Node]): Seq[Node] =
    if nodes == null then Seq.empty
    else nodes.foldLeft(Seq[Node]()) { (res, e) =>
        if e == null then res
        else e match
            case Frag(ns) => res ++ flattenNodes(ns)
            case n: Node => res :+ n
      }

  /*
   * NB: Frag nodes are flatten
   */
  def attrs: Seq[Attribute] = flattenNodes(nodes).collect{case attr: Attribute => attr}

  def content: Seq[Node] = flattenNodes(nodes) diff attrs

  lazy val (cssAttrs, htmlAttrs) = attrs.partition(_.isInstanceOf[CSSAttribute])

  def text: Seq[Text] = content.collect { case n: Text => n }

  /*
   * Empty tags are removed
   */
  def elems: Seq[Tag] = content.collect{case node: Tag => node}

  def htmlAttrValue: Option[String] = htmlAttrs.headOption.map(_ => htmlAttrs.map(_.render).mkString(if (htmlAttrs.isEmpty) "" else " ", " ", ""))

  def cssAttrValue: Option[String] = cssAttrs.headOption.map { _ =>
    /*
     * 'style' attr will be first and other html style attrs put last
     */
    val sortCSSAttrs = cssAttrs.filter(attr => attr.name == "style") ++ cssAttrs.filter(_.name != "style")

    sortCSSAttrs.map(_.render).mkString((if (htmlAttrValue.isEmpty) " " else "") + "style=\"", " ", "\"")
  }

  def attrsValue: String = htmlAttrValue ++ cssAttrValue mkString " "

  def attrsName: Seq[String] = attrs.map(_.name)

  def contentValue: String = content.map { n =>
    val nn = n match
      case StringText(v) if tagLabel == "script" && v != null => RawText(v.toString)
      case _ => n
    nn.render
  }.mkString

  def textValue: String = text.map(_.render).mkString

  def elemsValue: String = elems.map(_.render).mkString

  def attr(name: String): Option[Attribute] = attrs.find(attr => attr.name == name)

  def attrValue(name: String): Option[String] = attr(name).map(_.escapedValue)

  def attrCls: Option[Attribute] = attr("class")

  def attrClsValue: Option[String] = attrValue("class")

  /**
    *
    * @param position - prefix or postfix - it would only affect any existing Tag's attributes
    * @param newAttrs - new attributes used to compute the differences with tag's attributes
    * @return all attributes that append new value to existing attrs and new attributes
    */
  def computeUnionAttrs(position: String, newAttrs: Attribute*): Seq[Attribute] = {
    if newAttrs.isEmpty then attrs
    else {
      val newAttrsMap = newAttrs.map(a => a.name -> a).toMap
      val newAttrsName = newAttrs.map(_.name)
      val commonAttrNames = newAttrsName intersect attrsName
      val newAttrNames = newAttrsName diff attrsName
      val unionAttrs = attrs.map { attr =>
        attr.name match
          case attrName if commonAttrNames.contains(attr.name) =>
            /*
             * If newAttr has the same value as the existing attr - skip
             */
            attr.appendValue(position, newAttrsMap(attrName).value)
          case _ => attr

      } ++ newAttrNames.map(newAttrsMap)
      unionAttrs
    }
  }

  /**
    * Normalize is to collapse all the common HTMLAttributes
    */
  def normalizeNodes(newNodes: Seq[Node]): Seq[Node] = 
    val (newAttrs, newContent) = flattenNodes(newNodes).partition(_.isInstanceOf[Attribute])
    val (newCssAttrs, newHtmlAttrs) = newAttrs.asInstanceOf[Seq[Attribute]].partition(_.isInstanceOf[CSSAttribute])
    val normalizedHtmlAttrs = newHtmlAttrs.foldLeft(Map.empty: Map[String, Attribute]) { (res, attr) =>
      res.get(attr.name) match
        case None => res ++ Map(attr.name -> attr)
        case Some(existingAttr) => res ++ Map(attr.name -> existingAttr.appendValue(attr.value))

    }.values.toSeq
    normalizedHtmlAttrs ++ newCssAttrs ++ newContent

  def appendAttrs(newAttrs: Attribute*): Tag = appendAttrs(POSTFIX, newAttrs*)

  /**
    *
    * @param position    - prefix or postfix, it would only affect any existing Tag's attributes
    * @param appendAttrs - attributes that will be appended to Tag
    * @return
    */
  def appendAttrs(position: String, appendAttrs: Attribute*): Tag =
    if appendAttrs.isEmpty then this
    else replaceAttrs(computeUnionAttrs(position, appendAttrs*)*)

  def removeAttrsTokens(tokenAttrs: Attribute*): Tag = 
    if tokenAttrs.isEmpty then this
    else {
      val tokenAttrsByName = tokenAttrs.map(a => a.name -> a).toMap
      val newAttrs = attrs.foldLeft(Seq.empty[Attribute]){(res,attr) =>
        tokenAttrsByName.get(attr.name) match
          case None => res :+ attr
          case Some(tokenAttr) =>
            res :+ attr.removeTokens(tokenAttr.rawValue)
      }
      replaceAttrs(newAttrs *)
    }

  /*
   * Selectors APIs
   */
  import com.greenfossil.htmltags.css.Selectors

  override def addNodes(selectorString: String, newNodes: Node*): Tag = Selectors.addNodes(selectorString, newNodes*)(this)

  override def deleteTags(selectorString: String, deleteAttrs: Attribute*): Tag = Selectors.deleteTags(selectorString, deleteAttrs*)(this)

  override def replaceNodes(selectorString: String, newNodes: Node*): Tag = Selectors.replaceNodes(selectorString, newNodes*)(this)

  override def extractTags(selectorString: String): (Tag, Seq[Tag]) = Selectors.extractTags(selectorString)(this)

  def modifyNodes(cssSelector: String, modFn: (Tag, Location) => Option[Tag]): Node = Selectors.modifyNodes(cssSelector, modFn)(this)

  //add new nodes as the first child of a given cssSelector
  def addAsFirstChild(selectorString: String, newNodes: Node*) = Selectors.addAsFirstChild(selectorString, newNodes*)(this)

  //modify value of a specified attribute based on user defined conditions
  def modifyAttribute(cssSelector: String, modAttr: Attribute, modFn: (Attribute) => Option[Attribute]): Node = Selectors.modifyAttribute(cssSelector, modAttr, modFn)(this)

  //delete value from a specified attribute
  def deleteFromAttribute(selectorString: String, attribute: Attribute) = Selectors.removeAttrTokens(selectorString, attribute)(this)

  def :+(tag:Tag): Seq[Tag] = Seq(this, tag)

end Tag

object Tag extends Tag:

  val empty: Tag = this

  def apply():Tag = empty

  val tagLabel = ""
  
  val nodes = Nil

  override def using(nodes: Seq[Node]): Tag = this

  def render: String = ""

end Tag

case class PairedTag(tagLabel: String, nodes: Seq[Node] = Nil) extends Tag :
  override def using(nodes: Seq[Node]): Tag = copy(nodes = normalizeNodes(nodes))
  def render: String = "<" + tagLabel + attrsValue + ">" + contentValue + "</" + tagLabel + ">"

case class UnPairedTag(tagLabel: String, nodes: Seq[Node] = Nil) extends Tag :
  override def using(nodes: Seq[Node]): Tag = copy(nodes = normalizeNodes(nodes))
  def render: String =
    if contentValue.isEmpty then "<" + tagLabel + attrsValue + "/>"
    else "<" + tagLabel + attrsValue + ">" + contentValue + "</" + tagLabel + ">"

/*
 * StartTag cannot have content
 */
case class StartTag(tagLabel: String, nodes: Seq[Node] = Nil) extends Tag :
  override def using(nodes: Seq[Node]): Tag = copy(nodes = normalizeNodes(nodes))
  def render: String = "<" + tagLabel + attrsValue + ">"
