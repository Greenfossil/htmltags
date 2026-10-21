# htmltags Changelog

## 2026-10-21 — feat: add `HtmlPage` document builder

- Added `com.greenfossil.htmltags.HtmlPage` (moved from elementum-web without
  behavioural changes): title/charset, head/body extraction, top-level and
  nested `link`/`style`/`script` hoisting, body attributes, `Frag` flattening,
  selector mutations, and doctype rendering.
- Added `HtmlPageSuite` (5 tests) covering the document contract.
- The legacy `com.greenfossil.elementum.web.HtmlPage` remains untouched for
  existing consumers.
- Released as `1.4.1-RC2`.
