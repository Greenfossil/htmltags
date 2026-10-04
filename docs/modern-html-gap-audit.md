# Modern-HTML coverage gap audit

Date: 2026-10-05
Status: **audit only** — no code change. Implementing any gap below is a
separate, future decision (each is a public API addition).

Verified against the source at `v1.4.0` (`Tags.scala`: 139 tag bindings;
`Attrs.scala`: 218 attribute bindings). "Absent" = no binding for the name.

## Missing tags

| Tag | Note |
|---|---|
| `search` | HTML `<search>` landmark container (2023). |
| `slot` | Web Components `<slot>` distribution outlet. (Note: `vuejs`/`htmx` may define their own `slot`; a core binding is separate.) |

## Missing attributes

### Popover API

| Attribute | Note |
|---|---|
| `popover` | `popover="auto\|manual\|hint"` — the Popover API. |
| `popovertarget` | id of the element the invoker toggles. |
| `popovertargetaction` | `toggle\|show\|hide`. |

### Invoker Commands API

| Attribute | Note |
|---|---|
| `command` | Invoker command (`show-modal`, `close`, `toggle-popover`, custom). |
| `commandfor` | id of the element the command targets. |

### Dialog / modality

| Attribute | Note |
|---|---|
| `inert` | `inert` boolean flag — make a subtree non-interactive. |

### Input hints

| Attribute | Note |
|---|---|
| `enterkeyhint` | `enter\|done\|go\|next\|previous\|search\|send`. |
| `inputmode` | virtual-keyboard hint (`numeric`, `email`, `tel`, …). |

### Performance / resource hints

| Attribute | Note |
|---|---|
| `fetchpriority` | `high\|low\|auto` on `img`/`link`/`script`. |
| `decoding` | `sync\|async\|auto` on `img`. |

### Shadow DOM declarative

| Attribute | Note |
|---|---|
| `shadowrootmode` | `open\|closed` — declarative shadow root. |
| `shadowrootdelegatesfocus` | boolean; delegate focus into the shadow root. |

### Styling parts

| Attribute | Note |
|---|---|
| `part` | `part="…"` — exposes a shadow-DOM part for `::part()`. |

## Present — no action (checked during the audit)

Tags: `dialog`, `template`, `details`, `summary`, `picture`, `menu`, `output`,
`progress`, `meter`.

Attributes: `loading`, `srcset`, `sizes`, `translate`.

## Scope

This document records coverage only. It proposes no implementation order and
changes no code; adding any item above is a future unit with its own golden and
its own version decision (`early-semver` — the `htmltags` version moves, not this
audit).
