(ns mblog.content-design
  (:require
   [mblog.indigo :as forside]
   [mblog.samvirk :as farger]
   [clojure.string :as str]))

(def md
  (-> "
  # Markdown syntax guide

## Headers

# This is a Heading h1
## This is a Heading h2
###### This is a Heading h6

## Emphasis

*This text will be italic*
_This will also be italic_

**This text will be bold**
__This will also be bold__

_You **can** combine them_

## Lists

### Unordered

* Item 1
* Item 2
* Item 2a
* Item 2b
    * Item 3a
    * Item 3b

### Ordered

1. Item 1
2. Item 2
3. Item 3
    1. Item 3a
    2. Item 3b

## Images

![This is an alt text.](https://cdn.prod.website-files.com/646b604540d8abf1ef67ff82/690897327ba5159e4357f716_Neno-p-800.jpg \"This is a sample image.\")

## Links

You may be using [Mikrobloggeriet](https://mikrobloggeriet.no/).

## Blockquotes

> Markdown is a lightweight markup language with plain-text-formatting syntax, created in 2004 by John Gruber with Aaron Swartz.
>
>> Markdown is often used to format readme files, for writing messages in online discussion forums, and to create rich text using a plain text editor.

## Tables

| Left columns  | Right columns |
| ------------- |:-------------:|
| left foo      | right foo     |
| left bar      | right bar     |
| left baz      | right baz     |

## Blocks of code

```
let message = 'Hello world';
alert(message);
```

## Inline code

This web site is using `markedjs/marked`.

       "
      (str/trim)))

(defn req->innhold [req]
  (-> {:docs [{:db/id 17592186045464, :doc/created "2025-11-20T18:51:46Z", :doc/slug "leik-13", :doc/markdown md}]
       :cohorts [#:db{:id 17592186045425}]}
      (assoc :samvirk (farger/load))))


(defn innhold->hiccup [innhold]
  (forside/innhold->hiccup innhold))
