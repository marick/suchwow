## Such Wow

The [API documentation index](http://marick.github.io/suchwow) gives an overview of what this library offers.

Snippets from serious work, wrapped in a whimsical container. A bit of a tribute to the spirit of [_why](http://en.wikipedia.org/wiki/Why_the_lucky_stiff), but with a [Shiba Inu](http://en.wikipedia.org/wiki/Shiba_Inu) instead of [foxes](http://mislav.uniqpath.com/poignant-guide/images/the.foxes-3.png).

Intended audience:
* those who don't want to use `(if (seq ...))` and don't care *who* knows it.
* those who think `:use` has its virtues.
* those disinclined to remember which functions take namespaces, which take symbols, and which take both 
* those willing to devote precious lines on their [VT100](http://en.wikipedia.org/wiki/VT100) to examples in docstrings.

![By Euterpia (Own work, CC0), via Wikimedia Commons](http://upload.wikimedia.org/wikipedia/commons/thumb/d/df/Doge_homemade_meme.jpg/256px-Doge_homemade_meme.jpg)     
[via Euterpia](http://commons.wikimedia.org/wiki/File:Doge_homemade_meme.jpg)

[![Build Status](https://travis-ci.org/marick/suchwow.png?branch=master)](https://travis-ci.org/marick/suchwow)

## Such Usage

Available via [clojars](http://clojars.org/search?q=suchwow) for Clojure 1.4+  
For lein: [marick/suchwow "0.2.0"]     

[Much API doc](http://marick.github.io/suchwow/)

The functions are packaged so that you can do the usual `(:require
[such.types :as wow])` thing. Alternately, you can compose them into
your own private addition to `clojure.core`, whereupon you can adorn
any namespace declaration with:

    (:use marick.clojure.core) ; yes: `use`. Glorious, terse `use`.

The file [`test/such/clojure/core.clj`](https://github.com/marick/suchwow/blob/master/test/such/clojure/core.clj) shows how to arrange for that to work.


## Such License

This software is covered by the [Unlicense](http://unlicense.org/)
and, as such, is in the public domain.

## Such Contributing

Pull requests accepted, provided:

1. Your contribution has tests. In keeping with the spirit of the library, they
   don't even have to be written with
   [Midje](https://github.com/marick/Midje), since Midje can run
   clojure.test tests.

2. Your contribution doesn't depend on anything other than Clojure itself.

3. You have the right to put your contribution into the public domain.

    To allow me to be a teensy bit scrupulous, please include the following text in
    the comment of your pull request:

    > I dedicate any and all copyright interest in this software to the
    > public domain. I make this dedication for the benefit of the public at
    > large and to the detriment of my heirs and successors. I intend this
    > dedication to be an overt act of relinquishment in perpetuity of all
    > present and future rights to this software under copyright law.

