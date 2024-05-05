package com.greenfossil.htmltags

class XMLPrettyPrintSuite extends munit.FunSuite {
  
  val xml = """<Company><Department name="Sales"><Employee name="John Smith"/><Employee name="Tim Dellor"/></Department></Company>""".stripMargin

  test("XMLPrettyPrint") {
    val doc = xml.toDom //Convert string to Document
    assertNoDiff(doc.print, xml)
    assertNoDiff(doc.prettyPrint, """|<Company>
                                     |  <Department name="Sales">
                                     |    <Employee name="John Smith"/>
                                     |    <Employee name="Tim Dellor"/>
                                     |  </Department>
                                     |</Company>
                                     |""".stripMargin)
  }

}
