# htmltags

![](https://img.shields.io/github/actions/workflow/status/Greenfossil/htmltags/run-tests.yml?branch=master)
![](https://img.shields.io/github/license/Greenfossil/htmltags)
![](https://img.shields.io/github/v/tag/Greenfossil/htmltags)
![Maven Central](https://img.shields.io/maven-central/v/com.greenfossil/htmltags_3)
[![javadoc](https://javadoc.io/badge2/com.greenfossil/htmltags_3/javadoc.svg)](https://javadoc.io/doc/com.greenfossil/htmltags_3)

This is the official Greenfossil Scala templating engine.

## How to Build

This library uses sbt as its build tool. It requires at least Java 17 or later to build.

Follow the official guide on how to install [sbt](https://www.scala-sbt.org/download.html).

## Getting Started

### Generating HTML elements with Scala code

You can simply construct HTML fragments like this:
```scala  
val frag = html(
  body(
    h1("My Page Header"),
    div(
      p("My favourite food:"),
      ul(
        li("Scrambled eggs"),
        li("Sausage"),
        li("Tater tots")
      )
    )
  )
)

println(frag.render)
```  

Result:
```html
<html>
    <body>
        <h1>My Page Header</h1>
        <div>
            <p>My favourite food:</p>
            <ul>
                <li>Scrambled eggs</li>
                <li>Sausage</li>
                <li>Tater tots</li>
            </ul>
        </div>
    </body>
</html>
```

You can add in attributes as well:
```scala  
val frag = html(
  head(
    title:="My Page"
  ),
  body(
    h1(cls:="header", "My Page Header"),
    div(cls:="food-list")(
      p("My favourite food:"),
      ul(
        li("Scrambled eggs"),
        li("Sausage"),
        li("Tater tots")
      )
    ),
    a(href:="https://github.com/", "Github")
  )
)

println(frag.render)
```

Result:
```html
<html>
    <head title="My Page"></head>
    <body>
        <h1 class="header">My Page Header</h1>
        <div class="food-list">
            <p>My favourite food:</p>
            <ul>
                <li>Scrambled eggs</li>
                <li>Sausage</li>
                <li>Tater tots</li>
            </ul>
        </div>
        <a href="https://github.com/">Github</a>
    </body>
</html>
```

Inline styling can be added into our fragment:
```scala
val frag = html(
  body(
    h1(backgroundColor:="blue", "My Page Header"),
    div(borderStyle:="solid")(
      p(fontWeight:="bold", fontSize:=20.pt, "My favourite food:"),
      ul(
        li("Scrambled eggs"),
        li("Sausage"),
        li("Tater tots")
      )
    )
  )
)

println(frag.render)
```

Result:
```html
<html>
  <body>
    <h1 style="background-color: blue;">My Page Header</h1>
    <div style="border-style: solid;">
      <p style="font-weight: bold; font-size: 20pt;">My favourite food:</p>
      <ul>
        <li>Scrambled eggs</li>
        <li>Sausage</li>
        <li>Tater tots</li>
      </ul>
    </div>
  </body>
</html>
```

### CSS Selectors

It is possible to use CSS selectors to query your HTML fragment in scala
```scala
val frag = div(
    p("1st paragraph"),
    p("2nd paragraph"),
    p("3rd paragraph"),
    p("4th paragraph")
)

println("First <p> child element: " + frag.cssQuery("p:first-child").render)
println("Last <p> child element: " + frag.cssQuery("p:last-child").render)
```

Result:
```
First <p> child element: <p>1st paragraph</p>
Last <p> child element: <p>4th paragraph</p>
```

We can then use the selectors to modify, add or remove elements from our fragment

Adding a new element:
```scala
val frag = div(
  div(cls:="target"),
  p("1st paragraph"),
  p("2nd paragraph"),
  p("3rd paragraph"),
  p("4th paragraph")
)

println(frag.addNodes("div.target", p("New paragraph in nested div")).render)
```

Result:
```html
<div>
  <div class="target">
    <p>New paragraph in nested div</p>
  </div>
  <p>1st paragraph</p>
  <p>2nd paragraph</p>
  <p>3rd paragraph</p>
  <p>4th paragraph</p>
</div>
```

Modifying existing element:
```scala
val frag = div(
  p("1st paragraph"),
  p("2nd paragraph"),
  p("3rd paragraph"),
  p("4th paragraph")
)

println(frag.addNodes("p:nth-child(even)", color:="red").render)
```

Result:
```html
<div>
  <p>1st paragraph</p>
  <p style="color: red;">2nd paragraph</p>
  <p>3rd paragraph</p>
  <p style="color: red;">4th paragraph</p>
</div>
```

Modifying an attribute:
```scala
val frag = div(
  p(cls:="my first paragraph", "1st paragraph"),
  p("2nd paragraph"),
  p("3rd paragraph"),
  p("4th paragraph")
)

def modFn(attr: Attribute): Option[Attribute] = Option(attr.findAndReplaceValue("my", ""))
val res = frag.modifyAttribute(".paragraph", cls, modFn)
println(res.render)
```

Result:
```html
<div>
  <p class="first paragraph">1st paragraph</p>
  <p>2nd paragraph</p>
  <p>3rd paragraph</p>
  <p>4th paragraph</p>
</div>
```

Removing an element:
```scala
val frag = div(
  p("1st paragraph"),
  p("2nd paragraph"),
  p(id:="third-paragraph", "3rd paragraph"),
  p("4th paragraph")
)

println(frag.deleteTags("#third-paragraph").render)
```

Result:
```html
<div>
  <p>1st paragraph</p>
  <p>2nd paragraph</p>
  <p>4th paragraph</p>
</div>
```

## Advanced Features

### Embedded styles
```scala
val styles = embeddedStyle(
  """
    | .header {
    |   background-color: black;
    |   color: white;
    | }
    |""".stripMargin)

val frag = div(
  styles,
  h1(cls:="header", "My Header"),
  p("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer suscipit, nulla sed vestibulum porttitor.")
)

println(frag.render)
```

Result:
```html
<div>
  <style>
    .header {
      background-color: black;
      color: white;
    }
  </style>
  <h1 class="header">My Header</h1>
  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer suscipit, nulla sed vestibulum porttitor.</p>
</div>
```

### Conditional rendering
```scala
val frag = div(
  div(
    h3("Will only render paragraph 3, 4 and 5"),
    1 to 5 map { index =>
      ifTrue(index > 2, p("Paragraph ", index))
    }
  ),

  div(
    h3("Will only render paragraph 1 and 2"),
    1 to 5 map { index =>
      ifFalse(index > 2, p("Paragraph ", index))
    }
  )
)

println(frag.render)
```

Result:
```html
<div>
  <div>
    <h3>Will only render paragraph 3, 4 and 5</h3>
    <p>Paragraph 3</p>
    <p>Paragraph 4</p>
    <p>Paragraph 5</p>
  </div>
  <div>
    <h3>Will only render paragraph 1 and 2</h3>
    <p>Paragraph 1</p>
    <p>Paragraph 2</p>
  </div>
</div>
```

### Automatic handling of different data types
```scala
case class UserProfile(firstname: String, lastname: String, age: Int, dob: LocalDate, gender: String, emailOpt: Option[String])

val users = List(
  UserProfile("Cyrus", "Albright", 30, LocalDate.now.minusYears(30), null, None),
  UserProfile("Tressa", "Colzione", 18, LocalDate.now.minusYears(18), "Female", Some("tressa@octo.path"))
)

val frag = div(
  users.zipWithIndex.map{ (user, index) => List(
    h2("User ", index + 1),
    ul(
      li(strong("First Name: "), user.firstname),
      li(strong("Last Name: "), user.lastname),
      li(strong("Birthday: "), user.dob),
      li(strong("Gender: "), user.gender),
      li(strong("Contact: "), user.emailOpt),
    )
  )
  }
)

println(frag.render)
```

Result:
```html
<div>
    <h2>User 1</h2>
    <ul>
        <li>
            <strong>First Name: </strong>Cyrus
        </li>
        <li>
            <strong>Last Name: </strong>Albright
        </li>
        <li>
            <strong>Birthday: </strong>1992-12-02
        </li>
        <li>
            <strong>Gender: </strong>
        </li>
        <li>
            <strong>Contact: </strong>
        </li>
    </ul>
    <h2>User 2</h2>
    <ul>
        <li>
            <strong>First Name: </strong>Tressa
        </li>
        <li>
            <strong>Last Name: </strong>Colzione
        </li>
        <li>
            <strong>Birthday: </strong>2004-12-02
        </li>
        <li>
            <strong>Gender: </strong>Female
        </li>
        <li>
            <strong>Contact: </strong>tressa@octo.path
        </li>
    </ul>
</div>
```

### Preventing Cross Site Scripting (XSS)
```scala
val frag = div(
  p("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer suscipit, nulla sed vestibulum porttitor."),
  p("<script>alert('Hello World');</script>")
)

println(frag.render)
```

Result:
```
<div>
  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer suscipit, nulla sed vestibulum porttitor.</p>
  <p>&lt;script&gt;alert(&apos;Hello World&apos;);&lt;&#x2F;script&gt;</p>
</div>
```

To render without escaping special characters, use the `raw()` method
```scala
val rawHtml =
  """
    |<div>
    |  <p>1st paragraph</p>
    |  <p>2nd paragraph</p>
    |  <script>some script</script>
    |</div>
    |""".stripMargin

println(raw(rawHtml).render)
```

Result:
```html
<div>
  <p>1st paragraph</p>
  <p>2nd paragraph</p>
  <script>
    some script
  </script>
</div>
```

### Using data attributes
```scala
val frag = div(
    div(data.placeholder := "hello world"),
    div(data.placeholder := 100),
    div(data.placeholder := true),
    div(data.placeholder := Option("hello world")),
    div(data.placeholder := null)
)

println(frag.render)
```

Result:
```html
<div>
  <div data-placeholder="hello world"></div>
  <div data-placeholder="100"></div>
  <div data-placeholder="true"></div>
  <div data-placeholder="hello world"></div>
  <div data-placeholder=""></div>
</div>
```

## License

htmltags is licensed under the Apache license version 2.
See [LICENSE](LICENSE.txt).
