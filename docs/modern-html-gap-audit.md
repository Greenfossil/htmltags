# Modern-HTML coverage gap audit

Date: 2026-10-05
Status: **implemented 2026-10-19** — the gaps below are now public bindings with
goldens in `ModernHtmlSuite`. The original audit remains the rationale.

Verified against the source at `v1.4.0`; additions landed after that baseline.

## Implemented tags

| Tag | Note |
|---|---|
| `search` | HTML `<search>` landmark container (2023). |
| `slot` | Web Components `<slot>` distribution outlet. Its attribute is `Attrs2.slot` (it would collide with the tag name). |

## Implemented attributes

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
| `part` | `part="…"` — exposes a shadow-DOM part for `::part()` (implemented). |

## Present — no action (checked during the audit)

Tags: `dialog`, `template`, `details`, `summary`, `picture`, `menu`, `output`,
`progress`, `meter`.

Attributes: `loading`, `srcset`, `sizes`, `translate`.

## Scope

The bindings are implemented and covered by `ModernHtmlSuite`. Publishing them
requires a minor version bump under `early-semver`; this document records the
coverage rationale, not the release version.
