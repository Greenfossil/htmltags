package com.greenfossil.htmltags

import org.w3c.dom.Document
import org.xml.sax.InputSource

import java.io.{StringReader, StringWriter}
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{OutputKeys, TransformerFactory}

class XMLPrettyPrintSuite extends munit.FunSuite {

  def stringToDocument(s: String): Document =
    val df = DocumentBuilderFactory.newDefaultInstance()
    val builder = df.newDocumentBuilder()
    builder.parse(InputSource(StringReader(s)))

  def documentToString(doc: Document, indent: Int = 2, omitDeclaration: Boolean = true): String =
    val xf = TransformerFactory.newDefaultInstance().newTransformer()
    if indent > 0 then { //PrettyPrint
      xf.setOutputProperty(OutputKeys.INDENT, "yes")
      xf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indent.toString)
    }
    if omitDeclaration then {
      xf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }
    val writer = StringWriter()
    xf.transform(DOMSource(doc), StreamResult(writer))
    writer.getBuffer.toString
  
  val xml = """<Company><Department name="Sales"><Employee name="John Smith"/><Employee name="Tim Dellor"/></Department></Company>""".stripMargin

  test("XMLPrettyPrint") {
    val doc = stringToDocument(xml)
    println(s"doc = ${doc}")
    val s = documentToString(doc)
    println(s"s = ${s}")
  }

}
