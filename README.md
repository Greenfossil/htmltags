# htmltags

![](https://github.com/Greenfossil/htmltags/actions/workflows/run-tests.yml/badge.svg)
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

## License

htmltags is licensed under the Apache license version 2.
See [LICENSE](LICENSE.txt).
