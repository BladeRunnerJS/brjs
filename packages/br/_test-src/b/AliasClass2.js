require('br-presenter/_resources-test-at/html/test-form.html');
var br = require('br/Core');
var Alias2Interface = require('br/_test-src/Alias2Interface');

function AliasClass2() {
};

AliasClass2.prototype.interfaceFunction2 = function() {
};

br.hasImplemented(AliasClass2, Alias2Interface);

module.exports = AliasClass2;
