# htmltags — agent notes

The Greenfossil Scala 3 HTML templating engine: typed tag/attribute builders that
render HTML server-side. This is the **root of the web stack** — many libraries
and apps depend on it, so its public surface and dependency footprint are
deliberately conservative.

## Build & verification

- Compile: `sbt compile`
- Test: `sbt test` (munit; the suite is small and the full run is fine)
- The repo is small; goldens use `assertNoDiff` (munit) on `.render`.

## Dependency policy

**Runtime dependencies stay minimal.** They are exactly:

- `scala-parser-combinators` — the CSS-selector parser
- `slf4j-api` — one logger (only `logback-classic` is `Provided`, for tests)

**JSON is a binding-library concern — do NOT add `commons-json` / Jackson here.**
`commons-json` pulls `jackson-databind`, which must stay in lock-step with
Armeria downstream; that pin must not leak into every `htmltags` consumer through
a second resolution path. A JSON-valued attribute (e.g. htmx's `hx-vals`) is
implemented by the *binding* library as an `AttributeValueLike` (see below), not
by adding a JSON type to the core union. Keep this file dependency-clean.

`ThisBuild / versionScheme := Some("early-semver")` — bump accordingly; a
dependency addition is a breaking change for the policy even if SemVer-legal.

## Adding a tag / attribute

Tags (`Tags.scala`):

```scala
val div = PairedTag("div")          // <div>…</div>
val br  = UnPairedTag("br")         // <br/> (self-closing when empty)
val meta = StartTag("meta")         // <meta> (never has content)
```

Add to `Tags.scala` (or `Tags2` for `title`/`style`), grouped with its section,
matching the surrounding style.

Attributes (`Attrs.scala`):

```scala
val lang = attr("lang")             // a plain HTML attribute
val style = cssattr("style")        // a CSS attribute → folded into style="…"
```

Add to `Attrs.scala` (or `Attrs2` for names that collide with tags). A dynamic
namespace is an `object … extends Dynamic`
that emits `prefix-<suffix>` (`aria`, `data`, `xmlns` in `Node.scala`).

Attributes are statically typed `Attribute` values; the binding fixes the HTML
name, and call sites use `:=` rather than an attribute-name string.

### Attribute-name casing

- **Hyphenated HTML attributes and CSS properties use camelCase Scala names**,
  with the hyphen kept in the emitted attribute/property:
  `val httpEquiv = attr("http-equiv")`, `val acceptCharset =
  attr("accept-charset")`, `val borderColor = cssattr("border-color")` (CSS
  folds into `style="…"`).
- **HTML attributes with no hyphen stay lowercase** (`colspan`, `rowspan`);
  **SVG attributes keep their spec casing** (`viewBox`, `preserveAspectRatio`).
- **ARIA names are the exception**: the `aria-*` suffix is a single lowercase
  token, so typed names mirror it exactly (`aria.labelledby`, not
  `aria.labelledBy`).
- **Dynamic `data.*` / `aria.*` suffixes are appended verbatim**; use nested
  dynamic names for `data-*` hyphens (`data.request.id` → `data-request-id`).

Every new tag/attr is a public API addition — add a golden to the relevant suite
(`TagSuite`, `DataAttributeSuite`, `AttributeValueLikeSuite`, …).

## Escaping rules (the trap)

- **Text nodes** escape six characters on render:
  `"`→`&quot;`, `'`→`&apos;`, `<`→`&lt;`, `>`→`&gt;`, `&`→`&amp;`, `/`→`&#x2F;`.
- **Default attribute values** escape only the quote characters —
  `"`→`&quot;`, `'`→`&apos;`; `<`, `>`, `&`, `/` pass through verbatim
  (e.g. `hx-vals="{&quot;h&quot;:&quot;a<b>&c&quot;}"`).
- `attr(name, renderValueAsEscape = false)` opts an attribute out of escaping.
- **`RawText` bypasses escaping entirely** — `raw("…")` (or the
  `raw(text)` helper) injects its string verbatim. That is the XSS trap: only
  wrap trusted, self-produced markup. `RawText` is also how a binding library
  emits already-correct content (e.g. JSON that must not be HTML-escaped twice).

## The `:=` value lattice

`AttributeValueType` (in `Extensions.scala`) is the accepted RHS of `attr := _`:

```
String | Int | Long | Double | Float | BigDecimal | Boolean | CSSUnit | RawText
  | AttributeValueLike
  | Option[scalar] | Seq[scalar]
```

`AttributeValueLike` is the **sanctioned extension seam**: a third-party value
type implements `def render: String` and is then assignable with the ordinary
`:=` (see `AttributeValueLikeSuite`). A binding library can carry a domain type
through this seam without touching the core — htmx's `HxJsonAttribute` (a
JSON-valued attribute) is the reference implementation.

## Changelog rule

Every landed change is recorded in the commit that lands it; the git history is
the serialized source of truth. No retroactive reconstruction.
