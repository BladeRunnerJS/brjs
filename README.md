---
layout: main
permalink: /index.html
title: browser-modules
---

<script type="text/javascript" src="browser-modules.js"></script>
<script type="text/javascript" src="install.js"></script>

browser-modules
===============

This provides a relatively simple define/require that can be used in the browser.  It's close
to commonJS compliant, and also allows the node style extension - module.exports = ...

While it looks a little like the 'commonJS' wrapping described in the RequireJS documentation,
it's synchronous.  Everything that is required must have been previously defined or an error
will be thrown.

There's a decent chance if you're looking at this that your needs might be better served by
browserify.  Check it out if you haven't already.

There are some extra features such as hierarchical realms, but to get started with the basics,
include the browser-modules.js file and then call realm.install().

Warning: having a global 'define' method may be enough to trigger AMD definitions in some UMD
modules. They should really check for define.AMD, but not all of them do. This will work some
of the time, but this code is really designed to work with commonJS and node style modules. If
this is likely to be a problem, you might want to avoid calling .install().
