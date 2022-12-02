# htmltags

![](https://img.shields.io/github/workflow/status/Greenfossil/htmltags/Run%20tests)
![](https://img.shields.io/github/license/Greenfossil/htmltags)
![](https://img.shields.io/github/v/tag/Greenfossil/htmltags)

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

The result will be:
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

Which will generate:
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

The result will be:
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

We will get:
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

We will get:
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

Output:
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

Output:
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

Output:
```html
<div>
  <p>1st paragraph</p>
  <p>2nd paragraph</p>
  <p>4th paragraph</p>
</div>
```

## License

htmltags is licensed under the Apache license version 2.
See [LICENSE](LICENSE.txt).
