# 4.0.0 steps

- class capturing
- info
- document
- formatter
- compiler

## capture

- raw class fetching
- event listening, in mixin package
- binding event
- cache IO
- recipe holders, info or capture?

## info

- class walker
    - should add special handling for JS obj
- mostly data class, immutable, like `record`
- (maybe)no logic, except for class walker

## document

- info -> doc, as base
- read external doc
  - Token based? TokenStream?
  - currently using line as an element, not flexible enough
  - but a full parser for `.d.ts` seems overkill
- parse external doc and add into base
  - there should be a way of manually creating doc element, e.g. class, namespace, method
- apply generics
  - attached type params, `{[x in string]: DocType}`
- method/field filtering

## formatter

just formatter, quite obvious :)

## compiler


