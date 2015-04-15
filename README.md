---
layout: main
permalink: /index.html
title: topiarist
---

<script type="text/javascript" src="lib/topiarist.js">
</script>

Topiarist
=======

Topiarist provides tree and shape-based type verification for JavaScript.

[![Build Status](https://travis-ci.org/BladeRunnerJS/topiarist.png)](https://travis-ci.org/BladeRunnerJS/topiarist)

Details
-------

You can see the main page [here](http://BladeRunnerJS.github.io/topiarist/), or the git repository [here](https://github.com/BladeRunnerJS/topiarist).
The specification is [here](https://github.com/BladeRunnerJS/topiarist/tree/gh-pages/spec).
Actual js file is at <https://github.com/BladeRunnerJS/topiarist/blob/gh-pages/lib/topiarist.js>.

You can also read the [introductory blog post](http://bladerunnerjs.org/blog/topiarist/).

This page has the library loaded so you can experiment by opening up a console.


A Note on implementation
------------------------

This library makes liberal use of nonenumerable attributes and Object.getPrototypeOf.
It is therefore suitable only for ecmascript 5 engines.  It will work in ecmascript 6
engines but there would be a much nicer implementation in that case, using Map and
private symbols.


Usage
-----

This library provides the following action methods:

* `topiarist.extend(subclass, superclass)` which implements classical single inheritance by setting up the prototype chain.
* `topiarist.implement(class, interface)` which declares a classes intention to implement an interface, where verification is delayed until after the class has been finalized.
* `topiarist.hasImplemented(class, interface)` which declares that a class implements an interface and throws an exception if it does not.
* `topiarist.inherit(class, parent)` which provides multiple inheritance by copying functionality from the parent to the class.
* `topiarist.mixin(class, mixin)` which provides mixin inheritance, sandboxing mixin methods that are copied onto the class.

And the following query methods:

* `topiarist.isA(instance, parent)` which returns true if the instance is of a type which has been declared to be descended from the parent, e.g. because it's extended or implemented or mixed-in.
* `topiarist.classIsA(class, parent)` which returns true if the class has been declared to be descended from the parent, e.g. through extension, implementation, etc.
* `topiarist.fulfills(instance, interface)` which returns true if the instance supports everything on the interface.
* `topiarist.classFulfills(class, interface)` which returns true if instances of the class will be created supporting everything on the interface.

The following convenience methods are also provided:

* `topiarist.install()` which copies the appropriate methods onto the Function and Object prototype, renaming them where appropriate.  This lets you do things like `Subclass.extends(Superclass)`
* `topiarist.exportTo()` which copies the appropriate methods onto the global object so they can be accessed directly.
