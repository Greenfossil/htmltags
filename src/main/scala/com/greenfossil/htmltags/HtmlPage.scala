package com.greenfossil.htmltags

import com.greenfossil.htmltags.css.Selectors

import scala.language.implicitConversions

object HtmlPage:
  private def findTag(label: String, nodes: Seq[Node]): Seq[Tag] =
    nodes.collect { case tag: Tag if tag.tagLabel == label => tag }

  def apply(title: String, charset: String = "utf-8")(nodes: Node*): HtmlPage =
    build(title, charset)(nodes)

  private def flattenNodes(ns: Seq[Node]): Seq[Node] =
    ns.foldLeft(Seq[Node]()) { (res, n) =>
      val _ns = n match
        case frag: Frag => flattenNodes(frag.nodes)
        case _: Node => Seq(n)
      res ++ _ns
    }

  /**
   *
   * @param title title of the page, required.
   * @param nodes may be the combination of the following "top" elements head, body, script*, style*
   * @return
   */
  def toHeadBody(title: String, bodyAttrs: Seq[Attribute], charsetValue: String = "utf-8")(nodes: Seq[Node]): (Tag, Tag) = {
    val _nodes: Seq[Node] = flattenNodes(nodes)
    val tags = _nodes.collect { case t: Tag => t }
    val bodyNodes = _nodes diff tags
    val styles: Seq[Tag] = findTag("style", tags)
    val links: Seq[Tag] = findTag("link", tags)
    val headTags: Seq[Tag] = findTag("head", tags)
    val bodyTags: Seq[Tag] = findTag("body", tags)
    val scripts: Seq[Tag] = findTag("script", tags)
    val metas: Seq[Tag] = findTag("meta", tags)
    val _bodyContentTags: Seq[Tag] = tags diff (headTags ++ bodyTags ++ styles ++ links ++ scripts ++ metas)

    val (bodyContentTags, nestedLinks, nestedScripts, nestedStyles) = {
      val nestedTagList = List("script", "link", "style")
      val (remainingNodes, extractNodes) = div(_bodyContentTags).extractTags(nestedTagList.mkString(","))
      val (links, nonlinks) = extractNodes.partition(_.tagLabel == "link")
      val (scripts, styles) = nonlinks.partition(_.tagLabel == "script")
      (remainingNodes.nodes, links, scripts, styles)
    }

    val allLinks = (links ++ nestedLinks).distinct

    val charsetMeta = metas.find(tag => tag.attrs.exists(attr => attr.name == "charset")).getOrElse(meta(charset := charsetValue))
    val newhead = headTags.headOption.getOrElse(head).apply(charsetMeta, Tags2.title(title), metas.filterNot(_ == charsetMeta), allLinks, styles, nestedStyles)
    val newbody = bodyTags.headOption.getOrElse(body).apply(bodyAttrs, bodyNodes, bodyContentTags, scripts, nestedScripts)
    (newhead, newbody)
  }

  private def build(title: String, charsetValue: String)(nodes: Seq[Node]): HtmlPage =
    val (newhead, newbody) = toHeadBody(title, Nil, charsetValue)(nodes)
    HtmlPageImpl(newhead, newbody)

  case class HtmlPageImpl(head: Tag, body: Tag) extends HtmlPage

end HtmlPage

trait HtmlPage extends Tag:
  def head: Tag
  def body: Tag

  final override def using(nodes: Seq[Node]): HtmlPage =
    val tags = nodes.collect { case tag: Tag => tag }
    (for {
      newhead <- HtmlPage.findTag("head", tags).headOption
      newbody <- HtmlPage.findTag("body", tags).headOption
    } yield HtmlPage.HtmlPageImpl(newhead, newbody)).orNull

  override val tagLabel: String = ""

  override def attrs: Seq[Attribute] = body.nodes.collect { case attr: Attribute => attr }

  override def text: Seq[Text] = body.nodes.collect { case text: Text => text }

  override def content: Seq[Node] = Seq(head, body)

  def root: Tag = html(attrs, content)

  override def nodes: Seq[Node] = Seq(root)

  override def render: String = Seq(doctype, root).map(_.render).mkString

  override def addNodes(selectorString: String, newNodes: Node*): HtmlPage = Selectors.addNodes(selectorString, newNodes*)(this)

  override def deleteTags(selectorString: String, deleteAttrs: Attribute*): HtmlPage = Selectors.deleteTags(selectorString, deleteAttrs*)(this)

  override def replaceNodes(selectorString: String, newNodes: Node*): HtmlPage = Selectors.replaceNodes(selectorString, newNodes*)(this)

  override def addAsFirstChild(selectorString: String, newNodes: Node*): HtmlPage = Selectors.addAsFirstChild(selectorString, newNodes)(this)

  override def modifyAttribute(cssSelector: String, modAttr: Attribute, modFn: (Attribute) => Option[Attribute]): HtmlPage = Selectors.modifyAttribute(cssSelector, modAttr, modFn)(this)

  override def deleteFromAttribute(selectorString: String, attribute: Attribute): HtmlPage = Selectors.removeAttrTokens(selectorString, attribute)(this)

  def timestampId: String = java.lang.Long.toString(System.currentTimeMillis, 26)

  def jsModule(value: String): Tag = script(tpe := "module", value)
