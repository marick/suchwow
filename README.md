## Such Wow

[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)
[![Current Version](https://img.shields.io/clojars/v/marick/suchwow.svg)](https://clojars.org/marick/suchwow)

The [API documentation index](http://marick.github.io/suchwow) gives an overview of what this library offers.

Snippets from serious work, wrapped in a whimsical container. A bit of a tribute to the spirit of [_why](http://en.wikipedia.org/wiki/Why_the_lucky_stiff), but with a [Shiba Inu](http://en.wikipedia.org/wiki/Shiba_Inu) instead of [foxes](http://mislav.uniqpath.com/poignant-guide/images/the.foxes-3.png).

This package offers three types of functions:
* `clojure.core` functions, but with better documentation (including examples).
* Variants of `clojure.core` functions that accept more kinds of inputs.
* A grab-bag of useful functions that, importantly, you can copy into your own code without worrying about licenses or giving credit or any of that. As a programmer trying to get work done, I use this library and others to create a ["favorite functions" namespace](https://github.com/marick/clojure-commons/blob/master/src/commons/clojure/core.clj) that I `use` everywhere.

![By Euterpia (Own work, CC0), via Wikimedia Commons](http://upload.wikimedia.org/wikipedia/commons/thumb/d/df/Doge_homemade_meme.jpg/256px-Doge_homemade_meme.jpg)     
[via Euterpia](http://commons.wikimedia.org/wiki/File:Doge_homemade_meme.jpg)

[![Build Status](https://travis-ci.org/marick/suchwow.png?branch=master)](https://travis-ci.org/marick/suchwow)

## Such Usage

Available via [clojars](https://clojars.org/marick/suchwow) for Clojure 1.7+  
For lein: [marick/suchwow "6.0.0"]

[Much API doc](http://marick.github.io/suchwow/)

Copy the source if you want, do the normal `(:require
[such.types :as wow])` thing, or create your own `commons.clojure.core`
namespace with all the things you think should be packaged with Clojure. 

The files [test/such/clojure/f_immigration.clj](https://github.com/marick/suchwow/blob/master/test/such/f_immigration.clj) and [commons.clojure.core](https://github.com/marick/clojure-commons/blob/master/src/commons/clojure/core.clj) show how to arrange for that last.


## Such License

This software is covered by the [Unlicense](http://unlicense.org/)
and, as such, is in the public domain.

## Such Contributors

* Alex Miller
* Bahadir Cambel
* Børge Svingen
* Brian Marick

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

