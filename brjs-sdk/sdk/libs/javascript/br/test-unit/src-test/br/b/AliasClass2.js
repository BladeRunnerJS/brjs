var br = require('br/Core');
var Alias2Interface = require('br/Alias2Interface');

function AliasClass2() {
};

AliasClass2.prototype.interfaceFunction2 = function() {
};

br.hasImplemented(AliasClass2, Alias2Interface);

module.exports = AliasClass2;
