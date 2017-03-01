"use strict";

var Testlib3p = {};

Testlib3p.hello = function() {
	return "Hello from a third-party lib";
};


if (typeof module !== "undefined") module.exports = (Object.keys(module.exports).length || typeof module.exports === "function") ? module.exports : Testlib3p;

window.Testlib3p = (typeof module !== "undefined" && module.exports) || Testlib3p;

window.testlib3p = typeof require == 'function' && require('testlib3p');
